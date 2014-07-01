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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class builds a digest for a file on disk
 * @author hugo
 */
public class DigestBuilder {
    /** MD5 that gets updated for each file */
    private MessageDigest md5;

    public DigestBuilder() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
    }
    
    /** Update with a file */
    public void update(File target) throws IOException {
        BufferedInputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(target));
            byte[] buffer = new byte[256000];
            int len;
            while((len=inStream.read(buffer))!=-1){
                md5.update(buffer, 0, len);
            }             
        } catch (IOException ioe){
            throw ioe;
        } finally {
            if(inStream!=null){try{inStream.close();}catch(Exception e){}}
        }
    }
    
    /** Return the hash */
    public String getHash(){
        return Base64.encodeBytes(md5.digest());
    }
        
    /** Calculate the MD5 of a single file */
    public static String calculateMD5(File target) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[256000];
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(target));
            try {
                int len;
                while((len=inStream.read(buffer))!=-1){
                    md.update(buffer, 0, len);
                }
                return Base64.encodeBytes(md.digest());
            } finally {
                inStream.close();
            }
        } catch (Exception e){
            throw new Exception("Error calculating MD5 for file: " + e.getMessage(), e);
        }
        
    }
    
    public static String calculateMD5(InputStream stream) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[256000];
            BufferedInputStream inStream = new BufferedInputStream(stream);
            try {
                int len;
                while((len=inStream.read(buffer))!=-1){
                    md.update(buffer, 0, len);
                }
                return Base64.encodeBytes(md.digest());
            } finally {
                inStream.close();
            }
        } catch (Exception e){
            throw new Exception("Error calculating MD5 for stream: " + e.getMessage(), e);
        }        
    }
}