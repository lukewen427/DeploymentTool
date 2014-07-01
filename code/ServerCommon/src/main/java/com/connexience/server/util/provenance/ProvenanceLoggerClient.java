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
package com.connexience.server.util.provenance;

import com.connexience.server.model.logging.graph.GraphOperation;
import com.connexience.server.rmi.IProvenanceLogger;
import com.connexience.server.util.SerializationUtils;
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
 * Client to send a JMS Message to the provenance server to log an operation without going via the server
 * User: nsjw7
 * Date: Mar 15, 2011
 * Time: 4:02:04 PM
 */
public class ProvenanceLoggerClient extends AbstractProvenanceClient implements IProvenanceLogger {
    static Logger logger = Logger.getLogger(ProvenanceLoggerClient.class.getName());

    public ProvenanceLoggerClient() {
    }

    /** Create the default properties for the provenance logger client */
    public static void createDefaultProperties(){
        PreferenceManager.getSystemPropertyGroup("Provenance").add("Enabled", true, "Should JMS Messages be sent to the provenance server");
        PreferenceManager.getSystemPropertyGroup("Provenance").add("JMSServerHost", "localhost", "Host of the JMS Server");
        PreferenceManager.getSystemPropertyGroup("Provenance").add("JMSServerPort", 5445, "Port of the JMS Server");
        PreferenceManager.getSystemPropertyGroup("Provenance").add("JMSUser", "connexience", "Username of the JMS Queue");
        PreferenceManager.getSystemPropertyGroup("Provenance").add("JMSPassword", "1234", "Password for the JMS Queue");                
    }
    
    /**
     * Log a graph operation
     *
     * @param operation operation to be logged
     */
    public void log(GraphOperation operation) {
        try {
            boolean provEnabled = PreferenceManager.getSystemPropertyGroup("Provenance").booleanValue("Enabled", true);
            if (provEnabled) {
                sendMessage("ProvenanceQueue", operation); //ProvenanceQueue = queue name
            }
        } catch (Exception e) {
            logger.fatal("Cannot send message to proveneance service");
        }
    }

    /**
     * Send the message
     *
     * @param queueName queuename
     * @param message   graph operation
     * @throws Exception something went wrong
     */
    private void sendMessage(String queueName, GraphOperation message) throws Exception {

        Connection connection = null;
        try {
            Queue queue = HornetQJMSClient.createQueue(queueName);

            String jMSServerURLHost = PreferenceManager.getSystemPropertyGroup("Provenance").stringValue("JMSServerHost", "localhost");
            int jMSServerURLPort = PreferenceManager.getSystemPropertyGroup("Provenance").intValue("JMSServerPort", 5445);

            Map<String, Object> connectionParams = new HashMap<>();
            connectionParams.put(TransportConstants.HOST_PROP_NAME, jMSServerURLHost);
            connectionParams.put(TransportConstants.PORT_PROP_NAME, jMSServerURLPort);

            TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(),
                    connectionParams);

            HornetQConnectionFactory cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.QUEUE_CF, transportConfiguration);

            String jMSUser = PreferenceManager.getSystemPropertyGroup("Provenance").stringValue("JMSUser", "connexience");
            String jMSPassword = PreferenceManager.getSystemPropertyGroup("Provenance").stringValue("JMSPassword", "1234");

            connection = cf.createConnection(jMSUser, jMSPassword);

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageProducer producer = session.createProducer(queue);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            BytesMessage bm = session.createBytesMessage();

            byte[] data = SerializationUtils.serialize(message);
            bm.writeBytes(data);
            producer.send(bm);


        } catch (Exception e) {
            logger.info("Cannot send provenance message: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

    }


}
