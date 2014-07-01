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
package com.connexience.server.model.datasets;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.HibernateSessionProvider;
import com.connexience.server.ejb.SQLConnectionProvider;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.JSONContainer;
import org.hibernate.Session;

/**
 * This class can enact a query
 * @author hugo
 */
public abstract class DatasetQueryEnactor implements DatasetConstants {
    /** Session provider for hibernate queries */
    protected HibernateSessionProvider sessionProvider;
    
    /** SQL connection provider */
    protected SQLConnectionProvider connectionProvider;
    
    /** Ticket of the user executing the query */
    protected Ticket ticket;
    
    /** Query being enacted */
    protected DatasetQuery query;
    
    /** Dataset item that is being queried */
    protected DatasetItem item;
    
    /** Dataset that is being queried */
    protected Dataset dataset;

    /** Is a hibernate session required */
    protected CONNECTION_TYPE connectionType = CONNECTION_TYPE.HIBERNATE_CONNECTION;
    
    public DatasetQueryEnactor() {
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setItem(DatasetItem item) {
        this.item = item;
    }

    public DatasetItem getItem() {
        return item;
    }

    public void setQuery(DatasetQuery query) {
        this.query = query;
    }

    public DatasetQuery getQuery() {
        return query;
    }

    public CONNECTION_TYPE getConnectionType() {
        return connectionType;
    }

    public void setConnectionProvider(SQLConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public SQLConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public void setSessionProvider(HibernateSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }

    public HibernateSessionProvider getSessionProvider() {
        return sessionProvider;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }
    
    /** Perform the query */
    public abstract JSONContainer performQuery() throws ConnexienceException;
}