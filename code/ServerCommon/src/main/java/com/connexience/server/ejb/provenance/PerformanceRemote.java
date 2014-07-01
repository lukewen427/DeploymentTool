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
import com.connexience.server.model.security.Ticket;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.pipeline.core.data.DataException;

import javax.ejb.Remote;
import java.util.HashMap;
import java.util.List;

/**
 * User: nsjw7
 * Date: Mar 15, 2011
 * Time: 2:18:55 PM
 * This interface represents how clients can query provenance data
 */
@Remote
public interface PerformanceRemote
{


    double buildDurationModel(Ticket ticket, String serviceId) throws ConnexienceException, DataException;

     List<String> getServiceIds(Ticket ticket) throws ConnexienceException ;

    void saveModelDataToFile(Ticket ticket, String serviceId) throws ConnexienceException;

    /**
     * Build a model of inputs + props ~ output size for a particular service.  At present returns r2 for that model
     */
    HashMap<String, Double> buildOutputSizeModel(Ticket ticket, String serviceId) throws ConnexienceException;

    double getPrediction(Ticket ticket, String serviceId, String versionNum, Array2DRowRealMatrix observations) throws Exception;
}
