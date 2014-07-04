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
package com.connexience.server.workflow.cloud.execution;

import java.io.*;
import org.apache.log4j.*;

/**
 * This class just connects to an InputStream and dumps everything from it
 * @author hugo
 */
public class InputStreamDumper implements Runnable {
    private static Logger logger = Logger.getLogger(InputStreamDumper.class);
    /** Stream to read */
    private final InputStream stream;
    
    /** Buffer to keep output if required */
    private final OutputStream streamStore;

    /** Maximum number of characters to dump to the output stream. This is
     * used when storing results to memory for a workflow service invocation */
    private int maxDumpBytes = -1;

    /** Number of bytes currently stored */
    private int bytesStored = 0;

    /** Stop flag */
    private volatile boolean stopFlag = false;
    
    /** Construct with a stream */
    public InputStreamDumper(InputStream stream){
        this.stream = stream;
        this.streamStore = null;
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }
    
    /** Construct with an input and an output stream */
    public InputStreamDumper(InputStream stream, OutputStream streamStore){
        this.stream = stream;
        this.streamStore = streamStore;
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }

    /** Construct with an input stream and an output stream and a maximum
     * number of characters to store */
    public InputStreamDumper(InputStream stream, OutputStream streamStore, int maxDumpBytes) {
        this.stream = stream;
        this.streamStore = streamStore;
        this.maxDumpBytes = maxDumpBytes;
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }


    /** Read and discard input */
    public void run(){
        try {
            if(streamStore==null){
                byte[] buffer = new byte[4096];
                while(stream.read(buffer)!=-1 && stopFlag==false){    

                }
            } else {
                byte[] buffer = new byte[4096];
                int len;

                while((len=stream.read(buffer))!=-1 && stopFlag==false){    
                    if(maxDumpBytes<0 || bytesStored < maxDumpBytes){
                        streamStore.write(buffer, 0, len);
                        bytesStored = bytesStored + len;
                    }
                    streamStore.flush();
                }
                streamStore.flush();
            }
            
        } catch (Exception e){
            logger.error("Error reading stream: " + e.getMessage() + ": " + e.getMessage());
        }
    }
    
    /** Read the contents of a file into a string */
    public static String readFileIntoString(File file) throws IOException {
        if(file.exists() && file.canRead()){
            ByteArrayOutputStream tmp = new ByteArrayOutputStream((int)file.length());
            byte[] buffer = new byte[4096];
            int len = 0;
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(file));
            while((len = inStream.read(buffer))!=-1){
                tmp.write(buffer, 0, len);
            }
            inStream.close();
            tmp.flush();
            tmp.close();
            return new String(tmp.toByteArray());
        } else {
            throw new IOException("Cannot read file");
        }
    }
    
    public void stop(){
        stopFlag = true;
    }
}