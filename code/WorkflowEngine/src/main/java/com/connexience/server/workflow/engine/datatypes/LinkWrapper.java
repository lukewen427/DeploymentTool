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
package com.connexience.server.workflow.engine.datatypes;

import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.StorableTransferData;
import org.pipeline.core.drawing.TransferData;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.io.*;

/**
 * This class passes links to server documents and folders between blocks in
 * a workflow.
 * @author hugo
 */
public class LinkWrapper implements TransferData, StorableTransferData, XmlStorable, Iterable<LinkPayloadItem>
{
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

    ArrayList<LinkPayloadItem> payload = new ArrayList<>();

    @Override
    public TransferData getCopy() throws DrawingException {
        LinkWrapper copy = new LinkWrapper();
        for(LinkPayloadItem i : payload){
            copy.addPayloadItem(i.getCopy());
        }
        return copy;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    protected void addPayloadItem(LinkPayloadItem item){
        this.payload.add(item);
    }

    /** Add an item with a reference */
    public void addItem(LinkPayloadItem item)
    {
        payload.add(item);
    }

    /** Add a folder reference */
    public void addFolder(Folder f){
        payload.add(new FolderLinkItem(f));
    }

    /** Add a document reference */
    public void addDocument(DocumentRecord document, DocumentVersion version){
        if(document!=null && version!=null){
            payload.add(new DocumentRecordLinkItem(document, version));
        }
    }

    /** Get the number of items */
    public int size(){
        return payload.size();
    }

    /** Get a payload item */
    public LinkPayloadItem getItem(int index){
        return payload.get(index);
    }

    @Override
    public void saveToOutputStream(OutputStream stream) throws DrawingException {
        try {
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(storeObject());
            writer.write(stream);
            stream.flush();
        } catch (Exception e){
            throw new DrawingException("Error writing link-wrapper: " + e.getMessage(), e);
        }
    }

    @Override
    public void loadFromInputStream(InputStream stream) throws DrawingException {
        try {
            XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(stream);
            XmlDataStore store = reader.read();
            recreateObject(store);
        } catch (Exception e){
            throw new DrawingException("Eror reading link-wrapper data: " + e.getMessage(), e);
        }
    }


    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("LinkWrapper");
        store.add("PayloadSize", payload.size());
        for(int i=0;i<payload.size();i++){
            store.add("Item" + i, payload.get(i));
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int size = store.intValue("PayloadSize", 0);
        payload.clear();
        for(int i=0;i<size;i++){
            payload.add((LinkPayloadItem)store.xmlStorableValue("Item" + i));
        }
    }

    @Override
    public Iterator<LinkPayloadItem> iterator()
    {
        return new Itr();
    }


    private class Itr implements Iterator<LinkPayloadItem>
    {
        int cursor = 0;

        public Itr()
        { }


        @Override
        public boolean hasNext() {
            return cursor < payload.size();
        }

        @Override
        public LinkPayloadItem next() {
            return payload.get(cursor++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("LinkWrapper does not support removing");
        }
    }
}
