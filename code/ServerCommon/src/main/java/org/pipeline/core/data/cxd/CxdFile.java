 /*                     conneXience Data Pipeline
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

package org.pipeline.core.data.cxd;

 import org.pipeline.core.data.*;
 import org.pipeline.core.xmlstorage.XmlDataStore;
 import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamReader;
 import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;
 import org.xml.sax.InputSource;
 import org.xml.sax.XMLReader;
 import org.xml.sax.helpers.XMLReaderFactory;

 import java.io.*;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipFile;
 import java.util.zip.ZipInputStream;
 import java.util.zip.ZipOutputStream;

/**
 * This class represents a CXD data file that contains data in the standard
 * format for the connexience software and services. It is stored as a zipped
 * set of XML documents, with a single properties xml document and documents
 * for each column of data.
 * @author hugo
 */
public class CxdFile implements Serializable {
    /** Disk file that this object represents */
    private File file = null;
    
    /** Data that this file represents if it has been loaded */
    private Data data = null;
    
    /** Meta-data describing the file data */
    private DataMetaData metaData = null;
    
    /** Additional properties for the data */
    private XmlDataStore properties = new XmlDataStore();
    
    /** Empty constructor */
    public CxdFile(){
        file = null;
        data = null;
    }
    
    /** Construct from a File */
    public CxdFile(File file){
        this.file = file;
    }
    
    /** Construct from a Data set */
    public CxdFile(Data data){
        this.data = data;
        metaData = data.getMetaData();
    }
    
    /** Get the data properties */
    public XmlDataStore getProperties(){
        return properties;
    }
    
    /** Load all of the file into memory by reading an input stream */
    public Data load(InputStream stream) throws DataException {
        try {
            ZipInputStream zipStream = new ZipInputStream(stream);
            ByteArrayOutputStream bufferStream;        
            ZipEntry entry;
            String entryName;
            Column readCol;
            int index;
            int bt;
            int entryCount = 0;
            
            while((entry=zipStream.getNextEntry())!=null){
                // Read the data into a buffer
                bufferStream = new ByteArrayOutputStream();
                while((bt=zipStream.read())!=-1){
                    // Read data
                    bufferStream.write(bt);
                }                
                bufferStream.flush();
                bufferStream.close();
                
                // Process the data
                if(entryCount==0){
                    if(entry.getName().equalsIgnoreCase("MetaData.xml")){
                        metaData = readMetaData(new ByteArrayInputStream(bufferStream.toByteArray()));
                        
                        // Create the columns
                        data = new Data();
                        Column col;
                        for(int i=0;i<metaData.getColumns();i++){
                            col = ColumnFactory.createColumn(metaData.column(i).getColumnTypeId());
                            col.setName(metaData.column(i).getName());                    
                            data.addColumn(col);
                        }   
                        
                    } else {
                        throw new DataException("MetaData must come first in CxD file");
                    }
                    
                } else {
                    if(data!=null){
                        entryName = entry.getName();
                        index = getColumnIndex(entryName);
                        readCol = data.column(index);
                        readColumn(new ByteArrayInputStream(bufferStream.toByteArray()), readCol);
                                                
                    } else {
                        // Data needs to have been created by now
                        throw new DataException("Data has not been initialised correctly");
                    }
                }
 
                zipStream.closeEntry();
                entryCount++;
            }            
            return data;
            
            
        } catch (Exception e){
            e.printStackTrace();
            throw new DataException("Error reading CxD InputStream: " + e.getMessage());
        }
    }
    
    
    /** Load all of the file into memory */
    public Data load() throws DataException {
        final ColumnReadMonitor monitor = new ColumnReadMonitor();
        
        // Load in the metadata
        ZipFile zip = null;
        try {
            zip = new ZipFile(file);
            metaData = readMetaData(zip);
            if(metaData!=null){
                
                // Create the columns
                data = new Data();
                Column col;
                for(int i=0;i<metaData.getColumns();i++){
                    col = ColumnFactory.createColumn(metaData.column(i).getColumnTypeId());
                    col.setName(metaData.column(i).getName());                    
                    data.addColumn(col);
                }
                
                // Start threads to read the data
                monitor.setColumns(data.getColumns());
                final ZipFile zipRef = zip;
                
                for(int i=0;i<data.getColumns();i++){
                    final Column readCol = data.column(i);
                    final int index = i;
                    new Thread(new Runnable(){
                        public void run(){
                            try {
                                readColumn(zipRef, index, readCol, monitor);                    
                                monitor.setColumnFinished(index);
                            } catch (Exception e){}
                        }
                    }).start();
                    
                }
                
                // Check to see if all columns have been read
                while(!monitor.allColumnsFinished()){
                    try {
                        wait(0);
                    } catch(Exception e){}
                }
                
                return data;
                
            } else {
                throw new Exception("Null metadata");
            }
            
        } catch (Exception e){
            throw new DataException("Error reading CxD file: " + e.getMessage());
        } finally {
            try {zip.close();}catch(Exception e){}
        }
    }
    
