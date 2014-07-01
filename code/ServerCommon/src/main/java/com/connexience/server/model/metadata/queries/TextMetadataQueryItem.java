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
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONObject;

/**
 * This query item performs a text metadata search
 * @author hugo
 */
public class TextMetadataQueryItem extends MetadataQueryItem{
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


    /** Search text */
    private String searchText = "";
    
    /** Case insensitive search */
    private boolean caseSensitive = false;

    public TextMetadataQueryItem() {
        setLabel("Text Value");
    }
    
    @Override
    public String createQuery() {
        if(caseSensitive){
            return " objectid in (select objectid from metadata where (" + createQueryRoot() + " and textvalue like :" + createNamedParameterTag() + ") ";
        } else {
            return " objectid in (select objectid from metadata where (" + createQueryRoot() + " and lower(textvalue) like :" + createNamedParameterTag() + ") ";
        }
    }

    @Override
    @JsonIgnore
    public Object[][] getParameters() {
        Object[][] params = new Object[1][2];
        params[0][0] = createNamedParameterTag();
        if(caseSensitive){
            params[0][1] = "%" + searchText + "%";
        } else {
            params[0][1] = "%" + searchText.toLowerCase() + "%";
        }
        return params;
    }

    

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    @Override
    public JSONObject toJson() throws Exception {
        JSONObject json = super.toJson();
        json.put("CaseSensitive", caseSensitive);
        json.put("SearchText", searchText);
        json.put("_className", this.getClass().getName());
        return json;
    }

    @Override
    public void readJson(JSONObject json) throws Exception {
        super.readJson(json);
        caseSensitive = json.getBoolean("CaseSensitive");
        searchText = json.getString("SearchText");
    }    
    
}