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
package com.connexience.server.workflow.util;

import java.io.*;

/**
 * This class provides an InputStream that can be used to notify listeners
 * of data transfers.
 * @author hugo
 */
public class InstrumentedInputStream extends InputStream {
    /** Stream supplying data */
    private InputStream sourceStream;

    /** Total bytes transferred */
    private long bytesTransferred = 0;

    /** Listener object */
    private InstrumentedInputStreamListener listener;

    public InstrumentedInputStream(InputStream sourceStream, InstrumentedInputStreamListener listener) {
        this.sourceStream = sourceStream;
        this.listener = listener;
    }

    /** Set the listener */
    public void setInstrumentedInputStreamListener(InstrumentedInputStreamListener listener){
        this.listener = listener;
    }
    
    /** Get the total number of bytes read */
    public long getBytesTransferred(){
        return bytesTransferred;
    }

    @Override
    public int read() throws IOException {
        int bt = sourceStream.read();
        if(listener!=null && bt!=-1){
            listener.bytesRead(1);
        }

        if(bt!=-1){
            bytesTransferred++;
        }
        return bt;
    }
}