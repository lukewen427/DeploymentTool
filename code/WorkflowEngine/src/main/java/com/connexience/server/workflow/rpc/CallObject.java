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
package com.connexience.server.workflow.rpc;

import com.connexience.server.model.security.*;
import com.connexience.server.util.SerializationUtils;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.security.*;
import java.io.*;
/**
 * This is the core RPC call object that is used to send and retrieve information
 * from the server. It uses XmlDataStore objects to store data.
 * @author hugo
 */
public class CallObject implements XmlStorable, XmlSignable, XmlDataStoreSignatureListener, Serializable {
    static final long serialVersionUID = -2053244663432946862L;
    
    // Status flags
    
    /** Not yet executed */
    public static final int CALL_NOT_EXECUTED_YET = 0;
    
    /** Executed OK */
    public static final int CALL_EXECUTED_OK = 1;
    
    /** Executed with an error */
    public static final int CALL_EXECUTED_WITH_ERROR = 2;
    
    /** Executed with an exception */
    public static final int CALL_EXECUTED_WITH_RPC_EXCEPTION = 3;
    
    /** Call timed out */
    public static final int CALL_TIMEOUT = 4;
    
    /** Call arguments */
    private XmlDataStore callArgs;
    
    /** Return arguments */
    private XmlDataStore returnArgs;

    /** Provenance properties */
    private XmlDataStore provenanceProperties = null;

    /** Method name of the call object */
    private String methodName;
    
    /** Status of this call */
    private volatile int status = CALL_NOT_EXECUTED_YET;
    
    /** Did this call result in an IO exception */
    private volatile boolean ioException = false;
    
    /** Status text */
    private String statusMessage = "";
    
    /** ID of this call */
    private String callId;

    /** Is authentication required for this call */
    private boolean authenticationRequired = true;

    /** Security ticket */
    private Ticket ticket = null;

    /** Session object */
    private transient Object sessionObject = null;
    
    /** Request object */
    private transient Object requestObject = null;
    
    /** Signature helper object */
    private transient XmlDataStoreSignatureHelper signatureHelper;

    /** Empty constructor. This is used by the load / save code */
    public CallObject(){
        signatureHelper = new XmlDataStoreSignatureHelper(this);
    }
    
    /** Construct with a method name */
    public CallObject(String methodName){
        signatureHelper = new XmlDataStoreSignatureHelper(this);
        callArgs = new XmlDataStore("CallArguments");
        returnArgs = new XmlDataStore("ReturnArguments");
        this.methodName = methodName;
        callId = new RandomGUID().toString();
    }

    /** Did the attempt to send this call result in an IOException */
    public boolean isIoException() {
        return ioException;
    }
    
    /** Set whether the last attempt to send this call resulted in an IOException */
    public void setIOException(boolean ioException){
        this.ioException = ioException;
    }
            
    /** Reset this call so that it can be sent again */
    public void resetToResend(){
        this.returnArgs.clear();
        this.sessionObject = null;
        this.requestObject = null;
        this.status = CALL_NOT_EXECUTED_YET;
        this.statusMessage = "";
        this.callId = new RandomGUID().toString();
        this.ioException = false;
    }
    
    /** Set whether authentication is needed for this call to run. This won't affect
     * what the server does with the call, but will allow it to be sent if there
     * is no session id present */
    public void setAuthenticationRequired(boolean authenticationRequired){
        this.authenticationRequired = authenticationRequired;
    }

