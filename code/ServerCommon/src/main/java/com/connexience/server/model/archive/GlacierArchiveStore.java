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
import com.connexience.server.ejb.archive.glacier.MessageUtils;
import com.connexience.server.model.archive.glacier.SetupUtils;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.InitialContext;

/**
 * This class provides a connexience archive store that uses Amazon Glacier for its back end storage
 * @author swheater
 */
public class GlacierArchiveStore extends ArchiveStore {
    /** Logger */
    private static final Logger logger = Logger.getLogger(GlacierArchiveStore.class.getName());

    /** Amazon Access Key */
    private String accessKey = null;

    /** Amazon Secret Key */
    private String secretKey = null;

    /** Amazon AMS Domain Name */
    private String domainName = null;

    /** Amazon Glacier Vault Name */
    private String vaultName = null;

    /** Amazon SQA Queue URL */
    private String queueURL = null;

    /** Queue for Archiving Jobs */
    private Queue archivingQueue = null;

    /** Queue for Unarchiving Jobs */
    private Queue unarchivingQueue = null;

    /** Queue Connection Factory */
    private QueueConnectionFactory queueConnectionFactory = null;

    public GlacierArchiveStore() {
    }

    /** Setup AWS Glacier Archive: Vault, SNS Topic & SQS Queue */
    public void setupArchive()
    {
        SetupUtils.SQSInfo sqsInfo = SetupUtils.setupSQS(accessKey, secretKey, domainName, vaultName);

        if (sqsInfo != null)
        {
            queueURL = sqsInfo.queueURL;

            String topicARN = SetupUtils.setupSNS(accessKey, secretKey, domainName, vaultName, sqsInfo.queueARN);

            if (topicARN != null)
                SetupUtils.setupVault(accessKey, secretKey, domainName, vaultName, topicARN);
        }
    }

    /** Start Archiving of DocumentRecord */
    @Override
    public void startArchiving(String documentId, String dataStoreId) throws ConnexienceException {
        try {
            logger.info("**** AWS Glacier Archiving: [" + documentId + "] from [" + dataStoreId + "]");

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
                message.setStringProperty(MessageUtils.ACCESSKEY_PROPERTYNAME, accessKey);
                message.setStringProperty(MessageUtils.SECRETKEY_PROPERTYNAME, secretKey);
                message.setStringProperty(MessageUtils.DOMAINNAME_PROPERTYNAME, domainName);
                message.setStringProperty(MessageUtils.VAULTNAME_PROPERTYNAME, vaultName);
                message.setStringProperty(MessageUtils.DOCUMENTID_PROPERTYNAME, documentId);
                message.setStringProperty(MessageUtils.DATASTOREID_PROPERTYNAME, dataStoreId);
                message.setStringProperty(MessageUtils.ARCHIVESTOREID_PROPERTYNAME, getId());

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
            throw new ConnexienceException("Error starting Amazon Glacier archiving: " + exception.getMessage());
        }
    }

    /** Start Unarchiving of DocumentRecord */
    @Override
    public void startUnarchiving(String documentId, String dataStoreId) throws ConnexienceException {
        try {
            logger.info("**** AWS Glacier Unarchiving: [" + documentId + "] from [" + dataStoreId + "]");

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
                message.setStringProperty(MessageUtils.ACCESSKEY_PROPERTYNAME, accessKey);
                message.setStringProperty(MessageUtils.SECRETKEY_PROPERTYNAME, secretKey);
                message.setStringProperty(MessageUtils.DOMAINNAME_PROPERTYNAME, domainName);
                message.setStringProperty(MessageUtils.VAULTNAME_PROPERTYNAME, vaultName);
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
            throw new ConnexienceException("Error starting Amazon Glacier unarchiving: " + exception.getMessage());
        }
    }
    
    /** Set the Amazon Access Key */
    public String getAccessKey() {
        return accessKey;
    }
    
    /** Get the Amazon Access Key */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /** Get the Amazon Secret Key */
    public String getSecretKey() {
        return secretKey;
    }

    /** Set the Amazon Secret Key */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /** Get the Amazon Domain Name */
    public String getDomainName() {
        return domainName;
    }

    /** Set the Amazon Domain Name */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /** Get the Amazon Glacier Vault Name */
    public String getVaultName() {
        return vaultName;
    }

    /** Set the Amazon Glacier Vault Name */
    public void setVaultName(String vaultName) {
        this.vaultName = vaultName;
    }

    /** Get the Amazon SQS Queue URL */
    public String getQueueURL() {
        return queueURL;
    }

    /** Set the Amazon SQS Queue URL */
    public void setQueueURL(String queueURL) {
        this.queueURL = queueURL;
    }

    private void initQueues() throws ConnexienceException {
        try {
            InitialContext initialContext = new InitialContext();
    
            archivingQueue         = (Queue) initialContext.lookup("queue/AWSGlacierArchivingQueue");
            unarchivingQueue       = (Queue) initialContext.lookup("queue/AWSGlacierUnarchivingQueue");
            queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup("ConnectionFactory");
        } catch (Throwable throwable){
            logger.warn("Unable to obtain AWS Glacier queues or queue connection factory", throwable);
            archivingQueue         = null;
            unarchivingQueue       = null;
            queueConnectionFactory = null;
            throw new ConnexienceException("Unable to obtain AWS Glacier queues or queue connection factory", throwable);
        }
    }
}
