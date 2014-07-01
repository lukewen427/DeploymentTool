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

import com.connexience.server.ConnexienceException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class extracts a set of XML data files from a zip stream containing a
 * workflow library item
 * @author nhgh
 */
public class XMLDataExtractor {
    /** List of files to extract */
    private ArrayList<String> fileList;

    /** Extracted data */
    private Hashtable<String,byte[]> extractedData = new Hashtable<>();

    /** Construct with a list */
    public XMLDataExtractor(String[] files) {
        fileList = new ArrayList<>();
        for(int i=0;i<files.length;i++){
            fileList.add(files[i]);
        }
    }
    
    /** Extract the XML data from a file */
    public void extractXmlData(File serviceFile) throws ConnexienceException {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(serviceFile);
            extractXmlData(stream);
        } catch (IOException e){
            throw new ConnexienceException("Error opening file: " + e.getMessage(), e);
        } finally {
            try {stream.close();}catch(Exception e){}
        }
    }

    /** Extract the XML data from stream */
    public void extractXmlData(InputStream serviceArchiveStream) throws ConnexienceException {
        try {
            ZipInputStream zipStream = new ZipInputStream(serviceArchiveStream);
            ZipEntry entry;
            while((entry = zipStream.getNextEntry())!=null){
                if(fileList.contains(entry.getName())){
                    // This is the XML file
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[4095];
                    int len;
                    while((len = zipStream.read(data))>0){
                        buffer.write(data, 0, len);
                    }
                    extractedData.put(entry.getName(), buffer.toByteArray());
                }
            }

        } catch (Exception e){
            throw new ConnexienceException("Cannot extract service xml: + " + e.getMessage());
        }
    }
    
    /** Does the extracted data contain all of the specified files */
    public boolean allDataPresent(){
        for(int i=0;i<fileList.size();i++){
            if(!extractedData.containsKey(fileList.get(i))){
                return false;
            }
        }
        return true;
    }

    /** Is a specific entry present */
    public boolean entryPresent(String name){
        return extractedData.containsKey(name);
    }

    /** Get an extracted xml string */
    public String getEntry(String name){
        return new String(extractedData.get(name));
    }
    
    /** Get an extracted byte array */
    public byte[] getEntryAsBytes(String name){
        return extractedData.get(name);
    }
}
