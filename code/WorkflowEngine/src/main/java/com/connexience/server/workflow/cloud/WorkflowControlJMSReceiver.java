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
package com.connexience.server.workflow.cloud;

import com.connexience.server.workflow.cloud.execution.CloudWorkflowExecutionEngine;
import com.connexience.server.workflow.engine.WorkflowInvocation;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.log4j.*;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;

/**
 * This class listens to the workflow locks JMS topic and resumes workflows that
 * have completed
 *
 * @author hugo
 */
public class WorkflowControlJMSReceiver implements WorkflowJMSListener, MessageListener {

    private static Logger logger = Logger.getLogger(WorkflowControlJMSReceiver.class);
    private boolean attached = false;
    /**
     * JMS Session
     */
    private Session session = null;
    /**
     * JMS Workflow message topic
     */
    private Topic workflowTopic = null;
    /**
     * JMS Workflow message consumer
     */
    private MessageConsumer consumer = null;
    /**
     * JMS Message connection
     */
    private Connection connection = null;
    CloudWorkflowExecutionEngine engine;
    /**
     * Attacher thread
     */
    private JMSAttachThread jmsAttacher;

    public WorkflowControlJMSReceiver(CloudWorkflowExecutionEngine engine) {
        this.engine = engine;
    }

    public void setJmsAttacher(JMSAttachThread jmsAttacher) {
        this.jmsAttacher = jmsAttacher;
    }

    @Override
    public boolean isJmsAttached() {
        return attached;
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
            workflowTopic = session.createTopic(queueName);
            consumer = session.createConsumer(workflowTopic);

            consumer.setMessageListener(this);
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
            logger.debug("Detaching JMS: WorkflowControl");
            if (jmsAttacher != null) {
                jmsAttacher.stop();
            }
            connection.stop();
            connection.close();
            logger.debug("JMS Detached: WorkflowControl");
            attached = false;
        } catch (Exception e) {
            logger.error("Error detaching JMS", e);
        }
    }

    @Override
    public JMSAttachThread getAttacherThread() {
        return jmsAttacher;
    }

    @Override
    public void onMessage(Message msg) {
        try {
            if (msg instanceof TextMessage) {
                msg.acknowledge();
                TextMessage tm = (TextMessage) msg;
                if (tm.getText().equals("LockCompleted")) {
                    // Lock has been completed
                    String invocationId = tm.getStringProperty("InvocationID");
                    long lockId = tm.getLongProperty("LockID");
                    String contextId = tm.getStringProperty("ContextID");
                    int failures = tm.getIntProperty("Failures");
                    WorkflowInvocation invocation = engine.getExecutionEngine().getInvocation(invocationId);
                    if (invocation != null) {
                        logger.debug("Lock completed message accepted");
                        logger.debug("Lock: " + lockId + " completed for block: " + contextId + " in invocation: " + invocationId + " with " + failures + " failed child workflows");
                        invocation.resumeAfterLock(contextId, lockId, failures);
                    }
                } else if (tm.getText().equals("ReRegister")) {
                    // Re-register the workflow engine
                    logger.debug("Reregister message accepted");
                    new Thread(new Runnable() {
                        public void run() {
                            engine.notifyEngineStartup();
                        }
                    }).start();

                } else if (tm.getText().equals("ResetRMIComms")) {
                    logger.debug("RMI Reset message accepted");
                    engine.getApiProvider().resetRmiStatus();
                    
                } else if(tm.getText().equals("LibraryChanged")){
                    logger.debug("LibraryChanged message accepted");
                    String libaryName = tm.getStringProperty("libraryName");
                    String libraryDocumentId = tm.getStringProperty("libraryDocumentId");
                    engine.getServiceLibrary().getInformationCache().evictLibraryByName(libaryName);
                    engine.getServiceLibrary().getInformationCache().evictLatestVersionId(libraryDocumentId);
                    
                } else if(tm.getText().equals("ServiceChanged")){
                    logger.debug("ServiceChanged message acepted");
                    String serviceId = tm.getStringProperty("serviceId");
                    engine.getServiceLibrary().getInformationCache().evictLatestVersionId(serviceId);
                    engine.getServiceLibrary().getInformationCache().evictService(serviceId);
                    
                } else if(tm.getText().equals("ClearInformationCache")){
                    logger.debug("ClearInformationCache message accepted");
                    engine.getServiceLibrary().getInformationCache().clearCache();

                } else if(tm.getText().equals("TerminateInvocation")){
                    String invocationId = tm.getStringProperty("InvocationID");
                    WorkflowInvocation invocation = engine.getExecutionEngine().getInvocation(invocationId);
                    if(invocation!=null){
                        logger.debug("Kill message for InvocationID=" + invocationId + " accepted");
                        invocation.kill();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}