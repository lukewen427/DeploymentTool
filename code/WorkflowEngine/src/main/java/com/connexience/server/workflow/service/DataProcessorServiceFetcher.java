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
package com.connexience.server.workflow.service;

/**
 * This interface defines an object that can supply DataProcessorServiceDefinition
 * objects from a remote machine. In the editor, this is the DragAndDrop handler,
 * in the runtime it is the WorkflowInvocation which is responsible for being
 * able to supply service definition objects when the block executes.
 * @author nhgh
 */
public interface DataProcessorServiceFetcher {
    /** Get the latest version of a service definition by ID */
    public DataProcessorServiceDefinition getServiceDefinition(String serviceId) throws DataProcessorException;

    /** Get a specific version of a service definition */
    public DataProcessorServiceDefinition getServiceDefinition(String serviceId, String versionId) throws DataProcessorException;
}
