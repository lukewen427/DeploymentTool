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

import com.connexience.server.model.logging.provenance.model.nodes.opm.Artifact;
import org.neo4j.graphdb.Node;

/**
 * Created by IntelliJ IDEA.
 * User: nsjw7
 * Date: 07/04/2011
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class TransientData extends Artifact
{
  public static final String ID = "id";

  public static final String DATA_SIZE = "Data Size";
  public static final String DATA_TYPE = "Data Type";
  public static final String HASH_VALUE = "Hash Value";

  public TransientData(Node undelyingNode)
  {
    super(undelyingNode);
    underlyingNode.setProperty("TYPE", "Transient Data");

  }

   public void setId(String id)
  {
    underlyingNode.setProperty(ID, id);
  }

  public String getId()
  {
    return (String) underlyingNode.getProperty(ID);
  }
  public void setDataSize(long size)
 {
   underlyingNode.setProperty(DATA_SIZE, size);
 }

 public long getDataSize()
 {
   return (Long) underlyingNode.getProperty(DATA_SIZE);
 }

   public void setDataType(String dataType)
  {
    underlyingNode.setProperty(DATA_TYPE, dataType);
  }

  public String getDataType()
  {
    return (String) underlyingNode.getProperty(DATA_TYPE);
  }

  public void setHashValue(String hashValue)
  {
    underlyingNode.setProperty(HASH_VALUE, hashValue);
  }

  public String getHashValue()
  {
    return (String) underlyingNode.getProperty(HASH_VALUE);
  }

  @Override
  public boolean equals(Object o)
  {
    if(!(o instanceof TransientData))
    {
      return false;
    }
    else
    {
      TransientData that = (TransientData) o;
      return this.getHashValue().equals(that.getHashValue());
    }
  }
}
