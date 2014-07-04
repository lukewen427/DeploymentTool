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
package com.connexience.server.workflow.engine.cloud;

import com.connexience.server.workflow.service.*;

/**
 * This interface defines a class that can accept data processor messages for
 * an autodeploying cloud service
 * @author nhgh
 */
public interface CloudDataProcessorMessageDestination {
    /** Post a data processor message. This method returns true if the message was accepted,
     * and false if it was rejected */
    public boolean postCallMessage(DataProcessorCallMessage message) throws DataProcessorException;


    /** Post a data processor response message. This method returns true if the message was
     * accepted and false if it was rejected */
    public boolean postResponseMessage(DataProcessorResponseMessage message) throws DataProcessorException;

    /** Terminate a service running for a call message */
    public void terminate(DataProcessorCallMessage message) throws DataProcessorException;
}