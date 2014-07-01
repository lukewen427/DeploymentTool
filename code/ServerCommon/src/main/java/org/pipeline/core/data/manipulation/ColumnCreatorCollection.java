/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.data.manipulation;

import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class holds a collection of ColumnCreator objects that can be
 * used to create a new data set either from scratch or by processing
 * an existing set of data.
 * @author hugo
 */
public class ColumnCreatorCollection implements XmlStorable {
    /** Collection of column creators */
    private Vector creators = new Vector();
    
    /** Creates a new instance of ColumnCreatorCollection */
    public ColumnCreatorCollection() {
    }

    /** Return the number of column creators */
    public int size(){
        return creators.size();
    }
    
    /** Add a creator */
    public void addColumnCreator(ColumnCreator creator){
        if(!creators.contains(creator)){
            creators.addElement(creator);
        }
    }
    
    /** Get a creator */
    public ColumnCreator getColumnCreator(int index){
        return (ColumnCreator)creators.elementAt(index);
    }
    
    /** Delete a creator */
    public void deleteColumnCreator(int index){
        creators.remove(index);
    }
    
    /** Delete a creator */
    public void deleteColumnCreator(ColumnCreator creator){
        creators.remove(creator);
    }
    
    /** Create a set of data using the colunn creators */
    public Data createData() throws DataException {
        Data createdData = new Data();
        Enumeration e = creators.elements();
        ColumnCreator creator;
        while(e.hasMoreElements()){
            creator = (ColumnCreator)e.nextElement();
            createdData.addColumn(creator.createColumn());
        }
        return createdData;        
    }
    
    /** Create a set of data based upon an existing data set */
    public Data createData(Data data) throws DataException {
        Data createdData = new Data();
        Enumeration e = creators.elements();
        ColumnCreator creator;
        while(e.hasMoreElements()){
            creator = (ColumnCreator)e.nextElement();
            createdData.addColumn(creator.createColumn(data));
        }
        return createdData;
    }
    
    /** Get an Enumeration of all the creators */
    public Enumeration getColumnCreatorEnum(){
        return creators.elements();
    }
    
    /** Recreate from an XmlDataStore */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        creators.clear();
        int size = store.intValue("CreatorCount", 0);
        for(int i=0;i<size;i++){
            creators.addElement(store.xmlStorableValue("Creator" + i));
        }
    }

    /** Save to an XmlDataStore */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("ColumnCreatorCollection");
        int size = creators.size();
        store.add("CreatorCount", size);
        for(int i=0;i<size;i++){
            store.add("Creator" + i, ((XmlStorable)creators.elementAt(i)));
        }
        return store;
    }
}