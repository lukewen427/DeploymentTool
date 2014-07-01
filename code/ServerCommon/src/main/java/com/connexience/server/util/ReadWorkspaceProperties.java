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

import com.connexience.server.ConnexienceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class tries to read a set of workspace properties from a worksapce
 * file or input stream.
 * @author hugo
 */
public class ReadWorkspaceProperties {
    /** Read Properties from an InputStream */
    public static Properties readProperties(InputStream stream) throws ConnexienceException {
        ZipInputStream zip = new ZipInputStream(stream);
        ZipEntry entry;
        try {
            while((entry = zip.getNextEntry())!=null){
                if(entry.getName().equals("workspace.properties")){
                    // Load the properties
                    Properties props = new Properties();
                    props.load(zip);
                    return props;
                }
            }
        } catch (IOException ioe){
            throw new ConnexienceException("Error reading properties: " + ioe.getMessage());
        } finally {
            try {
                zip.close();
            } catch (IOException x) {
                // TODO: log this exception somewhere
            }
        }
        
        return null;
    }
}
