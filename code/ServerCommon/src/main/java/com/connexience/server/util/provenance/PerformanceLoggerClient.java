/**
 * e-Science Central Copyright (C) 2008-2013 School of Computing Science,
 * Newcastle University
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation at: http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.util.provenance;

import com.connexience.server.model.logging.performance.Execution;
import com.connexience.server.model.logging.performance.WorkflowEngineStats;
import com.connexience.server.model.logging.performance.WorkflowEngineStatusChange;
import com.connexience.server.rmi.IPerformanceLogger;
import com.connexience.server.util.SerializationUtils;
import java.io.Serializable;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.jboss.logging.Logger;
import org.pipeline.core.xmlstorage.prefs.PreferenceManager;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Client to send a JMS Message to the provenance server to log an operation
 * without going via the server User: nsjw7 Date: Mar 15, 2011 Time: 4:02:04 PM
 */
public class PerformanceLoggerClient extends AbstractProvenanceClient implements IPerformanceLogger {

    static Logger logger = Logger.getLogger(PerformanceLoggerClient.class.getName());
    public static boolean enabled = true;

    public PerformanceLoggerClient() {
    }

    /** Create default performance logging preferences */
    public static void createDefaultProperties(){
        PreferenceManager.getSystemPropertyGroup("Performance").add("Enabled", true);
        PreferenceManager.getSystemPropertyGroup("Performance").add("JMSServerHost", "localhost");
        PreferenceManager.getSystemPropertyGroup("Performance").add("JMSServerPort", 5445);
        PreferenceManager.getSystemPropertyGroup("Performance").add("JMSUser", "connexience");
        PreferenceManager.getSystemPropertyGroup("Performance").add("JMSPassword", "1234");
        PreferenceManager.getSystemPropertyGroup("Performance").add("EngineDataFilterConstant", 0.8);
        PreferenceManager.getSystemPropertyGroup("Performance").add("EngineDataSampleInterval", 500);
        PreferenceManager.getSystemPropertyGroup("Performance").add("EngineDataSendInterval", 5000);
        PreferenceManager.getSystemPropertyGroup("Performance").add("SendEngineDataWhenIdle", true);
    }
    
    /**
     * Log a graph operation
     *
     * @param operation operation to be logged
     */
    public void log(Execution operation) {
        try {
            boolean provEnabled = PreferenceManager.getSystemPropertyGroup("Performance").booleanValue("Enabled", true);
            if (provEnabled) {
                sendMessage("PerformanceQueue", operation); //PerformanceQueue = queue name
            }
        } catch (Exception e) {
            logger.fatal("Cannot send message to Performance service");
        }
    }

    /**
     * Log a global engine status
     */
    public void log(WorkflowEngineStats stats) {
        try {
            boolean provEnabled = PreferenceManager.getSystemPropertyGroup("Performance").booleanValue("Enabled", true);
            if (provEnabled) {
                sendMessage("PerformanceQueue", stats); //PerformanceQueue = queue name
            }
        } catch (Exception e) {
            logger.fatal("Cannot send message to Performance service");
        }
    }

    /** 
     * Log a workflow engine status change message
     */
    public void log(WorkflowEngineStatusChange statusChange){
        try {
            boolean provEnabled = PreferenceManager.getSystemPropertyGroup("Performance").booleanValue("Enabled", true);
            if (provEnabled) {
                sendMessage("PerformanceQueue", statusChange); //PerformanceQueue = queue name
            }
        } catch (Exception e) {
            logger.fatal("Cannot send message to Performance service");
        }        
    }
    
    /**
     * Send the message
     *
     * @param queueName queuename
     * @param message graph operation
     * @throws Exception something went wrong
     */
    private void sendMessage(String queueName, Serializable message) throws Exception {
        if (PerformanceLoggerClient.enabled) {
            try {
                Connection connection = null;
                try {
                    Queue queue = HornetQJMSClient.createQueue(queueName);

                    String jMSServerURLHost = PreferenceManager.getSystemPropertyGroup("Performance").stringValue("JMSServerHost", "localhost");
                    int jMSServerURLPort = PreferenceManager.getSystemPropertyGroup("Performance").intValue("JMSServerPort", 5445);

                    Map<String, Object> connectionParams = new HashMap<>();
                    connectionParams.put(TransportConstants.HOST_PROP_NAME, jMSServerURLHost);
                    connectionParams.put(TransportConstants.PORT_PROP_NAME, jMSServerURLPort);

                    TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(),
                            connectionParams);

                    HornetQConnectionFactory cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.QUEUE_CF, transportConfiguration);

                    String jMSUser = PreferenceManager.getSystemPropertyGroup("Performance").stringValue("JMSUser", "connexience");
                    String jMSPassword = PreferenceManager.getSystemPropertyGroup("Performance").stringValue("JMSPassword", "1234");

                    connection = cf.createConnection(jMSUser, jMSPassword);

                    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                    MessageProducer producer = session.createProducer(queue);
                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);

                    BytesMessage bm = session.createBytesMessage();

                    byte[] data = SerializationUtils.serialize(message);
                    bm.writeBytes(data);
                    producer.send(bm);

                } finally {
                    if (connection != null) {
                        connection.close();
                    }
                }
            } catch (Exception e) {
                logger.info("Cannot send performance message: " + e.getMessage() + " - disabling performance until next restart");
                PerformanceLoggerClient.enabled = false;
            }
        }
    }

}
