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
import com.connexience.server.model.datasets.DatasetQueryEnactor;
import com.connexience.server.model.datasets.DatasetsUtils;
import com.connexience.server.model.datasets.queries.temporal.AbsoluteTimeRangeQuery;
import com.connexience.server.model.datasets.queries.temporal.LatestByAbsoluteTimeQuery;
import com.connexience.server.util.JSONContainer;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * An enactor for an absolute time range query.
 * @author hugo
 */
public class AbsoluteTimeRangeQueryEnactor extends DatasetQueryEnactor {

    public AbsoluteTimeRangeQueryEnactor() {
        connectionType = CONNECTION_TYPE.HIBERNATE_CONNECTION;
    }

    @Override
    public JSONContainer performQuery() throws ConnexienceException {
        Session session = null;
        try {
            session = sessionProvider.getSession();
            AbsoluteTimeRangeQuery q = (AbsoluteTimeRangeQuery)query;
            
            Query hqlQuery = session.createQuery("from JsonDataRow as obj where obj.itemId=:itemid and obj.collectionTime>=:starttime and obj.collectionTime<=:endtime order by obj.id asc");
            hqlQuery.setLong("itemid", item.getId());
            hqlQuery.setDate("starttime", q.getStartDate());
            hqlQuery.setDate("endtime", q.getEndDate());
            List rows = hqlQuery.list();
            
            if(q.getKeyArray()!=null && q.getKeyArray().length>0){
                return DatasetsUtils.createResultFromList(rows, q.getKeyArray());
            } else {
                return DatasetsUtils.createResultFromList(rows, null);
            }
            
        } catch(Exception e){
            throw new ConnexienceException("Error performing query: " + e.getMessage(), e);
        } finally {
            sessionProvider.closeSession(session);
        }        
    }
    
    
}
