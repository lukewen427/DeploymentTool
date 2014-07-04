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

import com.connexience.server.util.DigestBuilder;
import com.connexience.server.workflow.engine.HashableTransferObject;
import java.io.*;
import java.util.*;

import org.pipeline.core.drawing.*;
import org.apache.log4j.*;

/**
 * This class provides a wrapper for the FileWrapperDataType which allows data
 * to be passed between blocks as raw non-parsed files. File references are stored
 * as a list of names. It is the responsibility of the storage client to
 * translate these names into actual locations.
 * @author nhgh
 */
public class FileWrapper implements TransferData, StorableTransferData, HashableTransferObject, Iterable<File> {
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

    private static final Logger logger = Logger.getLogger(FileWrapper.class);

    private static FilenameFilter _filter;

    /** File reference payload */
    private ArrayList<String> payload = new ArrayList<>();

    /** Hashing context -- a working directory used to calculate hash of files */
    private File hashContext;

    private File baseDir;


    public FileWrapper()
    { 
        this.baseDir = new File(".");
    }


    public FileWrapper(String baseDir) throws IOException
    {
        this(new File(baseDir));
    }


    /**
     * Create FileWrapper with given directory as the base for added file paths.
     * 
     * @param baseDir The pathname added to file paths.
     * 
     * @throws IOException thrown when baseDir denotes an absolute pathname.
     */
    public FileWrapper(File baseDir) throws IOException
    {
        if (baseDir == null) {
            throw new IOException("The base directory cannot be null. Use default constructor instead.");
        }

        //if (baseDir.isAbsolute()) {
        //    throw new IOException("The base directory cannot be absolute");
        //}

        this.baseDir = baseDir;
    }


    public FileWrapper(ArrayList<String> payload) throws IOException
    {
        this(new File("."), payload);
    }


    /** Construct with a payload */
    public FileWrapper(File baseDir, ArrayList<String> payload) throws IOException
    {
        //if (baseDir.isAbsolute()) {
        //    throw new IOException("The base directory cannot be absolute");
        //}

        this.baseDir = baseDir;
        this.payload = payload;
    }


    public File getBaseDir()
    {
        return baseDir;
    }


    /** Get a copy of this file wrapper */
    public TransferData getCopy() throws DrawingException
    {
        try {
            FileWrapper copy = new FileWrapper(baseDir);
            for (String name : payload) {
                copy.addFile(name);
            }
            return copy;
        } catch (Exception e){
            throw new DrawingException("Error copying file wrapper: " + e.getMessage());
        }
    }

    /** Get the object contained in this wrapper */
    public Object getPayload() {
        return payload;
    }


    /**
     * Add a file path to this wrapper. This is a convenience method equivalent
     * to calling <code>{@link #addFile(String, boolean) addFile(String path, boolean baseRelative = true)}</code>.
     * 
     * @param path The file path to add.
     */
    public void addFile(String path) {
        try { 
            addFile(path, true);
        } catch (IOException x) {
            // This should never happen
            logger.error("Internal FileWrapper error:", x);
            throw new Error("Internal FileWrapper error.", x);
        }
    }


    /** 
     * Add a file to this wrapper. This is a convenience method equivalent to
     * calling <code>{@link #addFile(String, boolean) addFile(file.getPath(), true)}</code>.
     * 
     * @param file The file to be added.
     */
    public void addFile(File file)
    {
        try {
            addFile(file.getPath(), true);
        } catch (IOException x) {
            // This should never happen
            logger.error("Internal FileWrapper error:", x);
            throw new Error("Internal FileWrapper error.", x);
        }
    }


    /**
     * Add a file to this wrapper. This is a covenience method equivalent to
     * calling <code>{@link #addFile(String, boolean) addFile(file.getPath(), baseRelative)}</code>.
     * 
     * @param file The file to be added.
     * 
     * @param baseRelative Whether the <code>file</code> denotes a path relative
     * to the base directory.
     * 
     * @throws IOException
     */
    public void addFile(File file, boolean baseRelative) throws IOException
    {
        addFile(file.getPath(), baseRelative);
    }


