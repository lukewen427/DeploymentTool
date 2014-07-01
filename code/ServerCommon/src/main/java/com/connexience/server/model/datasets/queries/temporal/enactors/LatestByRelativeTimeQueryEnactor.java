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
package com.connexience.server.model.datasets.queries.temporal.enactors;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.DatasetConstants;
import com.connexience.server.model.datasets.DatasetQueryEnactor;
import com.connexience.server.model.datasets.DatasetsUtils;
import com.connexience.server.model.datasets.queries.temporal.LatestByRelativeTimeQuery;
import com.connexience.server.util.JSONContainer;
import java.util.Calendar;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author hugo
 */
public class LatestByRelativeTimeQueryEnactor extends DatasetQueryEnactor implements DatasetConstants {

    public LatestByRelativeTimeQueryEnactor() {
        connectionType = CONNECTION_TYPE.HIBERNATE_CONNECTION;
    }

    @Override
    public JSONContainer performQuery() throws ConnexienceException {
        Session session = null;
        try {            
            Calendar c = Calendar.getInstance();
            LatestByRelativeTimeQuery q = (LatestByRelativeTimeQuery)query;
            int units = -q.getNumberOfUnits();  // negative units to subtract time
            
            if(q.getTimeUnit().equals(DAY)){
                c.add(Calendar.DAY_OF_YEAR, units);
                
            } else if(q.getTimeUnit().equals(HOUR)){
                c.add(Calendar.HOUR_OF_DAY, units);
                
            } else if(q.getTimeUnit().equals(MINUTE)){
                c.add(Calendar.MINUTE, units);
                
            } else if(q.getTimeUnit().equals(SECOND)){
                c.add(Calendar.SECOND, units);
                
            } else if(q.getTimeUnit().equals(WEEK)){
                c.add(Calendar.WEEK_OF_YEAR, units);
                
            } else if(q.getTimeUnit().equals(YEAR)){
                c.add(Calendar.YEAR, units);
                
            } else {
                c.add(Calendar.DAY_OF_YEAR, units);
            }
            
            session = sessionProvider.getSession();
            Query hqlQuery = session.createQuery("from JsonDataRow as obj where obj.itemId=:itemid and obj.collectionTime>=:collectiontime order by obj.id asc");
            hqlQuery.setLong("itemid", item.getId());
            hqlQuery.setDate("collectiontime", c.getTime());
            List rows = hqlQuery.list();
            
            if(q.getKeyArray()!=null && q.getKeyArray().length>0){
                return DatasetsUtils.createResultFromList(rows, q.getKeyArray());
            } else {
                return DatasetsUtils.createResultFromList(rows, null);
            }
            
        } catch (Exception e){
            throw new ConnexienceException("Error performing query: " + e.getMessage(), e);
        } finally {
            sessionProvider.closeSession(session);
        }
    }
    
    
}
