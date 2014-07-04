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
import com.connexience.server.workflow.engine.*;
import java.util.*;
import java.io.*;

/**
 * This class tracks the library preparation process and returns a list of
 * messages to help debug the library setup prior to service execution. 
 * @author nhgh
 */
public class LibraryPreparationReport implements Serializable {
    /** The item requested was already present */
    public static final int ITEM_PRESENT = 0;

    /** The item requested did not exist */
    public static final int ITEM_NOT_FOUND = 1;

    /** The item requested was downloaded ok */
    public static final int ITEM_DOWNLOADED_OK = 2;

    /** The item requested could not be downloaded */
    public static final int ITEM_DOWNLOAD_FAILED = 3;

    /** Message from the download manager */
    public static final int DOWNLOAD_MANAGER_MESSAGE = 4;

    /** Library preparation information message */
    public static final int INFORMATION_MESSAGE = 5;
    
    /** Message list */
    private ArrayList<Message> messageList = new ArrayList<>();

    /** Invocation containing this report */
    private transient WorkflowInvocation parentInvocation = null;

    /** Add a message */
    public void addMessage(int status, String messageText){
        messageList.add(new Message(status, messageText));
    }

    /** Get a message iterator */
    public Iterator<Message> messages(){
        return messageList.iterator();
    }

    /** Get the number of messages */
    public int size(){
        return messageList.size();
    }

    /** Copy data from a report to this one */
    public void copyMessages(LibraryPreparationReport source){
        Iterator<Message> i = source.messages();
        while(i.hasNext()){
            messageList.add(i.next());
        }
    }
    
    @Override
    public String toString() {
        Iterator<Message> i = messageList.iterator();
        StringBuffer buffer = new StringBuffer();

        while(i.hasNext()){
            buffer.append(i.next() + "\n");
        }
        return buffer.toString();
    }

    /** Print the contents to a stream */
    public void printToStream(OutputStream stream){
        PrintWriter writer = new PrintWriter(stream);
        Iterator<Message> i = messages();
        while(i.hasNext()){
            writer.println(i.next());
        }
        writer.flush();
        writer.close();
    }

    /** Set the parent workflow invocation */
    public void setParentInvocation(WorkflowInvocation parentInvocation){
        this.parentInvocation = parentInvocation;
    }

    /** Get the parent workflow invocation */
    public WorkflowInvocation getParentInvocation(){
        return parentInvocation;
    }

    /** Get the invocation ID if there is a parent invocation */
    public String getInvocationId(){
        if(parentInvocation!=null){
            return parentInvocation.getInvocationId();
        } else {
            return "NoInvocationID";
        }
    }
    
    /** This class represents a single message in a preparation report */
    public class Message implements Serializable {
        private int status;
        private String message;

        public Message(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}