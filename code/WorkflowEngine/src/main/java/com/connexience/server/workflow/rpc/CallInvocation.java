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
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;

import java.io.*;
import java.net.*;
import java.security.*;
import org.apache.log4j.Logger;

/**
 * This class tracks a call, the response from the server and the client side
 * listener if the call is operating asynchronously
 * @author hugo
 */
public class CallInvocation {
    private static Logger logger = Logger.getLogger(CallInvocation.class);
    
    /** Call object that was sent to the server */
    private CallObject call;

    /** ID of the call */
    private String callId;
    
    /** Listener object */
    private CallInvocationListener listener = null;
    
    /** Parent client object */
    private RPCClient parent;
    
    /** Thread used to send call to the server */
    private MessageThread msgThread;
    
    /** Response timeout in seconds */
    private int timeout = 3600;

    /** Key to sign call with */
    private PrivateKey key;

    /** Ticket of calling user */
    private Ticket ticket = null;

    /** ID of the user signing calls */
    private String signingUserId;

    /** Should this call be signed */
    private int securityMethod = RPCClient.SESSION_SECURITY;

    /** Create with a call object */
    public CallInvocation(CallObject call, RPCClient parent) {
        this.call = call;
        this.callId = call.getCallId();
        this.listener = null;
    }
    
    /** Create with a call object and a listener */
    public CallInvocation(CallObject call, RPCClient parent, CallInvocationListener listener){
        this.call = call;
        this.callId = call.getCallId();
        this.listener = listener;
        this.parent = parent;
    }

    /** Create a call with no parent or listener. This is used for synchronous calls */
    public CallInvocation(CallObject call){
        this.call = call;
        this.callId = call.getCallId();
        this.listener = null;
        this.parent = null;
    }

