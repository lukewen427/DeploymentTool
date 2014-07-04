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
package com.connexience.server.workflow.cloud.library;
import org.pipeline.core.xmlstorage.*;

import java.io.*;

/**
 * This class represents a dependency that a workflow library item has
 * on another library item. Each library item can have a list of these
 * dependencies that must be satisfied.
 * @author hugo
 */
public class CloudWorkflowItemDependency implements Serializable, XmlStorable
{
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

    /** Library name in the server */
    private String libraryName;

    /** Version number in the server */
    private int versionNumber;

    /** Use the latest library version */
    private boolean latestVersion = false;

    /** Is this a compile time or a runtime only dependency */
    private boolean runtimeOnlyDependency = false;

    public boolean isRuntimeOnlyDependency() {
        return runtimeOnlyDependency;
    }

    public void setRuntimeOnlyDependency(boolean runtimeOnlyDependency) {
        this.runtimeOnlyDependency = runtimeOnlyDependency;
    }
    
    public int getVersionNumber() {
        return versionNumber;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public boolean isLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(boolean latestVersion) {
        this.latestVersion = latestVersion;
    }

    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("CloudWorkflowItemDependency");
        store.add("LibraryName", libraryName);
        store.add("VersionNumber", versionNumber);
        store.add("LatestVersion", latestVersion);
        store.add("RuntimeOnlyDependency", runtimeOnlyDependency);
        return store;
    }

    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        libraryName = store.stringValue("LibraryName", null);
        versionNumber = store.intValue("VersionNumber", 0);
        latestVersion = store.booleanValue("LatestVersion", false);
        runtimeOnlyDependency = store.booleanValue("RuntimeOnlyDependency", false);
    }
}