    /**
     * Add a file path to this wrapper. If <code>baseRelative</code> is set,
     * the given path is resolved in the context of the base directory. 
     * For example, if the path is <code>"bar"</code> and the base directory 
     * is <code>"foo"</code>, it is expected that <code>"foo/bar"</code>
     * denotes a valid file name in the file system.
     *
     * @param path The file path to add.
     * 
     * @param baseRelative Whether or not the path is relative to the base 
     *        directory.
     * 
     * @throws IOException If baseRelative is false and the given path does not
     *         start with the base directory name.
     */
    public void addFile(String path, boolean baseRelative) throws IOException
    {
        if (baseRelative) {
            payload.add(stripCurrent(path));
        } else if (path.startsWith(baseDir.getPath() + File.separator)) {
            payload.add(path.substring(baseDir.getPath().length() + File.separator.length()));
        } else {
            throw new IOException("The file name does not match the base directory '" + baseDir + "'");
        }
    }


    /** 
     * Recursively add directory <code>dir</code> to the file wrapper. The 
     * directory path is resolved in the context of the base directory. For 
     * example, if <code>dir</code> equals <code>"bar"</code> and the base 
     * directory equals <code>"foo"</code>, it is expected that 
     * <code>"foo/bar"</code> denotes a valid directory name in the file system.
     * 
     * This method is equivalent to calling 
     * <code>{@link #addDir(File, boolean) addDir(File dir, boolean baseRelative = false)}</code>.
     * 
     * @param dir a directory to be added to the file wrapper.
     */
    public void addDir(File dir) throws IOException
    {
        _addDir(new File(baseDir, stripCurrent(dir.getPath())));
    }


    /** 
     * Recursively add directory <code>dir</code> to the file wrapper. If 
     * <code>baseRelative</code> is set, the directory path is resolved in 
     * the context of the base directory. For example, if <code>dir</code> 
     * equals <code>"bar"</code> and the base directory equals 
     * <code>"foo"</code>, it is expected that <code>"foo/bar"</code> denotes 
     * a valid directory name in the file system.
     * 
     * @param dir The directory to be added to the file wrapper.
     * @param baseRelative Whether or not the directory is relative to the base 
     *        directory. 
     */
    public void addDir(File dir, boolean baseRelative) throws IOException
    {
        if (baseRelative) {
            _addDir(new File(baseDir, stripCurrent(dir.getPath())));
        } else if (dir.getPath().startsWith(baseDir.getPath())) {
            _addDir(dir);
        } else {
            throw new IOException("The directory name does not match with the base directory '" + baseDir + "'");
        }
    }


    /** Get the number of files */
    public int getFileCount() {
        return payload.size();
    }


    /** 
     * Get a specific file. The file returned includes the base directory.
     */
    public File getFile(int index) {
        return new File(baseDir, payload.get(index));
    }


    public String getFilePath(int index) {
        return new File(baseDir, payload.get(index)).getPath();
    }


    public File getRelativeFile(int index) {
        return new File(payload.get(index));
    }


    public String getRelativeFilePath(int index) {
        return payload.get(index);
    }


    /** 
     * Get an Iterator of all the files. The files returned include the base
     * directory. 
     */
    public Iterator<File> iterator() {
        return new Itr(true);
    }


    public Iterable<File> relativeFiles()
    {
        return new Iterable<File>() {
            @Override
            public Iterator<File> iterator() {
                return new Itr(false);
            }
        };
    }


