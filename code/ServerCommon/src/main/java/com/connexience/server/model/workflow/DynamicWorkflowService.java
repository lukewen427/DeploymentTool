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
package com.connexience.server.model.workflow;

import com.connexience.server.model.document.DocumentRecord;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
/**
 * This class represents a dynamically deployed workflow service. It contains
 * details of the service category and the document record itself contains
 * a zip file in a standard format with a service.xml document within it. This
 * service.xml is extracted whenever the service is saved and is attached
 * to the document record as a ServiceXML object.
 * @author nhgh
 */
public class DynamicWorkflowService extends DocumentRecord {
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


    /** Service category */
    private String category;

    /** ID of the block project file that created this block if there is one */
    private String projectFileId;
    
    /** Get the service category */
    public String getCategory(){
        return category;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("Category", category);
        store.add("ProjectFileID", projectFileId);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        category = store.stringValue("Category", null);
        projectFileId = store.stringValue("ProjectFileID", null);
    }
    
    /** Set the service category */
    public void setCategory(String category){
        this.category = category;
    }
    
    /** Get the ID of the project file that created this block */
    public String getProjectFileId(){
        return this.projectFileId;
    }
    
    /** Set the ID of the project file that created this block */
    public void setProjectFileId(String projectFileId){
        this.projectFileId = projectFileId;
    }
}