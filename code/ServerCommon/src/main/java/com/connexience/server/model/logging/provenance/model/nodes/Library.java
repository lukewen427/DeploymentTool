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
import com.connexience.server.model.logging.provenance.model.relationships.ProvenanceRelationshipTypes;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * Created by IntelliJ IDEA.
 * User: nsjw7
 * Date: Mar 9, 2011
 * Time: 9:41:29 AM
 */
public class Library extends Artifact
{
  public static final String ESC_ID = "escId";
  public static final String VERSION_ID = "versionId";
  public static final String VERSION_NUM = "versionNumber";

  public Library(Node underlyingNode)
  {
    super(underlyingNode);
    this.underlyingNode.setProperty("TYPE", "Library");
  }

  public void setEscId(String escId)
  {
    underlyingNode.setProperty(ESC_ID, escId);
  }

  public String getEscId()
  {
    return (String) underlyingNode.getProperty(ESC_ID);
  }

 public void setVersionId(String versionId)
  {
    underlyingNode.setProperty(VERSION_ID, versionId);
  }

  public String getVersionId()
  {
    return (String) underlyingNode.getProperty(VERSION_ID);
  }

  public void setVersionNum(int versionNum)
  {
    underlyingNode.setProperty(VERSION_NUM, versionNum);
  }

  public int getVersionNum()
  {
    return (Integer) underlyingNode.getProperty(VERSION_NUM);
  }



  public Relationship versionOf(Library previousVersion)
  {
    return underlyingNode.createRelationshipTo(previousVersion.getUnderlyingNode(), ProvenanceRelationshipTypes.VERSION_OF);
  }

}