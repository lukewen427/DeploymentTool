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

import com.connexience.server.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;

/**
 * This class represents an XML service definition file that is stored in the
 * database. It is stored separately from the actual service because it needs
 * to be downloaded quickly when editing workflows. This service XML data is
 * attached to a specific document version of a WorkflowService.
 * @author nhgh
 */
public class ServiceXML implements Serializable {
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


    /** Database ID */
    private String id;

    /** ID of the service document */
    public String serviceId;

    /** ID of the workflow document version */
    public String versionId;

    /** XML Data as a string */
    public String xmlData;

    /** Get the object ID */
    public String getId() {
        return id;
    }

    /** Set the object ID */
    public void setId(String id) {
        this.id = id;
    }

    /** Get the id of the workflow service that this XML refers to */
    public String getServiceId() {
        return serviceId;
    }

    /** Set the id of the workflow service that this XML refers to */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /** Get the ID of the service data that this XML file refers to */
    public String getVersionId() {
        return versionId;
    }

    /** Set the ID of the service data that this XML file refers to */
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /** Get the actual XML data as a string */
    public String getXmlData() {
        return xmlData;
    }

    /** Set the actual XML data as a string */
    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    public String getCategory() throws Exception {
        Document doc = XmlUtils.readXmlDocumentFromString(xmlData);
        String category = null;
        Element top = doc.getDocumentElement();
        NodeList children = top.getChildNodes();
        Node child;
        
        for(int i=0;i<children.getLength();i++){
            child = children.item(i);
            if(child.getNodeName().equalsIgnoreCase("category")){
                category = child.getTextContent().trim();
            }
        }
        return category;
    }

    /** Change the category */
    public void changeCategory(String newCategory) throws Exception {
        String oldCategory = getCategory();
        xmlData = xmlData.replace("<Category>" + oldCategory + "</Category>", "<Category>" + newCategory.trim() + "</Category>");
    }
}