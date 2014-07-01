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
package com.connexience.server.model.datasets.queries.index.enactors;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.datasets.DatasetItem;
import com.connexience.server.model.datasets.DatasetQuery;
import com.connexience.server.model.datasets.DatasetQueryEnactor;
import com.connexience.server.model.datasets.DatasetsUtils;
import com.connexience.server.model.datasets.items.multiple.JsonMultipleValueItem;
import com.connexience.server.model.datasets.queries.index.IndexRangeQuery;
import com.connexience.server.model.datasets.queries.index.LatestByIndexQuery;
import com.connexience.server.util.JSONContainer;
import org.hibernate.Session;
import org.json.JSONObject;

/**
 * This class performs a range query
 * @author hugo
 */
public class IndexRangeQueryEnactor extends DatasetQueryEnactor {

    public IndexRangeQueryEnactor() {
        connectionType = CONNECTION_TYPE.HIBERNATE_CONNECTION;
    }
    
    @Override
    public JSONContainer performQuery() throws ConnexienceException {
        Session session = null;
        try {
            session = sessionProvider.getSession();
            int size = DatasetsUtils.getJsonMultipleValueDataSize(session, (JsonMultipleValueItem)item);
            IndexRangeQuery q = (IndexRangeQuery)query;
            if(size>0){
                int startPos = q.getStartRow();
                if(startPos<0){
                    startPos = 0;
                }
                return DatasetsUtils.getJsonMultipleValueData(session, (JsonMultipleValueItem)item, startPos , q.getNumberOfRows(), q.getKeyArray());

            } else {
                return new JSONContainer(new JSONObject());
            }
        } catch (Exception e){
            throw new ConnexienceException("Error perfoming query: " + e.getMessage(), e);
        } finally {
            sessionProvider.closeSession(session);
        }
    } 
}
