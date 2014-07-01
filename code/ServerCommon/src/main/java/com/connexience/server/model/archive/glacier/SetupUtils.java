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
package com.connexience.server.model.archive.glacier;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.model.CreateVaultRequest;
import com.amazonaws.services.glacier.model.CreateVaultResult;
import com.amazonaws.services.glacier.model.SetVaultNotificationsRequest;
import com.amazonaws.services.glacier.model.VaultNotificationConfig;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SetupUtils
{
    private static final Logger logger = Logger.getLogger(SetupUtils.class.getName());

    public static class SQSInfo
    {
        public SQSInfo(String queueARN, String queueURL)
        {
            this.queueARN = queueARN;
            this.queueURL = queueURL;
        }

        public String queueARN;
        public String queueURL;
    }
    
    /* Setup (create) an AMS SQS Queue */
    public static SQSInfo setupSQS(String accessKey, String secretKey, String domainName, String vaultName)
    {
        SQSInfo sqsInfo = null;
        try
        {
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            AmazonSQSClient amazonSQSClient = new AmazonSQSClient(awsCredentials);
            amazonSQSClient.setEndpoint("https://sqs." + domainName + ".amazonaws.com/");

            String             queueName          = vaultName + "-inkspot_glacier-queue";
            CreateQueueRequest createQueueRequest = new CreateQueueRequest();
            createQueueRequest.withQueueName(queueName);

            CreateQueueResult createQueueResult = amazonSQSClient.createQueue(createQueueRequest);
            if (createQueueResult != null)
            {
                String queueURL = createQueueResult.getQueueUrl();

                GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest();
                getQueueAttributesRequest.withQueueUrl(queueURL);
                getQueueAttributesRequest.withAttributeNames("QueueArn");
            
                GetQueueAttributesResult getQueueAttributesResult = amazonSQSClient.getQueueAttributes(getQueueAttributesRequest);
            
                if (getQueueAttributesResult != null)
                {
                    String queueARN = getQueueAttributesResult.getAttributes().get("QueueArn");

                    Statement sqsStatement = new Statement(Effect.Allow);
                    sqsStatement.withPrincipals(Principal.AllUsers);
                    sqsStatement.withActions(SQSActions.SendMessage);
                    sqsStatement.withResources(new Resource(queueARN));

                    Policy sqsPolicy = new Policy();
                    sqsPolicy.withStatements(sqsStatement);
                    
                    Map<String, String> sqsAttributes = new HashMap<>();
                    sqsAttributes.put("Policy", sqsPolicy.toJson());

                    SetQueueAttributesRequest setQueueAttributesRequest = new SetQueueAttributesRequest();
                    setQueueAttributesRequest.withQueueUrl(queueURL);
                    setQueueAttributesRequest.withAttributes(sqsAttributes);
                    
                    amazonSQSClient.setQueueAttributes(setQueueAttributesRequest);
                    
                    sqsInfo = new SQSInfo(queueARN, queueURL);
                }
                else
                    logger.warn("Unable to get queue attributes: \"" + queueName + "\"");
            }
            else
                logger.warn("Unable to create queue: \"" + queueName + "\"");
            
            amazonSQSClient.shutdown();
        }
        catch (AmazonServiceException amazonServiceException)
        {
            logger.warn("AmazonServiceException: " + amazonServiceException);
            logger.debug(amazonServiceException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            logger.warn("IllegalArgumentException: " + illegalArgumentException);
            logger.debug(illegalArgumentException);
        }
        catch (AmazonClientException amazonClientException)
        {
            logger.warn("AmazonClientException: " + amazonClientException);
            logger.debug(amazonClientException);
        }
        catch (Throwable throwable)
        {
            logger.warn("Throwable: " + throwable);
            logger.debug(throwable);
        }

        return sqsInfo;
    }

    /* Setup (create) an AMS SNS Topic */
    public static String setupSNS(String accessKey, String secretKey, String domainName, String vaultName, String queueARN)
    {
        String topicARN = null;
        try
        {
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            AmazonSNSClient amazonSNSClient = new AmazonSNSClient(awsCredentials);
            amazonSNSClient.setEndpoint("https://sns." + domainName + ".amazonaws.com/");

            String             topicName          = vaultName + "-inkspot_glacier-topic";
            CreateTopicRequest createTopicRequest = new CreateTopicRequest();
            createTopicRequest.withName(topicName);

            CreateTopicResult createTopicResult = amazonSNSClient.createTopic(createTopicRequest);
            if (createTopicResult != null)
            {
                topicARN = createTopicResult.getTopicArn();

                SubscribeRequest subscribeRequest = new SubscribeRequest();
                subscribeRequest.withTopicArn(topicARN);
                subscribeRequest.withProtocol("sqs");
                subscribeRequest.withEndpoint(queueARN);

                SubscribeResult subscribeResult = amazonSNSClient.subscribe(subscribeRequest);
                if (subscribeResult == null)
                    logger.warn("Unable to subscribe topic: \"" + topicName + "\" to queue: \"" + queueARN + "\"");
            }
            else
                logger.warn("Unable to create topic: \"" + topicName + "\"");
            
            amazonSNSClient.shutdown();
        }
        catch (AmazonServiceException amazonServiceException)
        {
            logger.warn("AmazonServiceException: " + amazonServiceException);
            logger.debug(amazonServiceException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            logger.warn("IllegalArgumentException: " + illegalArgumentException);
            logger.debug(illegalArgumentException);
        }
        catch (AmazonClientException amazonClientException)
        {
            logger.warn("AmazonClientException: " + amazonClientException);
            logger.debug(amazonClientException);
        }
        catch (Throwable throwable)
        {
            logger.warn("Throwable: " + throwable);
            logger.debug(throwable);
        }
        
        return topicARN;
    }

    /* Setup (create) an AMS Glacier Vault */
    public static void setupVault(String accessKey, String secretKey, String domainName, String vaultName, String topicARN)
    {
        try
        {
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            AmazonGlacierClient amazonGlacierClient = new AmazonGlacierClient(awsCredentials);
            amazonGlacierClient.setEndpoint("https://glacier." + domainName + ".amazonaws.com/");

            CreateVaultRequest createVaultRequest = new CreateVaultRequest();
            createVaultRequest.withVaultName(vaultName);

            CreateVaultResult createVaultResult = amazonGlacierClient.createVault(createVaultRequest);
            if (createVaultResult != null)
            {
                VaultNotificationConfig vaultNotificationConfig = new VaultNotificationConfig();
                vaultNotificationConfig.withSNSTopic(topicARN);
                vaultNotificationConfig.withEvents("ArchiveRetrievalCompleted", "InventoryRetrievalCompleted");

                SetVaultNotificationsRequest setVaultNotificationsRequest = new SetVaultNotificationsRequest();
                setVaultNotificationsRequest.withVaultName(vaultName);
                setVaultNotificationsRequest.withVaultNotificationConfig(vaultNotificationConfig);

                amazonGlacierClient.setVaultNotifications(setVaultNotificationsRequest);
            }
            else
                logger.warn("Unable to create vault: \"" + vaultName + "\"");

            amazonGlacierClient.shutdown();
        }
        catch (AmazonServiceException amazonServiceException)
        {
            logger.warn("AmazonServiceException: " + amazonServiceException);
            logger.debug(amazonServiceException);
        }
        catch (IllegalArgumentException illegalArgumentException)
        {
            logger.warn("IllegalArgumentException: " + illegalArgumentException);
            logger.debug(illegalArgumentException);
        }
        catch (AmazonClientException amazonClientException)
        {
            logger.warn("AmazonClientException: " + amazonClientException);
            logger.debug(amazonClientException);
        }
        catch (Throwable throwable)
        {
            logger.warn("Throwable: " + throwable);
            logger.debug(throwable);
        }
    }
}