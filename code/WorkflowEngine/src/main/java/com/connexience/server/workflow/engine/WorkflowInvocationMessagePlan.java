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
package com.connexience.server.workflow.engine;

import com.connexience.server.workflow.service.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.drawing.*;

import java.io.*;
import java.util.*;

/**
 * This class holds a list of messages that will be sent when a workflow is
 * executed. It is constructed by executing a workflow containing DataProcessor
 * blocks.
 * @author nhgh
 */
public class WorkflowInvocationMessagePlan implements Serializable, XmlStorable {
    /** Message is queued */
    public static final int MESSAGE_QUEUED = 0;

    /** Message has been sent */
    public static final int MESSAGE_SENT = 1;

    /** Message has been received by the server */
    public static final int MESSAGE_RECEIVED = 2;

    /** Message processing has completed */
    public static final int MESSAGE_PROCESSING_COMPLETED_OK = 3;

    /** Message Process has completed but with errors */
    public static final int MESSAGE_PROCESSING_COMPLETED_WITH_ERRORS = 4;

    /** Message was rejected */
    public static final int MESSAGE_REJECTED = 5;

    /** Message transmission exception */
    public static final int MESSAGE_TRANSMISSION_ERROR = 6;
    
    /** List of messages to be sent */
    private ArrayList<MessageItem> queuedMessages = new ArrayList<>();

    /** List of messages that have been sent */
    private ArrayList<MessageItem> sentMessages = new ArrayList<>();

    /** List of all messages */
    private ArrayList<MessageItem> allMessages = new ArrayList<>();

    /** Push a message onto the queue */
    public void push(DataProcessorCallMessage message, DataProcessorClient client){
        MessageItem item = new MessageItem(message, client);
        queuedMessages.add(item);
        allMessages.add(item);
    }

    /** Get the number of messages in the plan */
    public int getMessageCount(){
        return allMessages.size();
    }
    
    /** Get the position of a message in the plan */
    public int positionOfBlock(String contextId){
        for(MessageItem i : allMessages){
            if(i.getContextId().equals(contextId)){
                return allMessages.indexOf(i);
            }
        }
        return -1;
    }
    
    /** Create a message item without putting it on the queue */
    public MessageItem createMessageItem(DataProcessorCallMessage message, DataProcessorClient client){
        return new MessageItem(message, client);
    }
    
    /** Set the data processor client object. This is used after reloading a message plan
     * to set the client correctly in all of the message items */
    public void setDataProcessorClient(DataProcessorClient client){
        for(MessageItem i : allMessages){
            i.setClient(client);
        }
    }
    
    /** Push a MessageItem onto the queue */
    public void push(MessageItem item){
        queuedMessages.add(item);
        allMessages.add(item);
    }
    
    /** Push an item back into the queue */
    public void pushBack(MessageItem item){
        if(allMessages.contains(item)){
            queuedMessages.add(0, item);
            sentMessages.remove(item);
        }
    }
    
    /** Pop a message from the queue */
    public MessageItem pop(){
        if(queuedMessages.size()>0){
            MessageItem item = queuedMessages.remove(0);
            sentMessages.add(item);
            return item;
        } else {
            return null;
        }
    }

    /** Get a reference to the next item on the queue */
    public MessageItem peek(){
        if(queuedMessages.size()>0){
            return queuedMessages.get(0);
        } else {
            return null;
        }
    }
    
    /** Pop all the way through the queue to find a specified context ID. This method
     returns true if there was a valid message found during the search. Otherwise it
     returns false indicating that the specified contextId could not be found */
    public boolean moveToContextId(String contextId){
        boolean found = false;
        boolean validMessageAvailable = false;
        MessageItem i;
        while(found==false){
            i = pop();
            if(i!=null){
                if(i.getContextId().equals(contextId)){
                    pushBack(i);
                    found = true;
                    validMessageAvailable = true;
                } else {
                    i.setStatus(MESSAGE_PROCESSING_COMPLETED_OK);
                    i.setStatusText("");
                }
            } else {
                found = true;
                validMessageAvailable = false;
            }
        }
        return validMessageAvailable;
    }
    
    /** Get an enumeration of all messages */
    public Iterator<MessageItem> messages(){
        return allMessages.iterator();
    }

