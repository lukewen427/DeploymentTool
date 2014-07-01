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

package org.pipeline.core.xmlstorage.io;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * This class reads XmlDataStores from an input stream.
 * @author  hugo
 */
public class XmlDataStoreStreamReader extends XmlDataStoreReadWriter {
    /** Input stream to read */
    private InputStream inStream = null;
    
    /** Creates a new instance of XmlDataStoreStreamReader */
    public XmlDataStoreStreamReader(InputStream inStream) {
        super();
        this.inStream = inStream;
    }
    
    /** Read the XmlDataStore from the stream */
    public XmlDataStore read() throws XmlStorageException {
        if(inStream!=null){
            DocumentBuilder db = null;
            DocumentBuilderFactory dbf = null;
            Document doc = null;
            
            try {
                dbf = DocumentBuilderFactory.newInstance();
                dbf.setIgnoringElementContentWhitespace(true);
                dbf.setCoalescing(true);
                db = dbf.newDocumentBuilder();
                doc = db.parse(inStream);
            } catch (Exception e){
                throw new XmlStorageException("Error parsing XmlData");
            }
            
            // Parse the document
            parseXmlDocument(doc); 
            return getDataStore();
            
        } else {
            throw new XmlStorageException("No input stream available");
        }
    }
}