    /** Save the current data to an OutputStream */
    public void save(OutputStream stream) throws DataException {
        if(data!=null){
            try {

                ZipOutputStream zipStream = new ZipOutputStream(stream);
                writeMetaData(zipStream, data.getMetaData());

                // Write the individual columns
                for(int i=0;i<data.getColumns();i++){
                    writeColumn(zipStream, data.column(i), i);
                }
                
                zipStream.flush();
                zipStream.close();
                stream.flush();
                
            } catch (Exception e){
                throw new DataException("Error writing CxD file: " + e.getMessage());
            }
        } else {
            throw new DataException("No data specified");
        }        
        
    }
    
    /** Save the current data to a file */
    public void save(File file) throws DataException {
        if(data!=null){
            try {
                if(file.exists()){
                    file.delete();
                }

                // Write out the meta-data
                FileOutputStream fileStream = new FileOutputStream(file);
                save(fileStream);
                fileStream.close();
                
            } catch (DataException de){
                throw de;
            } catch (Exception e){
                throw new DataException("Error writing CxD file: " + e.getMessage());
            }
        } else {
            throw new DataException("No data specified");
        }
    }
    
    /** Load the meta data properties from the file */
    private DataMetaData readMetaData(ZipFile zip) throws DataException {
        try {
            XmlDataStore metaDataStore = new XmlDataStoreStreamReader(zip.getInputStream(zip.getEntry("MetaData.xml"))).read();
            DataMetaData md = new DataMetaData();
            md.recreateObject(metaDataStore);
            return md;
            
        } catch (Exception e){
            throw new DataException("Error reading meta-data: " + e.getMessage());
        }
    }
    
    /** Load the meta data properties from a zip entry */
    private DataMetaData readMetaData(InputStream stream) throws DataException {
        try {
            XmlDataStore metaDataStore = new XmlDataStoreStreamReader(stream).read();
            DataMetaData md = new DataMetaData();
            md.recreateObject(metaDataStore);
            return md;
        } catch (Exception e){
            e.printStackTrace();
            throw new DataException("Error reading meta-data: " + e.getMessage());
        }
    }
    
    /** Load the data properties from the file */
    private XmlDataStore readDataProperties(ZipFile zip) throws DataException {
        try {
            return new XmlDataStoreStreamReader(zip.getInputStream(zip.getEntry("Properties.xml"))).read();
        } catch (Exception e){
            throw new DataException("Error reading data properties: " + e.getMessage());
        }
    }
    
    /** Write the meta data properties to the file */
    private void writeMetaData(ZipOutputStream stream, DataMetaData metaData) throws DataException {     
        try {
            ZipEntry metaDataEntry = new ZipEntry("MetaData.xml");
            XmlDataStore store = metaData.storeObject();
            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(store);
            stream.putNextEntry(metaDataEntry);
            writer.write(stream);
            stream.closeEntry();
            
        } catch (Exception e){
            throw new DataException("Error writing meta-data: " + e.getMessage());
        }
    }
    
    /** Write a specific column to the file */
    private void writeColumn(ZipOutputStream stream, Column column, int index) throws DataException {
        try {
            ZipEntry dataEntry = new ZipEntry("column" + index + "/data.xml");
            stream.putNextEntry(dataEntry);
 
            PrintWriter writer = new PrintWriter(stream);
            writer.println("<ColumnData>");
            int rows = column.getRows();
            StringBuffer buffer;
            
            for(int i=0;i<rows;i++){
                buffer = new StringBuffer();
                if(!column.isMissing(i)){
                    buffer.append("<Row index=\"");
                    buffer.append(i);
                    buffer.append("\" missing=\"false\" value=\"");
                    buffer.append(column.getCxDFormatValue(i));
                    buffer.append("\"/>");
                    writer.println(buffer.toString());
                } else {
                    buffer.append("<Row index=\"");
                    buffer.append(i);
                    buffer.append("\" missing=\"true\" value=\"null\"");     
                    buffer.append("/>");
                    writer.println(buffer.toString());
                }
            }

            writer.println("</ColumnData>");
            writer.flush();

            stream.closeEntry();
        } catch (Exception e){
            throw new DataException("Error writing column: " + column.getName() + ": " + e.getMessage());
        }
    }
        
    /** Read a specific column from an InputStream */
    private void readColumn(InputStream stream, Column col) throws DataException {
        try {
            InputSource source = new InputSource(stream);
            SaxColumnRowHandler handler = new SaxColumnRowHandler(col, null);
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(handler);
            reader.parse(source);
        } catch (Exception e){
            throw new DataException("Error reading column: " + e.getMessage());
        }
    }
     
    /** Read a specific column from the file */
    private void readColumn(ZipFile file, int index, Column col, ColumnReadMonitor monitor) throws DataException {
        try {

            ZipEntry columnEntry = file.getEntry("column" + index + "/data.xml");
            InputStream stream = file.getInputStream(columnEntry);

            InputSource source = new InputSource(stream);
            SaxColumnRowHandler handler = new SaxColumnRowHandler(col, monitor);
            
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(handler);
            reader.parse(source);

        } catch (Exception e){
            throw new DataException("Error loading colunn: " + e.getMessage());
        }
    }
    
    /** Get a column index from an entry name */
    private int getColumnIndex(String entryName){
        int len = entryName.length();
        String indexText = entryName.substring(6, len - 9);
        return Integer.parseInt(indexText);
    }

    /** Load from a byte array */
    public void load(byte[] data){
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        ZipInputStream zip = new ZipInputStream(stream);
        
        // Load in all of the entries
        
    }
}
