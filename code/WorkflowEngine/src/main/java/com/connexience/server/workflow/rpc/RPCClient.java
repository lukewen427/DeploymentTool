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

import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.SerializationUtils;
import org.pipeline.core.xmlstorage.Base64;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides the client side RPC code. It sends messages to the server
 * and manages the co-ordination of the replies.
 * @author hugo
 */
public class RPCClient implements Serializable, XmlStorable {
	private static final long serialVersionUID = 1L;

	/** Session level security */
    public static final int SESSION_SECURITY = 1;

    /** Private key security */
    public static final int PRIVATE_KEY_SECURITY = 2;

    /** Hashing user private hash into a signature */
    public static final int HASH_SECURITY = 3;

    /** Ticket is sent along with the call object */
    public static final int TICKET_SECURITY = 4;

    /** Calls will be sent using XML */
    public static final int XML_SERIALIZATION = 0;
    
    /** Calls will be directly serialized */
    public static final int JAVA_SERIALIZATION = 1;
    
    /** Data transfer listeners */
    private transient ArrayList<RPCClientListener> listeners;

    /** Outstanding calls */
    private transient HashMap<String, CallInvocation> calls;

    /** Queued calls */
    private transient ArrayList<CallInvocation> queuedCalls;

    /** Server URL */
    private String serverUrl;

    /** JSESSION ID Cookie */
    private String sessionId = "";

    /** Default call timeout */
    private int defaultTimeout = 60;

    /** Desired session timeout */
    private int desiredSessionTimeout = 1800;

    /** Should calls be delayed pending a valid session id */
    private boolean sessionIdRequired = false;

    /** Provenance tracking data */
    private XmlDataStore provenanceProperties;
            
    /** Security method */
    private int securityMethod = SESSION_SECURITY;

    /** Private key for signing calls */
    private PrivateKey key = null;

    /** HashKey text of the signing user */
    private String hashingKey = null;

    /** Ticket of user */
    private Ticket ticket = new Ticket();

    /** ID of the user signing the calls */
    private String signingUserId = null;

    /** Serialization method to use */
    private int serializationMethod = JAVA_SERIALIZATION;
    /** Empty constructor */
    public RPCClient() {
        listeners = new ArrayList<>();
        calls = new HashMap<>();
        queuedCalls = new ArrayList<>();
    }

    /** Construct with a server URL */
    public RPCClient(String serverUrl) {
        this.serverUrl = serverUrl;
        listeners = new ArrayList<>();
        calls = new HashMap<>();
        queuedCalls = new ArrayList<>();
    }

    /** Get the method used to serialize calls */
    public int getSerializationMethod() {
        return serializationMethod;
    }

    /** Set the method used to serialize calls */
    public void setSerializationMethod(int serializationMethod) {
        this.serializationMethod = serializationMethod;
    }
    
    /** Set the provenance properties. These are sent with the call object. */
    public void setProvenanceProperties(XmlDataStore provenanceProperties) {
        this.provenanceProperties = provenanceProperties;
    }

    /** Get any provenance properties */
    public XmlDataStore getProvenanceProperties(){
        return provenanceProperties;
    }

    public void setSigningUserId(String signingUserId) {
        this.signingUserId = signingUserId;
    }

    public void setPrivateKey(PrivateKey key){
        this.key = key;
    }

    public void setHashingKey(String hashingKey){
        this.hashingKey = hashingKey;
    }
    
    public String getSigningUserId() {
        return signingUserId;
    }

    /** Set the desired session timeout interval */
    public void setDesiredSessionTimeout(int desiredSessionTimeout){
        this.desiredSessionTimeout = desiredSessionTimeout;
    }

    /** Get the desired session timeout interval */
    public int getDesiredSessionTimeout(){
        return desiredSessionTimeout;
    }
    
    /** Add a listener */
    public void addRPCClientListener(RPCClientListener listener){
        listeners.add(listener);
    }

    /** Remove a listener */
    public void removeRPCClientListener(RPCClientListener listener){
        listeners.remove(listener);
    }

    /** Notify the listeners that some data has been received */
    public void notifyDataReceived(int bytes){
        if(listeners!=null){
            for(int i=0;i<listeners.size();i++){
                listeners.get(i).dataReceived(bytes);
            }
        }
    }

    /** Notify the start of data transfer */
    public void notifyTransferStarted(){
        if(listeners!=null){
            for(int i=0;i<listeners.size();i++){
                listeners.get(i).transferStarted();
            }
        }
    }

