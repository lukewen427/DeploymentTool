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
package com.connexience.server.model.logging.graph;

/**
 * This operation is triggered when a service reads a piece of data
 * @author hugo
 */
public class WorkflowDataReadOperation extends WorkflowGraphOperation {
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


    /** ID of the document that was read */
    String documentId ;

    /** Version ID of the document that was read */
    String versionId;

    /** UUID of the block that read the data */
    String blockUUID;

    /**
      * Name of the document being written
      */
     String documentName;

     /**
      * Version number of the document being written
      */
     String versionNumber;


    public String getBlockUUID() {
        return blockUUID;
    }

    public void setBlockUUID(String blockUUID) {
        this.blockUUID = blockUUID;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getDocumentName()
    {
      return documentName;
    }

    public void setDocumentName(String documentName)
    {
      this.documentName = documentName;
    }

    public String getVersionNumber()
    {
      return versionNumber;
    }

    public void setVersionNumber(String versionNumber)
    {
      this.versionNumber = versionNumber;
    }
}