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
package com.connexience.server.workflow.api.impl;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.Base64;
import com.connexience.server.util.SerializationUtils;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.cloud.JMSAttachThread;
import com.connexience.server.workflow.cloud.WorkflowJMSListener;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.log4j.Logger;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;

/**
 * This class can send certain workflow update calls via JMS to prevent blocking
 * the workflow client. 
 * @author hugo
 */
public class JMSAPIHelper implements WorkflowJMSListener {
    private static Logger logger = Logger.getLogger(JMSAPIHelper.class);
    private boolean attached = false;
    /**
     * JMS Session
     */
    private Session session = null;
    
    /**
     * JMS Workflow manager messages queue
     */
    private Queue managerQueue = null;
    
    /**
     * Producer to send updates back to the server
     */
    MessageProducer producer = null;
    
    /**
     * JMS Message connection
     */
    private Connection connection = null;
    
    /** 
     * Thread to reattach to server
     */
    private JMSAttachThread jmsAttacher; 
    
    /**
     * Parent API provider
     */
    private ApiProvider parentProvider;
    
    @Override
    public boolean isJmsAttached() {
        return attached;
    }

    public JMSAPIHelper(ApiProvider parentProvider) {
        this.parentProvider = parentProvider;
    }

    @Override
    public void attachJms(String hostname, int port, String user, String password, String queueName, Integer bufferSize) throws Exception {
        attached = true;
        try {
            logger.debug("Attaching JMS to: " + hostname + ":" + port + " on topic: " + queueName);
            Map<String, Object> params = new HashMap<>();
            params.put(TransportConstants.HOST_PROP_NAME, hostname);
            params.put(TransportConstants.PORT_PROP_NAME, port);

            TransportConfiguration configuration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
            HornetQConnectionFactory factory = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, configuration);
            if (bufferSize != null) {
                logger.debug("Consumer window size: " + bufferSize);
                factory.setConsumerWindowSize(bufferSize.intValue());
            } else {
                logger.debug("Default consumer window size");
            }

            connection = factory.createConnection(user, password);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            managerQueue = session.createQueue(queueName);
            producer = session.createProducer(managerQueue);


            connection.setExceptionListener(new ExceptionListener() {
                @Override
                public void onException(JMSException jmse) {
                    if (jmse.getErrorCode().equals("DISCONNECT")) {
                        logger.debug("JMS Detached");
                        attached = false;
                        if (jmsAttacher != null) {
                            jmsAttacher.setInterval(10000);
                        }
                    } else {
                        logger.error("Unrecognised JMS Error code: " + jmse.getErrorCode());
                    }
                }
            });
            connection.start();
            logger.debug("JMS Attached: " + queueName);
        } catch (Exception e) {
            attached = false;
            throw e;
        }
    }

    @Override
    public void detachJms() {
        try {
            logger.debug("Detaching JMS: WorkflowManagerQueue");
            if (jmsAttacher != null) {
                jmsAttacher.stop();
            }
            connection.stop();
            connection.close();
            logger.debug("JMS Detached: WorkflowManagerQueue");
            attached = false;
        } catch (Exception e) {
            logger.error("Error detaching JMS", e);
        }
    }

    public void setJmsAttacher(JMSAttachThread jmsAttacher) {
        this.jmsAttacher = jmsAttacher;
    }
    
    @Override
    public JMSAttachThread getAttacherThread() {
        return jmsAttacher;
    }
    
    public void setInvocationEngineId(Ticket ticket, String invocationId, String engineId) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("setInvocationEngineId");
            msg.setStringProperty("invocationId", invocationId);
            msg.setStringProperty("engineId", engineId);
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for setInvocationEngineId: " + e.getMessage(), e);
        }
    }
    
    public void setWorkflowStatus(Ticket ticket, String invocationId, int status, String message) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("setWorkflowStatus");
            msg.setStringProperty("invocationId", invocationId);
            msg.setIntProperty("status", status);
            if(message!=null){
                msg.setStringProperty("message", message);
            } else {
                msg.setStringProperty("message", "");
            }
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for setWorkflowStatus: " + e.getMessage(), e);
        }
    }
    
    public void setCurrentBlock(Ticket ticket, String invocationId, String contextId, int percentComplete) throws ConnexienceException {
        if(parentProvider.invocationPresent(invocationId)){
            try {
                TextMessage msg = session.createTextMessage("setCurrentBlock");
                msg.setStringProperty("invocationId", invocationId);
                msg.setStringProperty("contextId", contextId);
                msg.setIntProperty("percentComplete", percentComplete);
                msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
                producer.send(msg);
            } catch (Exception e){
                throw new ConnexienceException("Error sending JMS message for setCurrentBlock: " + e.getMessage(), e);
            }
        }
    }
    
    public void updateServiceLog(Ticket ticket, String invocationId, String contextId, String outputData, String statusText, String statusMessage) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("updateServiceLog");
            msg.setStringProperty("invocationId", invocationId);
            msg.setStringProperty("contextId", contextId);
            msg.setStringProperty("outputData", outputData);
            msg.setStringProperty("statusText", statusText);
            msg.setStringProperty("statusMessage", statusMessage);
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for updateServiceLog: " + e.getMessage(), e);
        }        
    }
    
    public void updateServiceLogMessage(Ticket ticket, String invocationId, String contextId, String statusText, String statusMessage) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("updateServiceLogMessage");
            msg.setStringProperty("invocationId", invocationId);
            msg.setStringProperty("contextId", contextId);
            msg.setStringProperty("statusText", statusText);
            msg.setStringProperty("statusMessage", statusMessage);
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for updateServiceLogMessage: " + e.getMessage(), e);
        }
    }
    
    public void notifyWorkflowDequeued(Ticket ticket, String invocationId) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("notifyWorkflowDequeued");
            msg.setStringProperty("invocationId", invocationId);
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for notifyWorkflowDequeued: " + e.getMessage(), e);
        }
    }
    
    public void notifyWorkflowStarted(Ticket ticket, String invocationId) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("notifyWorkflowStarted");
            msg.setStringProperty("invocationId", invocationId);
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for notifyWorkflowStarted: " + e.getMessage(), e);
        }        
    }
    
    public void notifyWorkflowFinished(Ticket ticket, String invocationId) throws ConnexienceException {
        try {
            TextMessage msg = session.createTextMessage("notifyWorkflowFinished");
            msg.setStringProperty("invocationId", invocationId);
            msg.setStringProperty("ticketData", Base64.encodeBytes(SerializationUtils.serialize(ticket)));
            producer.send(msg);
        } catch (Exception e){
            throw new ConnexienceException("Error sending JMS message for notifyWorkflowFinished: " + e.getMessage(), e);
        }          
    }
}