    /** Notify the end of data transfer */
    public void notifyTransferFinished(){
        if(listeners!=null){
            for(int i=0;i<listeners.size();i++){
                listeners.get(i).transferFinished();
            }
        }
    }

    /** Is a session ID required in order to send calls */
    public boolean isSessionIdRequired() {
        return sessionIdRequired;
    }

    /** Set whether a session ID is required to send calls. If this is true, calls
     * are queued until an id is set */
    public void setSessionIdRequired(boolean sessionIdRequired) {
        this.sessionIdRequired = sessionIdRequired;
    }

    /** Set the default timeout */
    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    /** Get the default timeout */
    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    /** Set the session ID */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;

        // Process any held calls
        if(sessionIdRequired){
            processQueue();
        }
    }

    /** Get the session ID */
    public String getSessionId() {
        return sessionId;
    }

    /** Execute an asynchronous call */
    public void asyncCall(final CallObject call, final CallInvocationListener listener) throws RPCException {
        final RPCClient parent = this;
        if (!calls.containsKey(call.getCallId())) {
            if(this.provenanceProperties!=null){
                try {
                    XmlDataStore prov = new XmlDataStore();
                    prov.copyProperties(this.provenanceProperties);
                    call.setProvenanceProperties(prov);
                } catch (Exception e){
                    throw new RPCException("Error setting provenance data in call", e);
                }
            }
            CallInvocation invocation = new CallInvocation(call, parent, listener);
            
            if(securityMethod==PRIVATE_KEY_SECURITY){
                if(key!=null){
                    invocation.setSecurityMethod(securityMethod);
                    invocation.setPrivateKey(key);
                    invocation.setSigningUserId(signingUserId);
                } else {
                    throw new RPCException("Security set to private key, but no key presen");
                }
            } else if(securityMethod==TICKET_SECURITY){
                if(ticket!=null){
                    invocation.setSecurityMethod(securityMethod);
                    invocation.setTicket(ticket);
                } else {
                    throw new RPCException("Security set to ticket, but no ticket present");
                }
            }

            // Can the call be sent
            if(!call.isAuthenticationRequired()){
                sendCall(invocation);

            } else if(sessionIdRequired && (sessionId==null || sessionId.trim().equalsIgnoreCase("")) && securityMethod==SESSION_SECURITY){
                // Cannot send the call yet
                queuedCalls.add(invocation);

            } else {
                sendCall(invocation);
            }
        }
    }

    /** Execute a synchronous call. This method blocks until the server replies */
    public void syncCall(final CallObject call) throws RPCException {
        // Copy in provenance data if present
        if(this.provenanceProperties!=null){
            try {
                XmlDataStore prov = new XmlDataStore();
                prov.copyProperties(this.provenanceProperties);
                call.setProvenanceProperties(prov);
            } catch (Exception e){
                throw new RPCException("Error setting provenance data in call", e);
            }
        }

        CallInvocation invocation = new CallInvocation(call, this, null);
        invocation.setTimeout(defaultTimeout);
        if(securityMethod==PRIVATE_KEY_SECURITY){
            if(key!=null){
                invocation.setSecurityMethod(securityMethod);
                invocation.setPrivateKey(key);
                invocation.setSigningUserId(signingUserId);
            } else {
                throw new RPCException("Security set to private key, but no key presen");
            }
        } else if(securityMethod==TICKET_SECURITY){
            if(ticket!=null){
                invocation.setSecurityMethod(securityMethod);
                invocation.setTicket(ticket);
            } else {
                throw new RPCException("Security set to ticket, but no ticket present");
            }
        }

        if(!call.isAuthenticationRequired() || sessionIdRequired==false || (sessionIdRequired==true && sessionId!=null && !sessionId.trim().equals("") && securityMethod==SESSION_SECURITY)){
            notifyTransferStarted();
            if (sessionId != null && !sessionId.trim().equals("")) {
                invocation.sendSyncCall(serverUrl + ";jsessionid=" + sessionId);
            } else {
                invocation.sendSyncCall(serverUrl);
            }
            if(calls.size()==0){
                notifyTransferFinished();
            }
        } else {
            throw new RPCException("Authentication is required for this synchronous call");
        }
    }

    /** Send a call object */
    private void sendCall(CallInvocation invocation){
        invocation.setTimeout(defaultTimeout);
        calls.put(invocation.getCallId(), invocation);
        notifyTransferStarted();
        if (sessionId != null && !sessionId.trim().equals("")) {
            invocation.sendCall(serverUrl + ";jsessionid=" + sessionId);
        } else {
            invocation.sendCall(serverUrl);
        }
    }

    /** Send all of the calls in the queue */
    private void processQueue(){
        new Thread(new Runnable(){
            public void run(){
                CallInvocation invocation;

                while(queuedCalls.size()>0){
                    invocation = queuedCalls.remove(0);
                    sendCall(invocation);
                }
            }
        }).start();
    }

    /** Close this client */
    public void close(){

    }

    /** Call has been completed */
    public void callCompleted(CallInvocation invocation) {
        if (calls.containsKey(invocation.getCallId())) {
            calls.remove(invocation.getCallId());
        }

        if(calls.size()==0){
            notifyTransferFinished();
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setSecurityMethod(int securityMethod) {
        this.securityMethod = securityMethod;
    }

    /** Set the security ticket */
    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }

    /** Get the security ticket */
    public Ticket getTicket(){
        return this.ticket;
    }

    /** Get an encoded ticket */
    public String getEncodedTicket() throws IOException {
        if(ticket!=null){
            return Base64.encodeBytes(SerializationUtils.serialize(ticket), Base64.DONT_BREAK_LINES);
        } else {
            throw new IOException("No ticket to encode");
        }
    }

    /** Set an encoded ticket */
    public void setEncodedTicket(String encodedTicket) throws IOException, ClassNotFoundException {
        ticket = (Ticket)SerializationUtils.deserialize(Base64.decode(encodedTicket));
    }
    
    public int getSecurityMethod() {
        return securityMethod;
    }
    
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        listeners = new ArrayList<>();
        calls = new HashMap<>();
        queuedCalls = new ArrayList<>();
        defaultTimeout = stream.readInt();
        desiredSessionTimeout = stream.readInt();
        serverUrl = stream.readObject().toString();

        Object sessionObj = stream.readObject();
        if(sessionObj!=null){
            sessionId = sessionObj.toString().trim();
            // Set to null if it is an empty string
            if(sessionId.equalsIgnoreCase("")){
                sessionId = null;
            }
        } else {
            sessionId = null;
        }

        sessionIdRequired = stream.readBoolean();
        ticket = (Ticket)stream.readObject();
        securityMethod = stream.readInt();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(defaultTimeout);
        out.writeInt(desiredSessionTimeout);
        out.writeObject(serverUrl);
        out.writeObject(sessionId);
        out.writeBoolean(sessionIdRequired);
        out.writeObject(ticket);
        out.writeInt(securityMethod);
    }

    /** Save to an XmlDataStore */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("RPCClient");
        store.add("DefaultTimeout", defaultTimeout);
        store.add("DesiredSessionTimeout", desiredSessionTimeout);
        store.add("ServerURL", serverUrl);
        store.add("SessionID", sessionId);
        store.add("SessionIDRequired", sessionIdRequired);
        store.add("SecurityMethod", securityMethod);
        store.add("SerializationMethod", serializationMethod);

        // Save the ticket as a byte array
        if(ticket!=null){
            try {
                store.add("TicketData", SerializationUtils.serialize(ticket));
            } catch (IOException ioe){
                throw new XmlStorageException("IOError serializing ticket: " + ioe.getMessage());
            }
        }

        // Save any provenance properties
        if(provenanceProperties!=null){
            store.add("ProvenanceProperties", provenanceProperties);
        }

        return store;
    }

    /** Recreate from an XmlDataStore */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        defaultTimeout = store.intValue("DefaultTimeout", 3600);
        desiredSessionTimeout = store.intValue("DesiredSessionTimeout", 1800);
        serverUrl = store.stringValue("ServerURL", null);
        sessionId = store.stringValue("SessionID", null);
        sessionIdRequired = store.booleanValue("SessionIDRequired", false);
        securityMethod = store.intValue("SecurityMethod", SESSION_SECURITY);
        serializationMethod = store.intValue("SerializationMethod", JAVA_SERIALIZATION);
        if(store.containsName("TicketData")){
            try {
                ticket = (Ticket)SerializationUtils.deserialize(store.byteArrayValue("TicketData"));
            } catch (Exception e){
                ticket = null;
                throw new XmlStorageException("Error deserializing ticket: " + e.getMessage());
            }
        } else {
            ticket = new Ticket();
        }

        if(store.containsName("ProvenanceProperties")){
            provenanceProperties = store.xmlDataStoreValue("ProvenanceProperties");
        } else {
            provenanceProperties = null;
        }
    }
}