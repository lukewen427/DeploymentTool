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

/**
 * This interface defines a workflow engine / service that listens to a JMS
 * client
 * @author hugo
 */
public interface WorkflowJMSListener {
    /** Is the JMS client connected */
    public boolean isJmsAttached();
    
    /** Attach the JMS Client */
    public void attachJms(String hostname, int port, String user, String password, String queueName, Integer bufferSize) throws Exception;
    
    /** Detach the JMS Client */
    public void detachJms();
    
    /** Get the JMS Attacher thread */
    public JMSAttachThread getAttacherThread();
}