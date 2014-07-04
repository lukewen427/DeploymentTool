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
package com.connexience.server.workflow.service.clients;

import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.rpc.*;
import org.pipeline.core.xmlstorage.*;
import java.io.*;

/**
 * This client uses the built in java RPC code to send messages to the 
 * data processor services.
 * @author hugo
 */
public class RPCDataProcessorClient extends DataProcessorClient implements CallInvocationListener, Serializable {
	private static final long serialVersionUID = 1L;

	/** Invoke the service */
    public void invoke(DataProcessorCallMessage message) throws DataProcessorException {
        try {
            
            CallObject call = new CallObject("processCallMessage");
            call.getCallArguments().add("CallMessage", message);
            
            RPCClient client = new RPCClient(message.getServiceUrl());
            client.setDefaultTimeout(getTimeout());
            client.asyncCall(call, this);
            
        } catch (XmlStorageException xmlse){
            throw new DataProcessorException("XML Serialization Error: " + xmlse.getMessage());
        } catch (RPCException rpce){
            throw new DataProcessorException("Communications Error: "+ rpce.getMessage());
        }
    }

    @Override
    public void terminate(DataProcessorCallMessage message) throws DataProcessorException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** The RPC call has failed */
    public void callFailed(CallObject call) {
        notifyMessageRejected(call.getStatusMessage());
    }

    /** The RPC call was sent Ok */
    public void callSucceeded(CallObject call) {
        notifyMessageSent();
    }    
}