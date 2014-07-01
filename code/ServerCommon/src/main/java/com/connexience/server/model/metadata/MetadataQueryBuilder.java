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
package com.connexience.server.model.metadata;

import com.connexience.server.model.ServerObject;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.Iterator;
/**
 * This class builds metadata queries based on a collection of individual
 * metadata query items that together produce an HQL query 
 * @author hugo
 */
public class MetadataQueryBuilder implements Serializable {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    /** Metadata query containing the search terms */
    private MetadataQuery metadataQuery;
    
    public MetadataQueryBuilder() {
    }
    
    public MetadataQueryBuilder(MetadataQuery metadataQuery){
        this.metadataQuery = metadataQuery;
    }
    
    /** Build the query */
    public String buildMetaDataSearchQuery(){
        // Add the subqueries
        StringBuilder qb = new StringBuilder("select objectid from metadata where (");
        
        int count = 0;
        Iterator<MetadataQueryItem> items = metadataQuery.items();
        MetadataQueryItem i;
        int positionCounter = 0;
        
        while(items.hasNext()){
            i = items.next();
            i.setPositionCounter(positionCounter);
            qb.append(i.createQuery());
            positionCounter = i.getPositionCounter();
            if(count<metadataQuery.getSize() - 1){
                qb.append(" and ");
            }
            count++;
        }
        
        // Add the correct number of brackets
        for(int j=0;j<count;j++){
            qb.append(")");
        }
        qb.append(")");
        return qb.toString();
    }
    
    /** Create the Hibernate Query */
    public SQLQuery createSQLQuery(Session session) throws Exception {
        // Metadata query
        String queryText = buildMetaDataSearchQuery();
        
        // Full query
        String fullQuery = "select * from objectsflat where id in (" + queryText + ")";
        SQLQuery q = session.createSQLQuery(fullQuery);
        
        Iterator<MetadataQueryItem> items = metadataQuery.items();
        MetadataQueryItem i;
        
        // Set category if needed 
        Object[][] params;
        int positionCounter = 0;
        while(items.hasNext()){
            i = items.next();
            i.setPositionCounter(positionCounter);
            params = i.getParameters();
            for(int j=0;j<params.length;j++){
                q.setParameter((String)params[j][0], params[j][1]);
            }
            positionCounter = i.getPositionCounter();
        }
        q.addEntity(ServerObject.class);
        return q;
    }
    
    /** Create a count query */
    public SQLQuery createSQLCountQuery(Session session) throws Exception {
        // Metadata query
        String queryText = buildMetaDataSearchQuery();
        
        // Full query
        String fullQuery = "select count(id) from objectsflat where id in (" + queryText + ")";
        SQLQuery q = session.createSQLQuery(fullQuery);
        
        Iterator<MetadataQueryItem> items = metadataQuery.items();
        MetadataQueryItem i;
        
        // Set category if needed 
        Object[][] params;
        int positionCounter = 0;
        while(items.hasNext()){
            i = items.next();
            i.setPositionCounter(positionCounter);
            params = i.getParameters();
            for(int j=0;j<params.length;j++){
                q.setParameter((String)params[j][0], params[j][1]);
            }
            positionCounter = i.getPositionCounter();
        }
        return q;        
    }
}
