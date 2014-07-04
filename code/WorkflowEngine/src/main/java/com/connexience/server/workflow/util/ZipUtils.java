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
package com.connexience.server.workflow.util;

import java.io.*;
import java.util.zip.*;
import java.util.*;

/**
 * This class contains various zip file utility methods
 * @author nhgh
 */
public class ZipUtils {
    /** Unzip a file to a specified directory */
    public static void unzip(File file, File targetDirectory) throws Exception {
        Enumeration<? extends ZipEntry> entries;
        ZipFile zipFile = new ZipFile(file);
        try {
            File extractFile;

            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if(!entry.getName().startsWith(".")){
                    if (entry.isDirectory()) {
                        // Assume directories are stored parents first then children.
                        (new File(targetDirectory, entry.getName())).mkdirs();
                        continue;
                    }

                    InputStream inStream = zipFile.getInputStream(entry);
                    BufferedOutputStream outStream = null;
                    try {
                        extractFile = new File(targetDirectory, entry.getName());
                        if(extractFile.getParentFile()!=null && !extractFile.getParentFile().exists()){
                            extractFile.getParentFile().mkdirs();
                        }

                        outStream = new BufferedOutputStream(new FileOutputStream(extractFile));
                        copyInputStream(inStream, outStream);
                    } finally {
                        inStream.close();
                        if(outStream!=null){
                            outStream.close();
                        }
                    }
                }
            }
        } finally {
            zipFile.close();
        }
    }

    /** Copy the data from one stream to another */
    public static final void copyInputStream(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[4096];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        out.close();
        in.close();
    }

    /** Zip the contents of a directory */
    public static void zip(File directory, File zipFile) throws IOException {
        ZipOutputStream zipStream = null;
        try {
            zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
            zipDir(directory.getPath(), directory.getPath(), zipStream);
        } catch (IOException ioe){
            throw ioe;
        } finally {
            try {
                zipStream.flush();
            } catch (Exception e){}

            try {
                zipStream.close();
            } catch (Exception e){}
        }
    }

    /** Zip a directory to a zip output stream */
    public static void zipDir(String baseDir, String dir2zip, ZipOutputStream zos) throws IOException {

        //create a new File object based on the directory we have to zip
        File zipDir = new File(dir2zip);
        //get a listing of the directory content
        String[] dirList = zipDir.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;
        //loop through dirList, and zip the files
        for (int i = 0; i < dirList.length; i++) {
            File f = new File(zipDir, dirList[i]);
            if (f.isDirectory()) {
                //if the File object is a directory, call this
                //function again to add its content recursively
                String filePath = f.getPath();
                zipDir(baseDir, filePath, zos);
                //loop again
                continue;
            }
            //if we reached here, the File object f was not a directory
            //create a FileInputStream on top of f
            FileInputStream fis = new FileInputStream(f);
            // create a    new zipentry
            ZipEntry anEntry = new ZipEntry(subtractPath(baseDir, f.getPath()).replace("\\", "/"));

            //place the zip entry in the ZipOutputStream object
            zos.putNextEntry(anEntry);
            //now write the content of the file to the ZipOutputStream
            while ((bytesIn = fis.read(readBuffer)) != -1) {
                zos.write(readBuffer, 0, bytesIn);
            }
            //close the Stream
            fis.close();
        }
    }

    public static String subtractPath(String base, String full){
        return full.substring(base.length() + 1);
    }

    /** Remove a directory and all of its contents */
    public static void removeDirectory(File dir) throws IOException {
        if(dir.isDirectory()){
            File[] contents = dir.listFiles();
            for(int i=0;i<contents.length;i++){
                if(contents[i].isFile()){
                    contents[i].delete();
                } else {
                    removeDirectory(contents[i]);
                }
            }

            // Remove this dirctory
            dir.delete();
        } else {
            throw new IOException("File: " + dir.getName() + " is not a directory");
        }
    }
    
    /** Copy an input stream to a file */
    public static void copyStreamToFile(InputStream source, File target) throws IOException
    {
        FileOutputStream outStream = new FileOutputStream(target);
        copyInputStream(source, outStream);
    }


    /** Copy a file from one file to another */
    public static void copyFile(File source, File target) throws IOException
    {
        if (source.exists()) {
            FileInputStream inStream = null;
            BufferedOutputStream outStream = null;
            try {
                inStream = new FileInputStream(source);
                outStream = new BufferedOutputStream(new FileOutputStream(target));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                }
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException ignore) {
                    }
                }
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }


    /** 
     * Copy a file from one file to another.
     * This is a convenience method equivalent to calling
     * <code>{@link #copyFile(File, File) copyFile(source, new File(targetDir, source.getName())}</code>.
     */
    public static void copyFileToDirectory(File source, File targetDir) throws IOException {
        copyFile(source, new File(targetDir, source.getName()));
    }


    /** Copy a file from one file to another with a listener */
    public static void copyFile(File source, File target, InstrumentedInputStreamListener listener) throws IOException
    {
        if (source.exists()) {
            InstrumentedInputStream inStream = null;
            BufferedOutputStream outStream = null;
            try {
                inStream = new InstrumentedInputStream(new FileInputStream(source), listener);
                outStream = new BufferedOutputStream(new FileOutputStream(target));
                byte[] buffer = new byte[4096];
                int len;
                while ((len = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, len);
                }
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException ignore) {
                    }
                }
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }

    /** Add all of the contents of a directory to an array list */
    public static void addFolderContentsRecursively(File dir, ArrayList<File> files, String extension){
        if(dir.isDirectory()){
            File[] contents = dir.listFiles();
            for(int i=0;i<contents.length;i++){
                if(contents[i].isDirectory()){
                    // Recurse
                    addFolderContentsRecursively(contents[i], files, extension);
                } else {
                    if(contents[i].getName().endsWith(extension)){
                        files.add(contents[i]);
                    }
                }
            }
        }
    }

    /** Copy an entire directory tree */
    public static void copyDirTree(File sourceDir, File targetDir) throws IOException
    {
        if (!sourceDir.isDirectory() || !targetDir.isDirectory()) {
            throw new IOException("Copy directory needs a directory, not a file.");
        }
        if (sourceDir.equals(targetDir)) {
            throw new IOException("Cannot copy a directory to itself.");
        }

        File[] contents = sourceDir.listFiles();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isDirectory()) {
                if (contents[i].getCanonicalPath().equals(targetDir.getCanonicalPath())) {
                    // Skip if source subdir equals targetDir to avoid the exception
                    continue;
                }
                // Recurse into this directory
                File targetFile = new File(targetDir, contents[i].getName());
                targetFile.mkdir();
                copyDirTree(contents[i], targetFile);
            } else {
                // Copy the file
                File targetFile = new File(targetDir, contents[i].getName());
                copyFile(contents[i], targetFile);
            }
        }
    }


    /** Write a file to an output stream */
    public static void copyFileToOutputStream(File file, OutputStream stream) throws IOException {
        copyInputStream(new FileInputStream(file), stream);
    }


    /** Read the first line of a text file */
    public static String readFirstLineOfFile(File file) throws IOException
    {
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                return reader.readLine();
            } catch (IOException ioe){
                throw ioe;
            } finally {
                reader.close();
            }
        } else {
            throw new IOException("No such file: " + file.getPath());
        }
    }


    /** Write a single line of text to a file */
    public static void writeSingleLineFile(File file, String line) throws IOException
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.println(line);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    /**
     * Remove invalid characters from the <code>name</code> to make in a valid
     * file name.
     * 
     * @param name The name of a file.
     * @return An escaped name that is a valid file name in Linux, Windows and
     * other OSes.
     */
    public static String escapeFileName(String name)
    {
        char escapeChar = '%';
        StringBuilder output = new StringBuilder();
        int len = name.length();

        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (c == '/' || c == '\\' || c == escapeChar || c < ' ') {
                output.append(escapeChar);
                if (c < 0x10) {
                    output.append('0');
                }
                output.append(Integer.toHexString(c));
            } else {
                output.append(c);
            }
        }
        
        return output.toString();
    }
}