    public Iterable<String> relativeFilePaths()
    {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return payload.iterator();
            }
        };
    }


    /** Load the file reference from storage */
    public void loadFromInputStream(InputStream stream) throws DrawingException
    {
        LineNumberReader reader = null;

        try {
            payload.clear();
            reader = new LineNumberReader(new InputStreamReader(stream));
            String line = reader.readLine();

            if (line != null) {
                baseDir = new File(line);
                while ((line = reader.readLine()) != null) {
                    payload.add(line);
                }
            } else {
                baseDir = new File(".");
            }
        } catch (Exception e) {
            throw new DrawingException("Error loading file list in FileWrapper: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception x) {
                    logger.warn("Error closing a FileWrapper reader", x);
                }
            }
        }
    }


    /** Save the list of files to an output stream */
    public void saveToOutputStream(OutputStream stream) throws DrawingException
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(stream);
            writer.println(baseDir);
            for (String fileName : payload) {
                writer.println(fileName);
            }
        } catch (Exception e){
            throw new DrawingException("Error saving file list in FileWrapper: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    @Override
    public String getHash() throws DrawingException
    {
        if (hashContext == null) {
            logger.error("File wrapper needs a hash context in order to calculate file hashes");
            throw new DrawingException("No hash context set for file wrapper");
        }

        // Sort payload without changing the order of the content
        Integer[] idx = new Integer[payload.size()];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = i;
        }
        Arrays.sort(idx, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                return payload.get(i1).compareTo(payload.get(i2));
            }
        });

        try {
            File context;
            if (baseDir.isAbsolute()){
                context = baseDir;
            }
            else{
                context = new File(hashContext, baseDir.getPath());
            }
            DigestBuilder builder = new DigestBuilder();
            // Build digest but get payload in the sorted way  
            for (Integer i : idx) {
                builder.update(new File(context, payload.get(i)));
            }
            return builder.getHash();
        } catch (Exception e){

            //Try to recover from not being able to calculate the hash as the files don't exist
            //Is this because the base directory is set incorrectly?
            logger.warn("Trying to calculate without baseDir");

            try {
                //Try to build the digest again from the filenames without the base context
                DigestBuilder builder = new DigestBuilder();
                // Build digest but get payload in the sorted way  
                for(Integer i : idx){
                    builder.update(new File(payload.get(i))); //Don't include the context as this has already failed once
                }

                //If we've got to this point then the files obviously exist as we've been able to calculate the hashes of them
                //So update the FileWrapper which will be written out in a later step

                //Set the base direcetory to the CWD which is the invocation directory
                this.baseDir = new File(new File(".").getCanonicalPath());
                int baseDirLen = this.baseDir.getCanonicalPath().length();
                for(int i = 0; i < payload.size(); i++)
                {
                    //Remove the invocation directory path from the filename
                    String newFileName = payload.get(i).substring(baseDirLen);

                    //Do the update
                    payload.set(i, newFileName);
                }

                return builder.getHash();
            } catch (Exception e1) {
                logger.error("Error calculating hash: " + e.getMessage(), e);
                throw new DrawingException(e.getMessage(), e);
            }
        }
    }


    /**
     *  Set the hashing context – the base directory – which will be used to 
     *  calculate hash of the payload files.
     *  
     * @param baseDir a directory appended to the base directory and files names 
     * in <code>payload</code> to create valid absolute file names.
     */
    public void setHashContext(File workingDir) {
        this.hashContext = workingDir;
    }


    /** Get the size of all of the files referenced in this wrapper */
    public long getTotalFileSize(File workingDirectory)
    {
        long size = 0;
        // [FIXME] To be discussed whether the baseDir may be absolute or not
        File context = 
                baseDir.isAbsolute() ? baseDir : new File(workingDirectory, baseDir.getPath());
        File f;
        for(int i=0;i<payload.size();i++){
            f = new File(context, payload.get(i));
            if(f.exists() && f.isFile()){
                size = size + f.length();
            }
        }
        return size;
    }


    /**
     * Recursively add all files included in the given directory.
     * <code>dir</code> should include the base directory because only then
     * it is possible to list its contents.
     * 
     * @param dir directory to be added to <code>payload</code>
     * @throws IOException
     */
    private void _addDir(File dir) throws IOException
    {
        assert dir.getPath().startsWith(baseDir.getPath()) : "Internal error: dir and baseDir do not match";

        // Initialize the filename filter to exclude hidden files, i.e. those
        // starting with '.*'
        if (_filter == null) {
            _filter = new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return !name.startsWith(".");
                }
            };
        }

        int baseDirLength = baseDir.getPath().length() + File.separator.length();

        File[] children = dir.listFiles(_filter);
        if (children == null) {
            throw new IOException("'" + dir + "' does not exist or is not a directory.");
        }

        for (File child : children) {
            if (child.isDirectory()) {
                _addDir(child);
            } else {
                String fileToAdd = child.getPath().substring(baseDirLength);
                System.out.println("Adding: " + fileToAdd);
                payload.add(fileToAdd);
            }
        }
    }


    /**
     * Removes redundant '.\' strings from the beginning of the path.
     *  
     * @param path
     * @return
     */
    private String stripCurrent(String path)
    {
        String s = "." + File.separator;

        if (path == null || "".equals(path) || ".".equals(path) || s.equals(path)) {
            return path;
        }

        int sLen = s.length();

        while (path.startsWith(s)) {
            path = path.substring(sLen);
        }
        
        if ("".equals(path)) {
            return ".";
        }

        return path;
    }


    private class Itr implements Iterator<File>
    {
        boolean fullNames;
        int cursor = 0;

        public Itr(boolean fullNames)
        {
            this.fullNames = fullNames;
        }


        @Override
        public boolean hasNext() {
            return cursor < payload.size();
        }

        @Override
        public File next() {
            if (fullNames) {
                return new File(baseDir, payload.get(cursor++));
            } else {
                return new File(payload.get(cursor++));
            }
        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException("FileWrapper does not support removing");
        }
    }
}