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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * This class reads and writes XmlDataObjects to/from disk files
 * @author  hugo
 */
public class XmlFileIO extends XmlDataStoreReadWriter {
    /** File to read / write */
    private File file;
    
    /** Create a new instance of XmlFileIO */
    public XmlFileIO(File file){
        this.file = file;
    }
    
    /** Creates a new instance of XmlFileIO */
    public XmlFileIO(XmlDataStore dataStore) throws XmlStorageException {
        super(dataStore);
    }
    
    /** Write to a File */
    public void writeFile(File outFile) throws XmlStorageException {
        try { 
            if(outFile.exists()) {
                outFile.delete();
            }
            if(outFile.createNewFile()){
	            java.io.FileOutputStream fileStream = new java.io.FileOutputStream(outFile);
	            TransformerFactory tf = TransformerFactory.newInstance();
	            Transformer t = tf.newTransformer();
	            DOMSource source = new DOMSource(getDocument());
	            StreamResult result = new StreamResult(fileStream);
	            t.transform(source, result);                
	            fileStream.flush();
                    fileStream.getFD().sync();
                    fileStream.close();
            } else {
            	throw new XmlStorageException("Error writing XML to file: Cannot create file");
            }
            
        } catch(Exception e) {
            throw new XmlStorageException("Error writing XML to file: " + e.getMessage());
        }
    }
    
    /** Read data from internally stored file */
    public XmlDataStore readFile() throws XmlStorageException {
        if(this.file!=null){
            return readFile(this.file);
        } else {
            throw new XmlStorageException("No file");
        }
    }
    
    /** Read from a File */
    public XmlDataStore readFile(File inFile) throws XmlStorageException {
        if(inFile.exists()){
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setIgnoringElementContentWhitespace(true);
                dbf.setCoalescing(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                parseXmlDocument(db.parse(inFile));
                return getDataStore();
                
            } catch (Exception e){
                throw new XmlStorageException("Error reading file: " + e.getMessage());
            }
            
        } else {
            throw new XmlStorageException("File: " + inFile.getName() + " does not exist");
        }
    }
}
