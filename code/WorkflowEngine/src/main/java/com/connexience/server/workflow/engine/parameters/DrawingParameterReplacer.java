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
package com.connexience.server.workflow.engine.parameters;

import com.connexience.server.model.ServerObject;
import com.connexience.server.workflow.xmlstorage.*;
import com.connexience.server.workflow.api.*;
import com.connexience.server.model.document.*;
import com.connexience.server.model.folder.*;
import org.pipeline.core.drawing.*;
import org.pipeline.core.drawing.model.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.xmldatatypes.*;

import java.util.*;

/**
 * This class takes an existing drawing and replaces selected parameters with
 * those specified in an XmlDataStore
 * @author hugo
 */
public class DrawingParameterReplacer {
    /** Drawing that will have parameters replaced */
    private DrawingModel drawing;
    
    /** API to fetch documents that have been set as external parameters */
    private API api;
     
    public DrawingParameterReplacer(DrawingModel drawing, API api) {
        this.drawing = drawing;
        this.api = api;
    }

    /** Replace parameters */
    public void replaceParameters(XmlDataStore store) throws XmlStorageException {
        Vector blockNames = store.getNames();
        String blockName;
        BlockModel block;
        XmlDataStore blockProperties;
        XmlDataStore replacementProperties;
        Vector parameterNames;
        String name;
        String className;
        XmlDataObject originalValue;
        String replacementValue;

        for(int i=0;i<blockNames.size();i++){
            blockName = blockNames.get(i).toString();
            block = drawing.getBlockByName(blockName);
            replacementProperties = store.xmlDataStoreValue(blockName);
            if(block instanceof DefaultBlockModel){
                blockProperties = ((DefaultBlockModel)block).getEditableProperties();
                parameterNames = replacementProperties.getNames();
                for(int j=0;j<parameterNames.size();j++){
                    name = parameterNames.get(j).toString();
                    originalValue = blockProperties.get(name);
                    replacementValue = replacementProperties.stringValue(name, null);
                    if(replacementValue!=null){
                        if(originalValue instanceof XmlStringDataObject){
                            ((XmlStringDataObject)originalValue).setStringValue(replacementValue);

                        } else if(originalValue instanceof XmlIntegerDataObject){
                            // Parse as a double first and then cast to int
                            if(!replacementValue.isEmpty()){
                                try {
                                    double temp = Double.parseDouble(replacementValue);
                                    ((XmlIntegerDataObject)originalValue).setIntValue((int)Math.floor(temp));
                                } catch (Exception e){
                                    throw new XmlStorageException("Error parsing replacement integer value: " + replacementValue);
                                }
                            }

                        } else if(originalValue instanceof XmlLongDataObject){
                            // Parse as a double first and then cast to long
                            if(!replacementValue.isEmpty()){
                                try {
                                    double temp = Double.parseDouble(replacementValue);
                                    ((XmlLongDataObject)originalValue).setLongValue((long)Math.floor(temp));
                                } catch (Exception e){
                                    throw new XmlStorageException("Error parsing replacement long value: " + replacementValue);
                                }
                            }

                        } else if(originalValue instanceof XmlDoubleDataObject){
                            if(!replacementValue.isEmpty()){
                                ((XmlDoubleDataObject)originalValue).setDoubleValue(Double.parseDouble(replacementValue));
                            }

                        } else if(originalValue instanceof XmlBooleanDataObject){
                            if(!replacementValue.isEmpty()){
                                if(replacementValue.equalsIgnoreCase("true") || replacementValue.equalsIgnoreCase("yes") || replacementValue.equalsIgnoreCase("1")){
                                    ((XmlBooleanDataObject)originalValue).setBooleanValue(true);
                                } else {
                                    ((XmlBooleanDataObject)originalValue).setBooleanValue(false);
                                }
                            }

                        } else if(originalValue instanceof XmlDateDataObject){
                            if(!replacementValue.isEmpty()){
                                ((XmlDateDataObject)originalValue).setDateValue(new Date(Long.parseLong(replacementValue)));
                            }

                        } else if(originalValue instanceof XmlStorableDataObject){
                            className = ((XmlStorableDataObject)originalValue).getClassName();
                            if(className.equals("com.connexience.server.model.document.DocumentRecord")){
                                // Set the ID of a document record
                                DocumentRecord doc = (DocumentRecord)((XmlStorableDataObject)originalValue).getValue();
                                
                                // Get document via api call
                                try {
                                    DocumentRecord newDocument = api.getDocument(replacementValue);
                                    newDocument.populateCopy(doc);
                                    doc.setId(newDocument.getId());
                                    ((XmlStorableDataObject)originalValue).setValue(doc);
                                } catch (Exception e){
                                    throw new XmlStorageException("Cannot set value for property: " + originalValue.getName() + ": " + e.getMessage());
                                }
                                
                            } else if(className.equals("com.connexience.server.model.folder.Folder")){
                                // Set the ID of a folder
                                Folder folder = (Folder)((XmlStorableDataObject)originalValue).getValue();

                                // Get folder via api call
                                try {
                                    Folder newFolder = api.getFolder(replacementValue);
                                    newFolder.populateCopy(folder);
                                    folder.setId(newFolder.getId());
                                    ((XmlStorableDataObject)originalValue).setValue(folder);
                                } catch(Exception e){
                                    throw new XmlStorageException("Cannot set value for folder property: " + originalValue.getName() + ": " + e.getMessage());
                                }
                            } else if (className.equals(StringListWrapper.class.getName())) {
                                // FIXME: This code needs to be replaced by more serious deserialization of the StringListWrapper
                                // For this moment it just takes the replacement value and inserts it as the first item on the list.
                                StringListWrapper list = new StringListWrapper();
                                list.add(replacementValue);
                                originalValue.setValue(list);
                            } else {
                                // Just set the object id for a generic server object.
                                Object o = ((XmlStorableDataObject)originalValue).getValue();
                                if(o instanceof ServerObject){
                                    ServerObject serverObj = (ServerObject)o;
                                    serverObj.setId(replacementValue);
                                    ((XmlStorableDataObject)originalValue).setValue(serverObj);
                                }

                            }
                        }
                    }
                }
            } else {
                throw new XmlStorageException("Could not find block: " + blockName + " during parameter replacement");
            }
        }
    }
}