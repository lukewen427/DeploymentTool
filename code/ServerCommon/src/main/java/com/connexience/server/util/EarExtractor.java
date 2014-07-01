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
package com.connexience.server.util;
import java.io.*;
import java.util.zip.*;
import java.util.*;
import org.jboss.logging.Logger;

/**
 * This class extracts files from an Ear file to a temporary location. It is used
 * to build the workflow-engine core library when the internal workflow engine
 * starts.
 * @author hugo
 */
public class EarExtractor {
    Logger logger = Logger.getLogger(EarExtractor.class);
    /** File name to extract */
    String earFileName;
    
    /** Ear file location */
    String earDirectory;
    
    /** Temporary directory */
    String tempDirectory;
    
    /** Names of files to extract */
    private ArrayList<String> namePatterns = new ArrayList<>();
    
    /** List of extracted files */
    private ArrayList<File> extractedFiles;

    public EarExtractor(String earFileName, String earDirectory, String tempDirectory) {
        this.tempDirectory = tempDirectory;
        this.earFileName = earFileName;
        this.earDirectory = earDirectory;
    }
    
    public void addNamePattern(String pattern){
        namePatterns.add(pattern);
    }
    
    public ArrayList<File> extract() throws Exception {
        extractedFiles = new ArrayList<>();
        File earFile = new File(earDirectory, earFileName);
        if(earFile.exists() && earFile.isFile()){
            ZipFile zip = new ZipFile(earFile);
            ZipEntry entry;
            Enumeration<? extends ZipEntry> entries = zip.entries();
            File extractFile;
            try {
                while(entries.hasMoreElements()){
                    entry = entries.nextElement();
                    if(!entry.isDirectory()){
                        // This is a file. Check the name patterns
                        for(String pattern : namePatterns){
                            if(WildcardUtils.wildCardMatch(entry.getName(), pattern)){                    
                                extractedFiles.add(extractEntry(zip, entry));
                            }
                        }
                    }
                }
                
                return extractedFiles;
            } catch (Exception e){
                throw e; 
            } finally {
                zip.close();
            }
            
        } else {
            throw new Exception("Ear file not found");
        }
        
    }
    
    /** Purge the temporary files */
    public void purgeTempFiles(){
        if(extractedFiles!=null){
            for(File f : extractedFiles){
                if(!f.delete()){
                    f.deleteOnExit();
                }
            }   
        }
    }
    
    /** Extract an entry to a File */
    private File extractEntry(ZipFile zip, ZipEntry entry) throws Exception {
        File folder = new File(tempDirectory);
        if(!folder.exists()){
            folder.mkdirs();
        }
        
        if(folder.isDirectory()){
            File entryFile = new File(entry.getName());
            InputStream inStream = null;
            FileOutputStream outStream = null;
            try {
                File extractFile = new File(folder, entryFile.getName());
                outStream = new FileOutputStream(extractFile);
                inStream = zip.getInputStream(entry);
                ZipUtils.copyInputStream(inStream, outStream);
                return extractFile;
            } catch (Exception e){
                throw e;
            } finally {
                if(inStream!=null){
                    try{inStream.close();}catch(Exception e){}
                }
                if(outStream!=null){
                    try {
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e){}
                }
            }
        } else {
            throw new Exception("No available temporary directory");
        }
    }
    
    
    public static void main(String[] args){
        try {
            EarExtractor extractor = new EarExtractor("inkspot.ear", "/work/jboss/standalone/deployments", "/work/jboss/standalone/tmp/corelibrary");
            extractor.addNamePattern("log4j-*.jar");
            extractor.addNamePattern("server-common*.jar");
            extractor.addNamePattern("workflow-engine*.jar");
            ArrayList<File> files = extractor.extract();
            for(File f : files){
                System.out.println(f.getPath());
            }
                    
            extractor.purgeTempFiles();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
