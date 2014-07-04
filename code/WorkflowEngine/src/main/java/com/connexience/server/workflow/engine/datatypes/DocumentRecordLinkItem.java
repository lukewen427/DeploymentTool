/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connexience.server.workflow.engine.datatypes;

import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;

public class DocumentRecordLinkItem extends LinkPayloadItem {

    private DocumentVersion version = null;
    private DocumentRecord doc = null;
    
    public DocumentRecordLinkItem() {
    }

    public DocumentRecordLinkItem(DocumentRecord doc, DocumentVersion version) {
        super();
        this.doc = doc;
        this.version = version;
    }

    public DocumentRecord getDocument() {
        return doc;
    }

    public DocumentVersion getVersion() {
        return version;
    }

    @Override
    public ServerObject getServerObject() {
        return doc;
    }

    
    @Override
    public LinkPayloadItem getCopy() {
        return new DocumentRecordLinkItem(getDocument(), version);
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        if(doc!=null){
            store.add("Document", doc);
        }
        
        if(version!=null){
            store.add("Version", version);
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        if(store.containsName("Document")){
            doc = (DocumentRecord)store.xmlStorableValue("Document");
        } else {
            doc = null;
        }
        
        if(store.containsName("Version")){
            version = (DocumentVersion) store.xmlStorableValue("Version");
        } else {
            version = null;
        }
    }
}
