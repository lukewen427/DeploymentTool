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

import java.util.*;
/**
 * This abstract class defines the client side functionality of the data processor service. It
 * allows the caller to send data references to the server asynchronously.
 * @author hugo
 */
public abstract class DataProcessorClient {
    /** Service URL */
    private String url;
    
    /** Listeners */
    private Vector<DataProcessorClientListener> listeners = new Vector<>();
    
    /** Response timeout in seconds */
    private int timeout = 3600;
    
    /** Set the service URL string */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /** Set the response timeout */
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    
    /** Get the response timeout */
    public int getTimeout(){
        return timeout;
    }

    /** Remove all the listeners */
    public void removeAllListeners(){
        listeners.clear();
    }
    
    /** Add a listener */
    public void addListener(DataProcessorClientListener listener){
        listeners.add(listener);
    }
    
    /** Remove a listener */
    public void removeListener(DataProcessorClientListener listener){
        listeners.remove(listener);
    }
    
    /** Notify sucessful message transmission */
    protected void notifyMessageSent(){
        Vector<DataProcessorClientListener> tempList = new Vector<>(listeners);
        Iterator<DataProcessorClientListener> i = tempList.iterator();
        while(i.hasNext()){
            i.next().messageRecieved();
        }
    }
    
    /** Notify message failure */
    protected void notifyMessageRejected(String errorMessage){
        Vector<DataProcessorClientListener> tempList = new Vector<>(listeners);
        Iterator<DataProcessorClientListener> i = tempList.iterator();
        while(i.hasNext()){
            i.next().messageRejected(errorMessage);
        }
    }
    
    /** Invoke the service asynchronously */
    public abstract void invoke(DataProcessorCallMessage message) throws DataProcessorException;

    /** Terminate a service instance if possible */
    public abstract void terminate(DataProcessorCallMessage message) throws DataProcessorException;
}
