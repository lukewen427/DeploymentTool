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

/**
 * Provides an InputStream implementation that connects to an IRMIInputStream
 * server
 * @author hugo
 */
public class RMIInputStream extends InputStream {
    /** Current buffer of data */
    private byte[] buffer;

    /** Position within the data buffer */
    private int bufferPos = 0;

    /** RMI Data source */
    private IRMIInputStream server;

    /** Is the transfer complete */
    private boolean complete = false;

    public RMIInputStream(IRMIInputStream server) {
        this.server = server;
    }

    @Override
    public synchronized int read() throws IOException {
        if(!complete){
            if(buffer==null){
                // Need to fetch the next chunk
                int size = fetchNextChunk();
                if(size==0){
                    complete = true;
                    return -1;
                }
            }

            if(buffer!=null){
                if(!complete){
                    if(bufferPos<buffer.length){
                        int value = buffer[bufferPos];
                        bufferPos++;
                        return value;
                    } else {
                        complete = true;
                        return -1;
                    }
                    
                } else {
                    complete = true;
                    return -1;
                }
                
            } else {
                throw new IOException("No data buffer available");
            }
        } else {
            return -1;
        }
    }

    /** Fetch the next chunk of data. This method returns the size of the data buffer */
    private synchronized int fetchNextChunk() throws IOException {
        try {
            if(server!=null){
                if(!server.finished()){
                    buffer = server.next();
                    bufferPos = 0;
                    complete = false;
                    return buffer.length;
                } else {
                    complete = true;
                    server.close();
                    return 0;
                }
            } else {
                throw new Exception("Server not defined");
            }

        } catch (Exception e){
            throw new IOException("Error fetching chunk: " + e.getMessage(), e);
        }
    }
}