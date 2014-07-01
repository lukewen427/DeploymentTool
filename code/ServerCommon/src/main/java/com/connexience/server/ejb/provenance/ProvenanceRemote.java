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
package com.connexience.server.ejb.provenance;


import com.connexience.server.ConnexienceException;
import com.connexience.server.model.logging.WorkflowReport;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: nsjw7
 * Date: Mar 15, 2011
 * Time: 2:18:55 PM
 * This interface represents how clients can query provenance data
 */
@Remote
public interface ProvenanceRemote
{

  /**
   * Get a JSON version of a workflow which represnets the provenance of an objcet
   *
   * @param versionId The eSC versionId of the object, note not the documentId
   * @param positions positions on the canvas where the blocks should be placed.  The key should be blockUUID-invocationId
   * @return JSON String of the workflow
   */
  public String createVirtualWorkflow(String versionId, HashMap<String, HashMap<String, Integer>> positions) throws ConnexienceException;

  /**
   * Get the provenance (history) or effects of an object
   *
   * @param versionId DocumentVersionId of the object
   * @param direction forwards (effects) or backwards (provenance)
   * @return JSON String that is formatted for the JIT force directed layout
   */
  public String getProvenance(String versionId, String direction) throws ConnexienceException;
  
  /**
   * Get the provenance (history) or effects of an object in 'dot' format
   *
   * @param versionId DocumentVersionId of the object
   * @param direction forwards (effects) or backwards (provenance)
   * @return String that is formatted in 'dot'
   */
  public String getProvenanceAsDot(String versionId, String direction) throws RemoteException, ConnexienceException;

  /**
   * Get the provenance (history) or effects of an object in 'groff' format (ms macros)
   *
   * @param versionId DocumentVersionId of the object
   * @param direction forwards (effects) or backwards (provenance)
   * @return String that is formatted in 'groff' fragment  (ms macros)
   */
  public String getProvenanceAsRoff(String versionId, String direction) throws RemoteException, ConnexienceException;

  Map<String, WorkflowReport> getWorkflowStats(Ticket adminTicket, String projectId) throws ConnexienceException;
}
