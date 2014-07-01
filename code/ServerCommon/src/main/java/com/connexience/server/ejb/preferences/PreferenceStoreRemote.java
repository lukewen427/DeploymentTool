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
package com.connexience.server.ejb.preferences;

import com.connexience.server.ConnexienceException;
import java.io.File;
import java.util.Date;
import javax.ejb.Remote;
import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 * This interface defines the behavior of the preference store bean
 * @author hugo
 */
@Remote
public interface PreferenceStoreRemote {
    /** Get a property group */
    public XmlDataStore getPropertyGroup(String groupName);
    
    /** Save a property group */
    public void savePropertyGroup(String groupName, XmlDataStore store);
    
    public void add(String groupName, String propertyName, int value);
    public void add(String groupName, String propertyName, double value);
    public void add(String groupName, String propertyName, String value);
    public void add(String groupName, String propertyName, long value);
    public void add(String groupName, String propertyName, XmlDataStore value);
    public void add(String groupName, String propertyName, boolean value);
    public void add(String groupName, String propertyName, Date value);
    public void add(String groupName, String propertyName, File value);
    
    public int intValue(String groupName, String propertyName, int defaultValue);
    public double doubleValue(String groupName, String propertyName, double defaultValue);
    public String stringValue(String groupName, String propertyName, String defaultValue);
    public long longValue(String groupName, String propertyName, long defaultValue);
    public XmlDataStore xmlDataStoreValue(String groupName, String propertyName) throws ConnexienceException;
    public Date dateValue(String groupName, String propertyName, Date defaultValue);
    public File fileValue(String groupName, String propertyName, File defaultValue);
    
    public boolean groupHasProperty(String groupName, String propertyName);
    public String getMacAddress();
    public void setStorageEnabled(boolean storageEnabled);
    public boolean isStorageEnabled();
}