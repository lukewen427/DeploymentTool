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
package org.pipeline.core.xmlstorage.replacement;

import com.connexience.server.model.ServerObject;
import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.xmldatatypes.*;

import java.util.Date;
import java.util.Enumeration;
/**
 * This class replaces values in an XmlDataStore object using string values
 * contained in an XmlDataStore object.
 * @author hugo
 */
public class StringPropertiesReplacer {
    /** Data store being edited */
    private XmlDataStore store;
    
    /** Properties object containing replacements */
    private XmlDataStore replacementProperties ;

    public StringPropertiesReplacer(XmlDataStore store, XmlDataStore replacementProperties) {
        this.store = store;
        this.replacementProperties = replacementProperties;
    }
    
    public XmlDataStore replace() throws XmlStorageException {
        XmlDataStore results = (XmlDataStore)store.getCopy();
        
        Enumeration names = replacementProperties.getNames().elements();
        String name;
        String replacementValue;
        XmlDataObject originalValue;
        while(names.hasMoreElements()){
            name = (String)names.nextElement();
            replacementValue = replacementProperties.getPropertyString(name);
            
            if(results.containsName(name)){
                originalValue = results.get(name);

                if(replacementValue!=null){
                    if(originalValue instanceof XmlStringDataObject){
                        ((XmlStringDataObject)originalValue).setStringValue(replacementValue);

                    } else if(originalValue instanceof XmlIntegerDataObject){
                        // Parse as a double first and then cast to int
                        try {
                            double temp = Double.parseDouble(replacementValue);
                            ((XmlIntegerDataObject)originalValue).setIntValue((int)Math.floor(temp));
                        } catch (Exception e){
                            throw new XmlStorageException("Error parsing replacement integer value: " + replacementValue);
                        }

                    } else if(originalValue instanceof XmlLongDataObject){
                        // Parse as a double first and then cast to long
                        try {
                            double temp = Double.parseDouble(replacementValue);
                            ((XmlLongDataObject)originalValue).setLongValue((long)Math.floor(temp));
                        } catch (Exception e){
                            throw new XmlStorageException("Error parsing replacement long value: " + replacementValue);
                        }

                    } else if(originalValue instanceof XmlDoubleDataObject){
                        ((XmlDoubleDataObject)originalValue).setDoubleValue(Double.parseDouble(replacementValue));

                    } else if(originalValue instanceof XmlBooleanDataObject){
                        if(replacementValue.equalsIgnoreCase("true") || replacementValue.equalsIgnoreCase("yes") || replacementValue.equalsIgnoreCase("1")){
                            ((XmlBooleanDataObject)originalValue).setBooleanValue(true);
                        } else {
                            ((XmlBooleanDataObject)originalValue).setBooleanValue(false);
                        }
                    } else if(originalValue instanceof XmlStorableDataObject){
                        // If it is a server object set its ID
                        Object o = ((XmlStorableDataObject)originalValue).getValue();
                        if(o instanceof ServerObject){
                            ServerObject serverObj = (ServerObject)o;
                            serverObj.setId(replacementValue);
                            ((XmlStorableDataObject)originalValue).setValue(serverObj);
                        }                        
                        
                    } else if(originalValue instanceof XmlDateDataObject){
                        ((XmlDateDataObject)originalValue).setDateValue(new Date(Long.parseLong(replacementValue)));
 
                    }
                } 
            } else {
                // Need to add property
                results.add(name, replacementValue);
            }
        }
        
        return results;
    }
}
