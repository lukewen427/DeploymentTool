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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * This class provides an output stream pipe that can divert data to other
 * streams
 * @author hugo
 */
public class DivertingOutputStream extends OutputStream {
    /** Primary target stream */
    private OutputStream target;

    /** Extra output streams */
    private ArrayList<OutputStream> extraStreams = new ArrayList<>();
    
    public DivertingOutputStream(OutputStream target) {
        this.target = target;
    }

    @Override
    public void write(int i) throws IOException {
        target.write(i);
        for(OutputStream s:extraStreams){
            try {
                s.write(i);
            } catch (Exception e){}
        }
    }
    
    /** Add an output stream */
    public synchronized void addExtraStream(OutputStream stream){
        extraStreams.add(stream);
    }
    
    /** Remove an output stream */
    public synchronized void removeExtraStream(OutputStream stream){
        extraStreams.remove(stream);
    }
}