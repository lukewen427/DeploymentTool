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
 * This class dumps data from one stream to another
 * @author nhgh
 */
public class StreamDumper extends Thread {
    /** Source stream */
    private InputStream source;

    /** Target stream */
    private OutputStream target;

    public StreamDumper(InputStream source, OutputStream target) {
        this.source = source;
        this.target = target;
        setDaemon(true);
        start();
    }

    public void run(){
        int data;
        try {
            while((data = source.read())!=-1){
                target.write(data);
            }
            target.flush();
            
        } catch (IOException ioe){
            System.out.println("Stream dumper error: " + ioe.getMessage());
        }
    }
}