    /** Set the ticket object */
    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }
    
    /** Set the private key */
    public void setPrivateKey(PrivateKey key){
        this.key = key;
    }

    public void setSigningUserId(String signingUserId) {
        this.signingUserId = signingUserId;
    }

    public String getSigningUserId() {
        return signingUserId;
    }

    public void setSecurityMethod(int securityMethod) {
        this.securityMethod = securityMethod;
    }

    /** Get the call object */
    public CallObject getCall(){
        return call;
    }
    
    /** Set the response timeout */
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    
    /** Get the response timeout */
    public int getTimeout(){
        return timeout;
    }
    
    /** Get the call id */
    public String getCallId(){
        return callId;
    }

    /** Get the parent RPC Client object */
    public RPCClient getParent(){
        return parent;
    }
    
    /** Send the call to the server and wait for the response */
    public void sendCall(String url) {
        msgThread = new MessageThread(this, url);
        msgThread.start();
    }

    /** Send a call syncrhronously */
    public void sendSyncCall(String url){
        msgThread = new MessageThread(this, url);

        msgThread.setRunning(true);
        msgThread.start();
        
        // Wait until the timeout
        while(msgThread.isRunning()){
            try {
                Thread.sleep(1);
            } catch (Exception e){}
        }
    }
    
    /** Call has completed */
    private void sendCallCompleted(){
        if(parent!=null){
            parent.callCompleted(this);
        }
    }
    
    /** Watchdog thread */
    private class WatchdogThread extends Thread {
        /** Message thread object */
        private MessageThread thread;
        
        /** Timeout */
        private int timeout = 3600;
        
        /** Terminate flag for this thread */
        private volatile boolean terminate = true;
        
        public WatchdogThread(MessageThread thread, int timeout){
            this.thread = thread;
            this.timeout = timeout;
            this.terminate = false;
            setDaemon(true);
        }
        
        public void run(){
            if(thread.isRunning()){
                long endTime = System.currentTimeMillis() + ((int)(timeout * 1.1) * 2000);
                while((System.currentTimeMillis()<endTime || thread.isRunning()==false) && terminate==false){
                    try {
                        Thread.sleep(10);
                    } catch (Exception e){}
                }
                
                // If we get this far, abort the call
                if(thread.isRunning() && terminate==false){
                    thread.abortCall();
                }
            }
        }
        
        /** Set the terminate flag */
        public void setTerminate(boolean terminate){
            this.terminate = terminate;
        }
    }

    /** Message sending thread */
    private class MessageThread extends Thread {
        /** URL to send message to */
        private String url;
        
        /** Call invocation parent */
        private CallInvocation parent;

        /** Is this thread still executing */
        private volatile boolean running = false;
                
        /** Input stream for reading responses */
        private InputStream inStream = null;
        
        /** Output stream for writing responses */
        private OutputStream outStream = null;
        
        /** Watchdog thread for this message */
        private WatchdogThread watchdog = null;
        
        MessageThread(CallInvocation parent, String url){
            this.parent = parent;
            this.url = url;
            setDaemon(true);
        }

        /** Abort the call */
        public synchronized void abortCall(){
            logger.debug("Aborting call");
            
            InputStream tmpInput = inStream;
            if(tmpInput!=null){
                try {
                    tmpInput.close();
                } catch (Exception e){}
            }
            
            OutputStream tmpOutput = outStream;
            if(tmpOutput!=null){
                try {
                    tmpOutput.close();
                } catch (Exception e){}
            }
            call.setStatus(CallObject.CALL_TIMEOUT);
            call.setStatusMessage("Call timed out");
        }
        
        /** Set whether this thread is running */
        public synchronized void setRunning(boolean running){
            this.running = running;
        }
        
        /** Is this thread running */
        public boolean isRunning(){
            return running;
        }
        
        /** Send the call */
        public void run(){
            setRunning(true);
            
            // Start the watchdog
            watchdog = new WatchdogThread(this, timeout);
            watchdog.start();            
            
            try {    
                // Setup the correct security
                if(securityMethod==RPCClient.PRIVATE_KEY_SECURITY && key!=null){
                    // Sign the data store if needed
                    call.getSignatureData().signObject(key, signingUserId);
                } else if(securityMethod==RPCClient.TICKET_SECURITY && ticket!=null){
                    // Bundle in the ticket
                    call.setTicket(ticket);
                }

                // Serialize the call and prepare a writer to post the data to the server
                XmlDataStore callData = new XmlDataStore("CallWrapper");
                callData.add("Call", call);
                call.setIOException(false);
                int method = parent.getParent().getSerializationMethod();
                try {
                    URL urlDef = new URL(url);
                    URLConnection connection = urlDef.openConnection();   
                    connection.setReadTimeout(timeout * 1000);
                    connection.setDoOutput(true);
                    if(method==RPCClient.XML_SERIALIZATION){
                        connection.setRequestProperty("format", "xml");
                        XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(callData);
                        writer.setDescriptionIncluded(true);    // Include description data
                        outStream = connection.getOutputStream();
                        writer.write(outStream);
                    } else {
                        connection.setRequestProperty("format", "java");
                        outStream = connection.getOutputStream();
                        ObjectOutputStream writer = new ObjectOutputStream(outStream);
                        writer.writeObject(call);
                        writer.flush();
                        writer.close();
                    }
                    outStream.flush();
                    outStream.close();
                    outStream = null;
                    
                    // Get the response data stream
                    inStream = connection.getInputStream();

                    // Try and load the results an XmlDataStore 
                    CallObject returnedCall = new CallObject();
                    XmlDataStore returnData = null;
                    
                    if(method==RPCClient.XML_SERIALIZATION){
                        XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(inStream);
                        returnData = reader.read();
                    } else {
                        ObjectInputStream reader = new ObjectInputStream(inStream);
                        try {
                            returnData = (XmlDataStore)reader.readObject();
                        } catch (ClassNotFoundException cnfe){
                            throw new RPCException("Error parsing binary return data");
                        } finally {
                            reader.close();
                        }
                    }
                    
                    returnedCall = (CallObject)returnData.xmlStorableValue("Call");
                    inStream.close();
                    inStream = null;
                    
                    // Merge the response arguments
                    call.mergeResponse(returnedCall);
                    if(call.getStatus()==CallObject.CALL_EXECUTED_OK){
                        if(parent!=null){
                            parent.sendCallCompleted();
                        }

                        if(listener!=null){
                            listener.callSucceeded(call);
                        }

                    } else {
                        if(parent!=null){
                            parent.sendCallCompleted();
                        }

                        if(listener!=null){
                            listener.callFailed(call);
                        }
                    }
                    setRunning(false);

                } catch (IOException e){
                    setRunning(false);
                    
                    // Keep existing error if there was a timeout
                    if(call.getStatus()!=CallObject.CALL_TIMEOUT){
                        call.setIOException(true);
                        call.setStatus(CallObject.CALL_EXECUTED_WITH_ERROR);
                        call.setStatusMessage("Error sending call" + e.getMessage());
                    }
                    
                    if(parent!=null){
                        parent.sendCallCompleted();
                    }

                    if(listener!=null){
                        listener.callFailed(call);
                    }

                } catch (RPCException rpce){
                    setRunning(false);
                    
                    // Keep existing error if there was a timeout
                    if(call.getStatus()!=CallObject.CALL_TIMEOUT){
                        call.setIOException(false);
                        call.setStatus(CallObject.CALL_EXECUTED_WITH_RPC_EXCEPTION);
                        call.setStatusMessage(rpce.getMessage());
                    }

                    if(parent!=null){
                        parent.sendCallCompleted();
                    }

                    if(listener!=null){
                        listener.callFailed(call);
                    }
                }

            } catch(XmlStorageException xmle){
                setRunning(false);
                call.setIOException(false);
                call.setStatus(CallObject.CALL_EXECUTED_WITH_ERROR);
                call.setStatusMessage("Error serializing call object: " + xmle.getMessage());

                if(parent!=null){
                    parent.sendCallCompleted();
                }

                if(listener!=null){
                    listener.callFailed(call);
                }
            } 
            
            watchdog.setTerminate(true);
        }
    }
}