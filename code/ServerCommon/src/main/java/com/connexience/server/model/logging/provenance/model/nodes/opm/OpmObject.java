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
public class OpmObject
{
  public static final String NAME = "name";
  public static final String ROLE = "role";
  public static final String ACCOUNT = "account";

  protected Node underlyingNode;

  public OpmObject(Node undelyingNode)
  {
    this.underlyingNode = undelyingNode;
  }

  public Node getUnderlyingNode()
  {
    return underlyingNode;
  }

  public String getName()
  {
      if(this.underlyingNode.hasProperty(NAME))
      {
          return (String) this.underlyingNode.getProperty(NAME);
      }
      else
      {
          return null;
      }

  }

  public void setName(String name)
  {
    this.underlyingNode.setProperty(NAME, name);
  }

  public void printProperties()
  {
    for(String key : this.underlyingNode.getPropertyKeys())
    {
      System.out.println(key + " = " + this.underlyingNode.getProperty(key) + "\n");
    }
  }

  public void printRelationships()
  {
    for(Relationship rel : this.underlyingNode.getRelationships())
    {
      System.out.println("relationship: " + rel.getType());
    }
  }
}