    /** Clear out the message plan */
    public void clear(){
        Iterator<MessageItem> messages = allMessages.iterator();
        MessageItem item;
        while(messages.hasNext()){
            item = messages.next();
            item.getClient().removeAllListeners();
            queuedMessages.remove(item);
            sentMessages.remove(item);
        }
        allMessages.clear();
    }

    /** Get a message for a block context Id */
    public MessageItem getMessageForContextId(String contextId){
        for(MessageItem item : allMessages){
            if(item.getMessage().getContextId().equals(contextId)){
                return item;
            }
        }
        return null;
    }

    @Override
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("MessagePlan");
        store.add("MessageCount", allMessages.size());
        int count = 0;
        for(MessageItem i : allMessages){
            store.add("Message" + count, i);
        }
        return store;
    }

    @Override
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int messageCount = store.intValue("MessageCount", 0);
        MessageItem msg;
        allMessages.clear();
        sentMessages.clear();
        queuedMessages.clear();
        for(int i=0;i<messageCount;i++){
            msg = (MessageItem)store.xmlStorableValue("Message" + i);
            allMessages.add(msg);
            if(msg.getStatus()==MESSAGE_QUEUED){
                queuedMessages.add(msg);
            } else {
                sentMessages.add(msg);
            }
        }
    }

    
    /** Message item and its associated client */
    public class MessageItem implements Serializable, XmlStorable {
        /** Invocation message */
        private DataProcessorCallMessage message;

        /** Communication client */
        private DataProcessorClient client;

        /** Message status */
        private int status = MESSAGE_QUEUED;

        /** Status text */
        private String statusText = "";

        /** Server output text */
        private String commandOutput = "";
        
        /** Is it ok to retry this message if there are failures */
        private boolean okToRetry = false;
        
        /** Remaining retries before we give up */
        private int remainingRetries = 1;

        /** Construct with message and client */
        public MessageItem(DataProcessorCallMessage message, DataProcessorClient client){
            this.message = message;
            this.client = client;
            okToRetry = message.isOkToRetry();
            remainingRetries = message.getRetryAttempts();
        }

        /** Get the status text */
        public String getStatusText(){
            return statusText;
        }

        /** Set the status text */
        public void setStatusText(String statusText){
            this.statusText = statusText;
        }
        
        /** Set the output text */
        public void setCommandOutput(String commandOutput){
            this.commandOutput = commandOutput;
        }

        /** Get the output text */
        public String getCommandOutput(){
            return commandOutput;
        }
        
        /** Get the status */
        public synchronized int getStatus(){
            return status;
        }

        /** Set the message status */
        public synchronized void setStatus(int status){
            this.status = status;
        }
        
        /** Get the call message */
        public DataProcessorCallMessage getMessage(){
            return message;
            
        }

        /** Get the communication client */
        public DataProcessorClient getClient(){
            return client;
        }
        
        /** Set the communication client */
        public void setClient(DataProcessorClient client){
            this.client = client;
        }

        /** Is it OK to retry sending this message */
        public boolean isOkToRetry() {
            return okToRetry;
        }

        /** Get the number of time this message can be retried */
        public int getRemainingRetries() {
            return remainingRetries;
        }

        public void setOkToRetry(boolean okToRetry) {
            this.okToRetry = okToRetry;
        }

        public void setRemainingRetries(int remainingRetries) {
            this.remainingRetries = remainingRetries;
        }

        public String getContextId(){
            return message.getContextId();
        }
        
        @Override
        public XmlDataStore storeObject() throws XmlStorageException {
            XmlDataStore store = new XmlDataStore("MessageItem");
            store.add("Message", message);
            store.add("CommandOutput", commandOutput);
            store.add("OkToRetry", okToRetry);
            store.add("RemainingRetries", remainingRetries);
            store.add("Status", status);
            store.add("StatusText", statusText);
            return store;
        }

        @Override
        public void recreateObject(XmlDataStore store) throws XmlStorageException {
            message = (DataProcessorCallMessage)store.xmlStorableValue("Message");
            commandOutput = store.stringValue("CommandOutput", "");
            okToRetry = store.booleanValue("OkToRetry", false);
            remainingRetries = store.intValue("RemainingRetries", 1);
            status = store.intValue("Status", MESSAGE_QUEUED);
            statusText = store.stringValue("StatusText", "");
        }
    }
}