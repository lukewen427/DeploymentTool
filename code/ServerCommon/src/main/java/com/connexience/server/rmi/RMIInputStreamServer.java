/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class provides a server for an input stream that can transfer the data
 * via RMI
 * @author hugo
 */
public class RMIInputStreamServer extends UnicastRemoteObject implements IRMIInputStream {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    /** Source stream */
    private InputStream source;

    /** Is the stream readining finished */
    private boolean finished = false;

    public RMIInputStreamServer(InputStream source) throws RemoteException {
        this.source = source;
    }

    public boolean finished() throws RemoteException {
        return finished;
    }

    public byte[] next() throws RemoteException {
        if(source!=null){
            try {
                byte[] buffer = new byte[4096];
                int len = source.read(buffer);
                if(len!=-1){
                    if(len<buffer.length){
                        // Need to trim the buffer
                        byte[] trimmedBuffer = new byte[len];
                        for(int i=0;i<len;i++){
                            trimmedBuffer[i] = buffer[i];
                        }
                        return trimmedBuffer;
                    } else {
                        // Return full buffer
                        return buffer;
                    }
                } else {
                    finished = true;
                    source.close();
                    return null;
                }
            } catch (IOException e){
                throw new RemoteException("IO Error reading stream: " + e.getMessage(), e);
            }
        } else {
            throw new RemoteException("No source stream defined in server");
        }
    }

    public void close() throws RemoteException {
        if(source!=null){
            try {
                source.close();
            } catch (Exception e){}
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if(source!=null){
            try {
                source.close();
            } catch (Exception e){}
        }
    }
}