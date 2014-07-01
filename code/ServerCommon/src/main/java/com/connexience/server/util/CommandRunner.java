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
package com.connexience.server.util;

import java.io.*;

public class CommandRunner {

    private StreamConsumer sysErr, sysOut;
    private int exitCode;
    private volatile boolean commandStarted = false;
    
    public int run(String command) throws InterruptedException, IOException {
        commandStarted = false;
        Process process = Runtime.getRuntime().exec(command);
        commandStarted = true;
        sysErr = new StreamConsumer(process.getErrorStream());
        sysOut = new StreamConsumer(process.getInputStream());
        exitCode = process.waitFor();
        return exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean isCommandStarted() {
        return commandStarted;
    }
   

    public String sysErr() {
        return sysErr.toString();
    }

    public String sysOut() {
        return sysOut.toString();
    }

    class StreamConsumer implements Runnable {

        private InputStream inputStream;
        private StringWriter stringWriter = new StringWriter();
        private PrintWriter output = new PrintWriter(stringWriter, true);

        public StreamConsumer(InputStream inputStream) {
            this.inputStream = inputStream;
            new Thread(this).start();
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);
                while (true) {
                    String s = br.readLine();
                    if (s == null) {
                        break;
                    }
                    output.println(s);
                }
                inputStream.close();
                output.close();
            } catch (Exception e) {
                throw new RuntimeException("problems reading input stream", e);
            }
        }

        @Override
        public String toString() {
            return stringWriter.toString();
        }
    }
}
