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
package com.connexience.server.ejb.dashboard;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.dashboard.Dashboard;
import com.connexience.server.model.security.Ticket;

import javax.ejb.Remote;
import java.util.List;

/**
 * This interface defines the behaviour of the dashboard management bean
 * @author hugo
 */
@Remote
public interface DashboardRemote {
    /** List the dashboards the user owns */
    public List listUserDashboards(Ticket ticket) throws ConnexienceException;
    
    /** List all of the dashboards the user can see */
    public List listSharedDashboards(Ticket ticket) throws ConnexienceException;
    
    /** List all of the dashboards the user owns for a dataset */
    public List listDashboardsForDataset(Ticket ticket, String datasetId) throws ConnexienceException;
    
    /** Save a dashboard */
    public Dashboard saveDashboard(Ticket ticket, Dashboard dashboard) throws ConnexienceException;
    
    /** Get a dashboard by ID */
    public Dashboard getDashboard(Ticket ticket, String dashboardId) throws ConnexienceException;
    
    /** Delete a dashboard */
    public void removeDashboard(Ticket ticket, String dashboardId) throws ConnexienceException;
}