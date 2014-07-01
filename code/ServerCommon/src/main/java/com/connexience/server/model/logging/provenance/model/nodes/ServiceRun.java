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
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Created by IntelliJ IDEA.
 * User: nsjw7
 * Date: Mar 9, 2011
 * Time: 9:52:29 AM
 */
public class ServiceRun extends Process
{
  public static final String ESC_ID = "escId";
  public static final String INVOCATION_ID = "invocationId";
  public static final String BLOCK_UUID = "blockUUID";
  public static final String START_TIME = "startTime";
  public static final String END_TIME = "endTime";
  public static final String PROPERTIES_DATA = "propertiesData";
  public static final String IDEMPOTENT = "idempotent";
  public static final String DETERMINISTIC = "deterministic";

  public ServiceRun(Node underlyingNode)
  {
    super(underlyingNode);
    this.underlyingNode.setProperty("TYPE", "Service Run");

  }

  public void setEscId(String escId)
  {
    underlyingNode.setProperty(ESC_ID, escId);
  }

  public String getEscId()
  {
    return (String) underlyingNode.getProperty(ESC_ID);
  }

  public void setInvocationId(String invocationId)
  {
    underlyingNode.setProperty(INVOCATION_ID, invocationId);
  }

  public String getInvocationId()
  {
    return (String) underlyingNode.getProperty(INVOCATION_ID);
  }

  public void setBlockUuid(String blockUUID)
  {
    underlyingNode.setProperty(BLOCK_UUID, blockUUID);
  }

  public String getBlockUuid()
  {
    return (String) underlyingNode.getProperty(BLOCK_UUID);
  }


  public void setStartTime(String startTime)
  {
    underlyingNode.setProperty(START_TIME, startTime);
  }

  public String getStartTime()
  {
    return (String) underlyingNode.getProperty(START_TIME);
  }

  public void setPropertiesData(byte[] props)
  {
    underlyingNode.setProperty(PROPERTIES_DATA, props);
  }

  public String getPropertiesData()
  {
    return (String) underlyingNode.getProperty(PROPERTIES_DATA);
  }

  public void setEndTime(String endTime)
  {
    underlyingNode.setProperty(END_TIME, endTime);
  }

  public String getEndTime()
  {
    return (String) underlyingNode.getProperty(END_TIME);
  }

  public void setIdempotent(Boolean idempotent)
  {
    underlyingNode.setProperty(IDEMPOTENT, idempotent);
  }

  public Boolean isIdempotent()
  {
    return (Boolean) underlyingNode.getProperty(IDEMPOTENT);
  }

  public void setDeterministic(Boolean deterministic)
  {
    underlyingNode.setProperty(DETERMINISTIC, deterministic);
  }

  public Boolean isDeterministic()
  {
    return (Boolean) underlyingNode.getProperty(DETERMINISTIC);
  }


  //
  //
  //  public void writeData(Data data, DataVersion version, String timestamp)
  //  {
  //    Transaction tx = underlyingNode.getGraphDatabase().beginTx();
  //    try
  //    {
  //      Relationship rel = underlyingNode.createRelationshipTo(version.getUnderlyingNode(), ProvenanceRelationshipTypes.WROTE);
  //      rel.setProperty("timestamp", timestamp);
  //
  //      //add a relationship to say that this version is a version_of the data
  //      version.getUnderlyingNode().createRelationshipTo(data.getUnderlyingNode(), ProvenanceRelationshipTypes.VERSION_OF);
  //      tx.success();
  //    }
  //    finally
  //    {
  //      tx.finish();
  //    }
  //  }
  //
  //  public void dataTransferTo(ServiceRun targetService, String sourcePortName, String targetPortName, String timestamp)
  //  {
  //    Transaction tx = underlyingNode.getGraphDatabase().beginTx();
  //    try
  //    {
  //      Relationship rel = underlyingNode.createRelationshipTo(targetService.getUnderlyingNode(), ProvenanceRelationshipTypes.DATA_TRANSFER);
  //      rel.setProperty("sourcePortName", sourcePortName);
  //      rel.setProperty("targetPortName", targetPortName);
  //      rel.setProperty("timestamp", timestamp);
  //      tx.success();
  //    }
  //    finally
  //    {
  //      tx.finish();
  //    }
  //  }
  //
  public void requiresLibrary(Library library)
  {
    underlyingNode.createRelationshipTo(library.getUnderlyingNode(), ProvenanceRelationshipTypes.REQUIRED);
  }

  public void instanceOf(ServiceVersion serviceVersion)
  {
    underlyingNode.createRelationshipTo(serviceVersion.getUnderlyingNode(), ProvenanceRelationshipTypes.INSTANCE_OF);
  }


  @Override
  public boolean equals(Object o)
  {
    if (!(o instanceof ServiceRun))
    {
      return false;
    }
    else
    {
      ServiceRun that = (ServiceRun) o;
      Relationship rel1 = this.getUnderlyingNode().getSingleRelationship(ProvenanceRelationshipTypes.INSTANCE_OF, Direction.OUTGOING);
      if (rel1 != null)
      {
        ServiceVersion sv1 = new ServiceVersion(rel1.getEndNode());

        Relationship rel2 = that.getUnderlyingNode().getSingleRelationship(ProvenanceRelationshipTypes.INSTANCE_OF, Direction.OUTGOING);
        if (rel2 != null)
        {
          ServiceVersion sv2 = new ServiceVersion(rel2.getEndNode());
          return sv1.equals(sv2);
        }
        else
        {
          return false;
        }
      }
      else
      {
        return false;
      }
    }
  }
}
