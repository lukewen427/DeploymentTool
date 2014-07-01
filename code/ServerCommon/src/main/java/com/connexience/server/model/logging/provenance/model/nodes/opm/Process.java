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
package com.connexience.server.model.logging.provenance.model.nodes.opm;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Created by IntelliJ IDEA.
 * User: nsjw7
 * Date: 23/03/2011
 * Time: 08:19
 */
public class Process extends OpmObject
{
  public Process(Node undelyingNode)
  {
    super(undelyingNode);
  }

  public Relationship used(Artifact artifact, String role, String account)
  {
      Relationship rel = underlyingNode.createRelationshipTo(artifact.getUnderlyingNode(), OpmRelationshipTypes.USED);
      rel.setProperty(OpmObject.ROLE, role);
      rel.setProperty(OpmObject.ACCOUNT, account);
      return rel;
  }

  public void wasControlledBy(Agent agent, String role, String account)
  {
      Relationship rel = underlyingNode.createRelationshipTo(agent.getUnderlyingNode(), OpmRelationshipTypes.WAS_CONTROLLED_BY);
      rel.setProperty(OpmObject.ROLE, role);
      rel.setProperty(OpmObject.ACCOUNT, account);
  }

  public void wasTriggeredBy(Process process, String role, String account)
  {
      Relationship rel = underlyingNode.createRelationshipTo(process.getUnderlyingNode(), OpmRelationshipTypes.WAS_TRIGGERED_BY);
      rel.setProperty(OpmObject.ROLE, role);
      rel.setProperty(OpmObject.ACCOUNT, account);
  }
}
