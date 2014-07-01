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
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

/**
 * This class just outputs an XmlDataStore to a Stream
 * @author  hugo
 */
public class XmlDataStoreStreamWriter extends XmlDataStoreReadWriter {
    
    /** Creates a new instance of XmlDataStoreStreamWriter */
    public XmlDataStoreStreamWriter(XmlDataStore dataStore) throws XmlStorageException {
        super(dataStore);
    }
    
    /** Write to an output stream */
    public void write(OutputStream stream) throws XmlStorageException {
        try{
            // Write XML to a string
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();

            DOMSource source = new DOMSource(getDocument());
            StreamResult result = new StreamResult(stream);
            t.transform(source, result);
            stream.flush();
        } catch (Exception e) {
            throw new XmlStorageException("Error writing XML to PrintStream: " + e.getMessage());
        }        
    }

    public void prettyPrint(OutputStream stream) throws XmlStorageException
    {
        try {
            DOMImplementationLS lsImpl = 
                    (DOMImplementationLS)DOMImplementationRegistry
                        .newInstance()
                        .getDOMImplementation("LS");
            LSSerializer writer = lsImpl.createLSSerializer();
            writer.getDomConfig().setParameter("format-pretty-print", true);
            LSOutput output = lsImpl.createLSOutput();
            output.setByteStream(stream);
            writer.write(getDocument(), output);
            stream.flush();
        } catch (Exception e) {
            throw new XmlStorageException("Error writing XML to PrintStream: ", e);
        }
    }
}
