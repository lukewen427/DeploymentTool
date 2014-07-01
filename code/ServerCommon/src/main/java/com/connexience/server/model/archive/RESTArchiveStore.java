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
package com.connexience.server.model.archive;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.archive.rest.MessageUtils;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.InitialContext;

/**
 * This class provides a connexience archive store that uses a simple REST based service for its back end storage
 * @author swheater
 */
public class RESTArchiveStore extends ArchiveStore {
    /** Logger */
    private static final Logger logger = Logger.getLogger(RESTArchiveStore.class.getName());

    /** Service URL */
    private String serviceURL = null;

    /** Queue for Archiving Jobs */
    private Queue archivingQueue = null;

    /** Queue for Unarchiving Jobs */
    private Queue unarchivingQueue = null;

    /** Queue Connection Factory */
    private QueueConnectionFactory queueConnectionFactory = null;

    public RESTArchiveStore() {
    }

    /** Start Archiving of DocumentRecord */
    @Override
    public void startArchiving(String documentId, String dataStoreId) throws ConnexienceException {
        try {
            logger.info("**** REST Archiving: [" + documentId + "] from [" + dataStoreId + "]");

            if ((archivingQueue == null) || (unarchivingQueue == null) || (queueConnectionFactory == null))
                initQueues();
            
            if (queueConnectionFactory != null)
            {
                QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
                queueConnection.start();
                Session         queueSession    = queueConnection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = queueSession.createProducer(archivingQueue);

                Message message = queueSession.createMessage();

                message.setIntProperty(MessageUtils.OPCODE_PROPERTYNAME, MessageUtils.ARCHIVE_REQUEST_OPCODEVALUE);
                message.setStringProperty(MessageUtils.SERVICEURL_PROPERTYNAME, serviceURL);
                message.setStringProperty(MessageUtils.DOCUMENTID_PROPERTYNAME, documentId);
                message.setStringProperty(MessageUtils.DATASTOREID_PROPERTYNAME, dataStoreId);

                messageProducer.send(message);

                messageProducer.close();
                queueSession.close();
                queueConnection.close();
            }
            else
            {
                logger.warn("Unable to achive document, unable to queue request: [" + documentId + "]");
                throw new ConnexienceException("Unable to achive document, unable to queue request: [" + documentId + "]");
            }
        } catch (ConnexienceException connexienceException){
            throw connexienceException;
        } catch (Exception exception){
            throw new ConnexienceException("Error starting REST archiving: " + exception.getMessage());
        }
    }

    /** Start Archiving of DocumentRecord */
    @Override
    public void startUnarchiving(String documentId, String dataStoreId) throws ConnexienceException {
        try {
            logger.info("**** REST Unarchiving: [" + documentId + "] from [" + dataStoreId + "]");

            if ((archivingQueue == null) || (unarchivingQueue == null) || (queueConnectionFactory == null))
                initQueues();

            if (queueConnectionFactory != null)
            {
                QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
                queueConnection.start();
                Session         queueSession    = queueConnection.createSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = queueSession.createProducer(unarchivingQueue);

                Message message = queueSession.createMessage();

                message.setIntProperty(MessageUtils.OPCODE_PROPERTYNAME, MessageUtils.UNARCHIVE_REQUEST_OPCODEVALUE);
                message.setStringProperty(MessageUtils.SERVICEURL_PROPERTYNAME, serviceURL);
                message.setStringProperty(MessageUtils.DOCUMENTID_PROPERTYNAME, documentId);
                message.setStringProperty(MessageUtils.DATASTOREID_PROPERTYNAME, dataStoreId);

                messageProducer.send(message);

                messageProducer.close();
                queueSession.close();
                queueConnection.close();
            }
            else
            {
                logger.warn("Unable to unachive document, unable to queue request: [" + documentId + "]");
                throw new ConnexienceException("Unable to unachive document, unable to queue request: [" + documentId + "]");
            }
        } catch (ConnexienceException connexienceException){
            throw connexienceException;
        } catch (Exception exception){
            throw new ConnexienceException("Error starting REST unarchiving: " + exception.getMessage());
        }
    }
    
    /** Set the Service URL */
    public String getServiceURL() {
        return serviceURL;
    }
    
    /** Get the Service URL */
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    private void initQueues() throws ConnexienceException {
        try {
            InitialContext initialContext = new InitialContext();
    
            archivingQueue         = (Queue) initialContext.lookup("queue/RESTArchivingQueue");
            unarchivingQueue       = (Queue) initialContext.lookup("queue/RESTUnarchivingQueue");
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup("ConnectionFactory");
        } catch (Throwable throwable){
            logger.warn("Unable to obtain REST archive queues or queue connection factory", throwable);
            archivingQueue         = null;
            unarchivingQueue       = null;
            queueConnectionFactory = null;
            throw new ConnexienceException("Unable to obtain REST archive queues or queue connection factory", throwable);
        }
    }
}
