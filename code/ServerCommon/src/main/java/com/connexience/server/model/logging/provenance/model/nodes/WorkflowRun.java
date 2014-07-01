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
package com.connexience.server.model.logging.provenance.model.nodes;

import com.connexience.server.model.logging.provenance.model.nodes.opm.Process;
import com.connexience.server.model.logging.provenance.model.relationships.ProvenanceRelationshipTypes;
import org.neo4j.graphdb.Node;

/**
 * Created by IntelliJ IDEA.
 * User: nsjw7
 * Date: 07/04/2011
 * Time: 10:48
 */
public class WorkflowRun extends Process {
    public static final String ESC_ID = "escId";
    public static final String INVOCATION_ID = "invocationId";

    public WorkflowRun(Node undelyingNode) {
        super(undelyingNode);
        underlyingNode.setProperty("TYPE", "Workflow Run");
    }

    public void setEscId(String escId) {
        underlyingNode.setProperty(ESC_ID, escId);
    }

    public String getEscId() {
        return (String) underlyingNode.getProperty(ESC_ID);
    }

    public void setInvocationId(String invocationId) {
        underlyingNode.setProperty(INVOCATION_ID, invocationId);
    }

    public String getInvocationId() {
        return (String) underlyingNode.getProperty(INVOCATION_ID);
    }

    public void contained(ServiceRun serviceRun) {
        underlyingNode.createRelationshipTo(serviceRun.getUnderlyingNode(), ProvenanceRelationshipTypes.CONTAINED);
    }

    public void runOf(WorkflowVersion workflowVersion) {
        underlyingNode.createRelationshipTo(workflowVersion.getUnderlyingNode(), ProvenanceRelationshipTypes.RUN_OF);
    }

    public void invokedBy(WorkflowRun workflowRun)
    {
        underlyingNode.createRelationshipTo(workflowRun.getUnderlyingNode(), ProvenanceRelationshipTypes.INVOKED_BY);
    }
}