    /** Get whether authentication is needed for this call. This only affects is
     * passage through the client if there is no session id set */
    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }
    
    /** Set the session object */
    public void setSessionObject(Object sessionObject){
        this.sessionObject = sessionObject;
    }

    /** Get the session object */
    public Object getSessionObject(){
        return sessionObject;
    }
    
    /** Set the request object */
    public void setRequestObject(Object requestObject){
        this.requestObject = requestObject;
    }
    
    /** Get the request object */
    public Object getRequestObject(){
        return requestObject;
    }
    
    /** Set the security ticket */
    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }

    /** Get the security ticket */
    public Ticket getTicket(){
        return ticket;
    }
    
    /** Get the call status */
    public int getStatus(){
        return status;
    }
    
    /** Set the call status */
    public void setStatus(int status){
        this.status = status;
    }
    
    /** Get the status message */
    public String getStatusMessage(){
        return statusMessage;
    }
    
    /** Set the status message */
    public void setStatusMessage(String statusMessage){
        this.statusMessage = statusMessage;
    }
    
    /** Get the method name */
    public String getMethodName(){
        return methodName;
    }
    
    /** Set the method name */
    public void setMethodName(String methodName){
        this.methodName = methodName;
    }
    
    /** Get the call arguments */
    public XmlDataStore getCallArguments(){
        return callArgs;
    }
    
    /** Get the return arguments */
    public XmlDataStore getReturnArguments(){
        return returnArgs;
    }

    /** Set the provenance properties. These are sent with the call object. */
    public void setProvenanceProperties(XmlDataStore provenanceProperties) {
        this.provenanceProperties = provenanceProperties;
    }

    /** Get any provenance properties */
    public XmlDataStore getProvenanceProperties(){
        return provenanceProperties;
    }

    /** Does this object have provenance properties */
    public boolean hasProvenanceProperties(){
        if(provenanceProperties!=null){
            return true;
        } else {
            return false;
        }
    }
    
    /** Get the call Id */
    public String getCallId(){
        return callId;
    }
    
    /** Set the call Id */
    public void setCallId(String callId){
        this.callId = callId;
    }
    
    /** Clear the call arguments before sending back to client */
    public void clearCallArguments(){
        callArgs.clear();
        ticket = null;
    }
    
    /** Merge in a response call object */
    public void mergeResponse(CallObject response) throws RPCException {
        if(response.getCallId().equals(callId)){
            // Only merge in non-timeout responses
            if(this.status!=CALL_TIMEOUT){
                this.status = response.getStatus();
                this.statusMessage = response.getStatusMessage();
                this.returnArgs = response.getReturnArguments();
            }
        } else {
            throw new RPCException("Incorrect response object");
        }
    }
    
    /** Print this call object to System.out */
    public void debugPrint(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(stream);
        writer.println("===== RPC CallObject: " + methodName + " =====");
        writer.println("CallID: " + callId);
        writer.println("Status Message: " + statusMessage);
        writer.println("CallArgs:");
        callArgs.debugPrint(writer, 4);
        writer.println("ReturnArgs: ");
        returnArgs.debugPrint(writer, 4);
        writer.println("==========================================");
        writer.println();
        writer.flush();
        writer.close();
        System.out.println(new String(stream.toByteArray()));
    }
    
    /** Save to a store object */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("CallObject");
        store.add("MethodName", methodName);
        store.add("CallArguments", callArgs);
        store.add("ReturnArguments", returnArgs);
        store.add("CallID", callId);
        store.add("Status", status);
        store.add("StatusMessage", statusMessage);
        if(provenanceProperties!=null){
            store.add("ProvenanceProperties", provenanceProperties);
        }

        if(ticket!=null){
            try {
                byte[] ticketData = SerializationUtils.serialize(ticket);
                store.add("Ticket", ticketData);
            } catch (Exception e){
                throw new XmlStorageException("Could not store ticket");
            }
        }
        return store;
    }

    /** Recreate from a store object */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        this.methodName = store.stringValue("MethodName", "");
        this.callArgs = store.xmlDataStoreValue("CallArguments");
        this.returnArgs = store.xmlDataStoreValue("ReturnArguments");
        this.callId = store.stringValue("CallID", "");
        this.status = store.intValue("Status", CALL_NOT_EXECUTED_YET);
        this.statusMessage = store.stringValue("StatusMessage", "");
        if(store.containsName("ProvenanceProperties")){
            this.provenanceProperties = store.xmlDataStoreValue("ProvenanceProperties");
        } else {
            this.provenanceProperties = null;
        }

        // Pull out the ticket if possible
        if(store.containsName("Ticket")){
            try {
                ticket = (Ticket)SerializationUtils.deserialize(store.byteArrayValue("Ticket"));
            } catch (Exception e){
                throw new XmlStorageException("Could not load ticket");
            }
        } else {
            this.ticket = null;
        }
    }
    

    public XmlDataStoreSignatureHelper getSignatureData() {
        if(signatureHelper==null){
            signatureHelper = new XmlDataStoreSignatureHelper(this);
        }
        return signatureHelper;
    }

    public void signatureStateChanged(XmlDataStoreSignatureHelper sig) {

    }
}