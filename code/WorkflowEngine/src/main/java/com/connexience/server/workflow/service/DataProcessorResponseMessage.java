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
package com.connexience.server.workflow.service;

import org.pipeline.core.xmlstorage.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/**
 * This message is used to signifiy the end of a data processing task. It triggers
 * the rest of the workflow execution engine tasks.
 * @author hugo
 */
public class DataProcessorResponseMessage implements Serializable, XmlStorable {
	private static final long serialVersionUID = 1L;

    // Service execution message flags */
	/** Service has not been executed yet */
    public static final int SERVICE_NOT_EXECUTED_YET = 0;
    
    /** Service executed Ok */
    public static final int SERVICE_EXECUTION_OK = 1;
    
    /** Service executed with an error */
    public static final int SERVICE_EXECUTION_ERROR = 2;    
    
    /** Service timed our */
    public static final int SERVICE_TIMEOUT = 3;
    
    /** Service has frozen */
    public static final int SERVICE_FROZEN = 4;
    
    /** User has killed the process */
    public static final int SERVICE_KILLED = 5;
    
    /** Workflow invocation id */
    private String invocationId;
    
    /** Invocation context id. This refers to the part of the workflow that triggered the call */
    private String contextId;
    
    /** Execution status of the service */
    private int status = SERVICE_NOT_EXECUTED_YET;
    
    /** Execution status message */
    private String statusMessage = "";

    /** Output from the command if there is any */
    private String commandOutput = "";

    /** Should the workflow wait for a lock */
    private boolean waitingForLock = false;

    /** ID of the lock to wait for */
    private long lockId = 0;

    /** Create an empty response message */
    public DataProcessorResponseMessage(){
        
    }
    
    /** Create with invocation and context ids */
    public DataProcessorResponseMessage(String invocationId, String contextId){
        this.contextId = contextId;
        this.invocationId = invocationId;
    }
    
    /** Save this object to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("DataProcessorResponseMessage");
        store.add("InvocationID", invocationId);
        store.add("ContextID", contextId);
        store.add("Status", status);
        store.add("StatusMessage", statusMessage);
        store.add("CommandOutput", commandOutput);
        store.add("WaitingForLock", waitingForLock);
        store.add("LockID", lockId);
        return store;
    }

    /** Set the invocation ID */
    public void setInvocationId(String invocationId){
        this.invocationId = invocationId;
    }
    
    /** Set the contextID */
    public void setContextId(String contextId){
        this.contextId = contextId;
    }
    
    /** Get the invocation ID */
    public String getInvocationId(){
        return invocationId;
    }
    
    /** Get the context ID */
    public String getContextId(){
        return contextId;
    }
    
    /** Get the status message */
    public String getStatusMessage(){
        return statusMessage;
    }
    
    /** Set the execution execution message */
    public void setStatusMessage(String statusMessage){
        this.statusMessage = statusMessage;
    }
    
    /** Get the execution status flag */
    public int getStatus(){
        return status;
    }
    
    /** Set the execution status flag */
    public void setStatus(int status){
        this.status = status;
    }

    /** Set the output text */
    public void setCommandOutput(String commandOutput){
        this.commandOutput = commandOutput;
    }

    /** Get the output text */
    public String getCommandOutput(){
        return commandOutput;
    }

    public boolean isWaitingForLock() {
        return waitingForLock;
    }

    public void setWaitingForLock(boolean waitingForLock) {
        this.waitingForLock = waitingForLock;
    }

    public long getLockId() {
        return lockId;
    }

    public void setLockId(long lockId) {
        this.lockId = lockId;
    }


    /** Recreate this object from storage */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        this.invocationId = store.stringValue("InvocationID", "");
        this.contextId = store.stringValue("ContextID", contextId);
        this.status = store.intValue("Status", SERVICE_NOT_EXECUTED_YET);
        this.statusMessage = store.stringValue("StatusMessage", "");
        this.commandOutput = store.stringValue("CommandOutput", "");
        this.waitingForLock = store.booleanValue("WaitingForLock", false);
        this.lockId = store.longValue("LockID", 0);
    }

    /** Parse an XML Document to create this response message */
    public void loadXmlStream(InputStream stream) throws DataProcessorException {
        try {
            // Read the document
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setCoalescing(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(stream);

            // Get the top level element
            Element documentElement = doc.getDocumentElement();

            // Get all of the child elements
            NodeList contentNodes = documentElement.getChildNodes();
            Element currentElement;
            for(int i=0;i<contentNodes.getLength();i++){

                currentElement = (Element)contentNodes.item(i);

                // First search for standard properties
                String nodeName = currentElement.getNodeName().trim();
                if(nodeName.equalsIgnoreCase("invocationid")){
                    invocationId = currentElement.getAttribute("value").trim();

                } else if(nodeName.equalsIgnoreCase("contextid")){
                    contextId = currentElement.getAttribute("value").trim();

                } else if(nodeName.equalsIgnoreCase("status")){
                    status = Integer.parseInt(currentElement.getAttribute("value").trim());

                } else if(nodeName.equalsIgnoreCase("statusmessage")){
                    statusMessage = currentElement.getAttribute("value").trim();

                } else if(nodeName.equalsIgnoreCase("commandoutput")){
                    commandOutput = currentElement.getAttribute("value").trim();

                } else if(nodeName.equalsIgnoreCase("waitingforlock")){
                    String v = currentElement.getAttribute("value").trim();
                    if(v.equalsIgnoreCase("true")){
                        waitingForLock = true;
                    } else {
                        waitingForLock = false;
                    }

                } else if(nodeName.equalsIgnoreCase("lockid")){
                    lockId = Long.parseLong(currentElement.getAttribute("value"));
                }
            }
        } catch (Exception e){
            throw new DataProcessorException("Error parsing data processor response message: " + e.getMessage());
        }
    }
}