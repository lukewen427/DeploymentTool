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

package org.pipeline.core.xmlstorage.autopersist;

import org.pipeline.core.xmlstorage.XmlAutoStorable;
import org.pipeline.core.xmlstorage.XmlDataObjectFactory;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This class pulls out all of the storable parameters on an XmlStorable object.
 * It spawns new XmlStorablePersister objects for class variables that are 
 * themselves XmlStorable
 * @author  hugo
 */
public class XmlStorablePersister {
    /** Object being stored */
    private XmlAutoStorable object = null;
    
    /** Data store used to store parameters */
    private XmlDataStore store = null;
    
    /** Creates a new instance of XmlStorablePersister */
    public XmlStorablePersister(XmlDataStore store, XmlAutoStorable object) {
        this.store = store;
        this.object = object;
    }
    
    /** Persist data to store */
    public void persist() throws XmlStorageException, Exception {
        if(this.object!=null && this.store!=null){
            Field[] fields = this.object.getClass().getDeclaredFields();
            Field field;
            Class fieldType;
            String strFieldTypeName;
            
            // Save each individual field
            for(int i=0;i<fields.length;i++){
                field = fields[i];
                fieldType = field.getType();
                strFieldTypeName= fieldType.getName();
                field.setAccessible(true);
                
                if(field.getModifiers()!=Modifier.TRANSIENT){
                    
                    // Deal with primitive types
                    if(strFieldTypeName.equals("boolean")){
                        this.store.add(field.getName(), field.getBoolean(this.object));

                    } else if(strFieldTypeName.equals("double")){
                        this.store.add(field.getName(), field.getDouble(this.object));

                    } else if(strFieldTypeName.equals("int")){
                        this.store.add(field.getName(), field.getInt(this.object));

                    } else if(strFieldTypeName.equals("long")){
                        this.store.add(field.getName(), field.getLong(this.object));


                    } else {
                        // Deal with other type          
                        if(XmlDataObjectFactory.isDataTypeRecognised(fieldType)){
                            this.store.add(field.getName(), field.get(this.object));
                        }
                    }
                }                
            }
        } else {
            throw new XmlStorageException("No store or object defined");
        }
    }
}
