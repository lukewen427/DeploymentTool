/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connexience.server.workflow.engine.datatypes;

import com.connexience.server.model.ServerObject;
import com.connexience.server.model.folder.Folder;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

public class FolderLinkItem extends LinkPayloadItem {
    private Folder folder;

    public FolderLinkItem() {
    }
   
    public FolderLinkItem(Folder folder) {
        this.folder = folder;
    }

    public Folder getFolder() {
        return folder;
    }

    @Override
    public LinkPayloadItem getCopy() {
        return new FolderLinkItem(getFolder());
    }

    @Override
    public ServerObject getServerObject() {
        return folder;
    }

    
    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("Folder", folder);
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        folder = (Folder)store.xmlStorableValue("Folder");
    }
    
    
}