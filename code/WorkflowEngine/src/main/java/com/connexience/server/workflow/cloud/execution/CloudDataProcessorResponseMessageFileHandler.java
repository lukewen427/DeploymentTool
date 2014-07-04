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
package com.connexience.server.workflow.cloud.execution;

import com.connexience.server.workflow.service.*;
import com.connexience.server.workflow.util.XmlSerializationUtils;
import java.io.*;
import org.apache.log4j.*;
/**
 * This class provides a standard response message handler that saves the response
 * message to a file in the invocation directory so that the service invocation
 * can send it back to the workflow engine.
 * @author nhgh
 */
public class CloudDataProcessorResponseMessageFileHandler implements DataProcessorResponseMessageHandler {
    static Logger logger = Logger.getLogger(CloudDataProcessorResponseMessageFileHandler.class);
    /** Invocation directory */
    private File invocationDir;

    public CloudDataProcessorResponseMessageFileHandler(File invocationDir) {
        this.invocationDir = invocationDir;
    }

    /** Save the message to file */
    public void sendResponseMessage(DataProcessorResponseMessage message) throws DataProcessorException {
        try {
            File messageFile = new File(invocationDir, message.getContextId() + "-response.msg");
            XmlSerializationUtils.xmlDataStoreSerialize(messageFile, message);
        } catch (Exception e){
            logger.error("Error saving response message to invocation folder: InvocationID=" + message.getInvocationId(), e);
            throw new DataProcessorException("Error saving response message to invocation folder: " + e.getMessage());
        }
    }
}