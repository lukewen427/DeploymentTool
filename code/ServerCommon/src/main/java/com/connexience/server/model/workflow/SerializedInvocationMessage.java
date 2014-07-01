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
package com.connexience.server.model.workflow;

import com.connexience.server.util.SerializationUtils;

import java.io.Serializable;
/**
 * This class stores a serialized copy of a workflow invocation message so 
 * that it can be resent if the workflow fails.
 * @author hugo
 */
public class SerializedInvocationMessage implements Serializable {
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


    /** Database ID */
    private long id;
    
    /** ID of the invocation */
    private String invocationId;
    
    /** Byte array of serialized message */
    private byte[] serializedMessage;

    public SerializedInvocationMessage() {
    }

    /** Create from an invocation message */
    public SerializedInvocationMessage(WorkflowInvocationMessage msg) throws Exception {
        serializedMessage = SerializationUtils.serialize(msg);
        this.invocationId = msg.getInvocationId();
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public byte[] getSerializedMessage() {
        return serializedMessage;
    }

    public void setSerializedMessage(byte[] serializedMessage) {
        this.serializedMessage = serializedMessage;
    }
    
    /** Deserialize and return the invocation message */
    public WorkflowInvocationMessage deserializeMessage() throws Exception {
        return (WorkflowInvocationMessage)SerializationUtils.deserialize(serializedMessage);
    }
}