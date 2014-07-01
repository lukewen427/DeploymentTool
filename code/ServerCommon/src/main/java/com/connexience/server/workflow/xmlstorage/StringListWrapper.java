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
package com.connexience.server.workflow.xmlstorage;
import org.pipeline.core.xmlstorage.*;

import java.util.*;
import java.io.*;

/**
 * This class provides an XmlStorable array of Strings
 * @author nhgh
 */
public class StringListWrapper implements XmlStorable, Serializable, Iterable<String> {
	private static final long serialVersionUID = 1L;

	/** String values */
    private ArrayList<String> values = new ArrayList<>();

    /** Get the size of this wrapper */
    public int getSize(){
        return values.size();
    }
    
    /** Get a value */
    public String getValue(int index){
        return values.get(index);
    }
    
    /** Add a value */
    public void add(String value){
        values.add(value);
    }
    
    /** Remove a value */
    public void remove(int index){
        values.remove(index);
    }

    /** Get a toString value */
    @Override
    public String toString() {
        return "List";
    }
    
    /** Return as a string array */
    public String[] toStringArray(){
        String[] list = new String[values.size()];
        for(int i=0;i<values.size();i++){
            list[i] = values.get(i);
        }
        return list;
    }
    
    /** Save this object to Xml */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("StringList");
        store.add("Size", values.size());
        for(int i=0;i<values.size();i++){
            store.add("Value" + i, values.get(i));
        }
        return store;
    }

    /** Recreate this object from Xml */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int size = store.intValue("Size", 0);
        values.clear();
        for(int i=0;i<size;i++){
            values.add(store.stringValue("Value" + i, ""));
        }
    }


    @Override
    public Iterator<String> iterator()
    {
        return new Itr();
    }
    
    private class Itr implements Iterator<String>
    {
        int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < values.size();
        }

        @Override
        public String next() {
            return values.get(cursor++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("StringListWrapper does not support removing");
        }
    }
}