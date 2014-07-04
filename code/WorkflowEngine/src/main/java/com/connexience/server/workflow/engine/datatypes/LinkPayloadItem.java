/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connexience.server.workflow.engine.datatypes;

import com.connexience.server.model.ServerObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

/**
 *
 * @author hugo
 */
public abstract class LinkPayloadItem implements XmlStorable {

    public LinkPayloadItem() {
    }

    public abstract LinkPayloadItem getCopy();

    public abstract ServerObject getServerObject();
    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("LinkPayloadItem");

        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
    }
}