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
package com.connexience.server.model.metadata.queries;

import com.connexience.server.model.metadata.MetadataQueryItem;
import com.connexience.server.util.JSONDate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

import java.util.Date;
/**
 * This query searches for metadata date that lie with a range
 * @author hugo
 */
public class DateRangeMetadataQueryItem extends MetadataQueryItem{
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


    /** Lower bound of the date range */
    private Date lowerBound = new Date();
    
    /** Upper bound of the date range */
    private Date upperBound = new Date();

    public DateRangeMetadataQueryItem() {
        setLabel("Date Range");
    }

    @Override
    public String createQuery() {
        return " objectid in (select objectid from metadata where (" + createQueryRoot() + " and datevalue>=:" + createNamedParameterTag() + " and datevalue<=:" + createNamedParameterTag() +") ";
    }

    @Override
    @JsonIgnore
    public Object[][] getParameters() {
        Object[][] params = new Object[2][2];
        params[0][0] = createNamedParameterTag();
        params[0][1] = lowerBound;
        params[1][0] = createNamedParameterTag();
        params[1][1] = upperBound;
        return params;
    }

    public void setLowerBound(Date lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Date getLowerBound() {
        return lowerBound;
    }

    public void setUpperBound(Date upperBound) {
        this.upperBound = upperBound;
    }

    public Date getUpperBound() {
        return upperBound;
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("StartDate", new JSONDate(lowerBound));
        json.put("EndDate", new JSONDate(upperBound));
        json.put("_className", this.getClass().getName());
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        
        if(json.has("StartDate") && JSONDate.isJSONDate(json.getJSONObject("StartDate"))){
            lowerBound = JSONDate.createDate(json.getJSONObject("StartDate"));
        }
        
        if(json.has("EndDate") && JSONDate.isJSONDate(json.getJSONObject("EndDate"))){
            upperBound = JSONDate.createDate(json.getJSONObject("EndDate"));
        }
    }    
}