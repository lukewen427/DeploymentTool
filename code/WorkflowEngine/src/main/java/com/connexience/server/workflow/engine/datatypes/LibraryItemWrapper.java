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

import com.connexience.server.workflow.cloud.library.CloudWorkflowServiceLibraryItem;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.StorableTransferData;
import org.pipeline.core.drawing.TransferData;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamReader;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;

/**
 * This class provides a wrapper for a cloud workflow library item. It allows
 * references to libraries to be passed between blocks.
 * @author hugo
 */
public class LibraryItemWrapper implements TransferData, StorableTransferData, XmlStorable {
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


    /** Library item */
    ArrayList<CloudWorkflowServiceLibraryItem> libraries;

    public LibraryItemWrapper() {
        this.libraries = new ArrayList<>();
    }

    public LibraryItemWrapper(CloudWorkflowServiceLibraryItem... libraries) {
        this.libraries = new ArrayList<>();
        for (CloudWorkflowServiceLibraryItem library : libraries) {
            this.libraries.add(library);
        }
    }
    
    public LibraryItemWrapper(Collection<CloudWorkflowServiceLibraryItem> libraries) {
        this.libraries = new ArrayList<>(libraries);
    }

    /**
     * Returns a shallow copy of this wrapper.
     */
    @Override
    public TransferData getCopy() throws DrawingException {
        return new LibraryItemWrapper(libraries);
    }

    @Override
    public Object getPayload() {
        return libraries;
    }

    public void add(CloudWorkflowServiceLibraryItem... libraries)
    {
        for (CloudWorkflowServiceLibraryItem library : libraries) {
            this.libraries.add(library);
        }
    }

    public int size() {
        return libraries.size();
    }

    public CloudWorkflowServiceLibraryItem getLibrary(int index){
        return libraries.get(index);
    }
    
    @Override
    public void saveToOutputStream(OutputStream stream) throws DrawingException
    {
        try {
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(storeObject());
            writer.write(stream);
            stream.flush();
        } catch (Exception e) {
            throw new DrawingException("Error writing library-wrapper: " + e.getMessage(), e);
        }
    }

    @Override
    public void loadFromInputStream(InputStream stream) throws DrawingException {
        try {
            XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(stream);
            XmlDataStore store = reader.read();
            recreateObject(store);
        } catch (Exception e){
            throw new DrawingException("Eror reading library-wrapper data: " + e.getMessage(), e);
        }
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("LibraryItemWrapper");
        store.add("Size", libraries.size());
        int i = 0;
        for (CloudWorkflowServiceLibraryItem library : libraries) {
            store.add("LibraryItem-" + i++, library);
        }

        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int size = store.intValue("Size", 0);
        libraries.clear();
        for (int i = 0; i < size; i++) {
            libraries.add((CloudWorkflowServiceLibraryItem)store.xmlStorableValue("LibraryItem-" + i));
        }
    }
}