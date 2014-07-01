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
 * This class represents the transfer of a piece of data from one block (service)
 * in a workflow to another. It contains enough data to recreate the two operations
 * which in turn can be used to construct a "virtual workflow" to recreate the
 * steps taken in generating a piece of data.
 * @author hugo
 */
public class WorkflowDataTransferOperation extends WorkflowGraphOperation
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


    /** Source port name */
    private String sourcePortName;

    /** Source block UUID */
    private String sourceBlockUUID;

    /** Target port name */
    private String targetPortName;

    /** Target block UUID */
    private String targetBlockUUID;

    /** Data type name */
    private String dataType;

    /** MD5 hash of data if known */
    private String hashValue = null;
    
    /** Number of bytes transferred */
    private long dataSize;

    /** Id of the service which sent the data */
    private String sourceServiceId;

    /** Version Id of the service which sent the data */
    private String sourceServiceVersionId;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSourceBlockUUID() {
        return sourceBlockUUID;
    }

    public void setSourceBlockUUID(String sourceBlockUUID) {
        this.sourceBlockUUID = sourceBlockUUID;
    }

    public String getSourcePortName() {
        return sourcePortName;
    }

    public void setSourcePortName(String sourcePortName) {
        this.sourcePortName = sourcePortName;
    }

    public String getTargetBlockUUID() {
        return targetBlockUUID;
    }

    public void setTargetBlockUUID(String targetBlockUUID) {
        this.targetBlockUUID = targetBlockUUID;
    }

    public String getTargetPortName() {
        return targetPortName;
    }

    public void setTargetPortName(String targetPortName) {
        this.targetPortName = targetPortName;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getHashValue() {
        return hashValue;
    }

    public String getSourceServiceId() {
        return sourceServiceId;
    }

    public void setSourceServiceId(String sourceServiceId) {
        this.sourceServiceId = sourceServiceId;
    }

    public String getSourceServiceVersionId() {
        return sourceServiceVersionId;
    }

    public void setSourceServiceVersionId(String sourceServiceVersionId) {
        this.sourceServiceVersionId = sourceServiceVersionId;
    }
}