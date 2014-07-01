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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
/**
 * This class splits a path based on a delimiter into a Vector of Strings
 * that represent each folder along the chain.
 * @author nhgh
 */
public class PathSplitter implements Serializable
{
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


    ArrayList<String>pathElements = new ArrayList<>();
    
    /** Current walk position */
    private int walkPosition = 0;
    
    /** Last path item */
    private String lastItem;
    
    /** Creates a new instance of PathSplitter */
    public PathSplitter(String path) {
        splitPath(path, "/");
    }
    
    /** Split with a custom delimiter */
    public PathSplitter(String path, String delim) {
        splitPath(path, delim);
    }
    
    /** Split the path using a StringTokenizer */
    private void splitPath(String path, String delim){
        StringTokenizer tokens = new StringTokenizer(path, delim);
        pathElements.clear();
        String token = null;
        while(tokens.hasMoreElements()){
            token = tokens.nextToken();
            pathElements.add(token);
        }
        lastItem = token;
    }
    
    /** Get all of the elements */
    public List<String>getPathElements(){
        return pathElements;
    }
    
    /** Reset the walk index */
    public void resetWalk(){
        walkPosition = 0;
    }
    
    /** Get the next element in a walk through the list */
    public String nextElement(){
        if(walkPosition<pathElements.size()){
            String value = pathElements.get(walkPosition);
            walkPosition++;
            return value;
        } else {
            return null;
        }
    }
    
    /** Are there any elements left */
    public boolean hasNextElement(){
        if(walkPosition<pathElements.size()){
            return true;
        } else {
            return false;
        }
    }
    
    /** Get the last item */
    public String getLastItem(){
        return lastItem;
    }
    
    /** Return the path without the last element */
    public String buildPathWithoutLastElement(){
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<pathElements.size() - 1;i++){
            builder.append("/");
            builder.append(pathElements.get(i));
        }
        return builder.toString();
    }
}
