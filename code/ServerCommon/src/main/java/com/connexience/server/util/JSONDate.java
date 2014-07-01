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
package com.connexience.server.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * This class provides a JSON container for a Java date object
 * @author hugo
 */
public class JSONDate extends JSONObject {
    static DateFormat format = DateFormat.getDateTimeInstance();

    public JSONDate(JSONTokener x) throws JSONException {
        super(x);
    }

    public JSONDate(Map<?, ?> map) {
        super(map);
    }

    public JSONDate(Object bean) {
        super(bean);
    }

    public JSONDate(String source) throws JSONException {
        super(source);
    }

    public JSONDate(JSONObject jo, String[] names) throws JSONException {
        super(jo, names);
    }

    public JSONDate(Object object, String[] names) {
        super(object, names);
    }

    public JSONDate(Date dateValue) throws JSONException {
        put("_milliseconds", dateValue.getTime());
        put("value", format.format(dateValue));
        put("_type", "java.util.Date");
    }

    public JSONDate() {
    }

    public Date getDateValue() throws JSONException {
        long time = getLong("_milliseconds");
        return new Date(time);
    }

    public void setDateValue(Date dateValue) throws JSONException {
        put("_milliseconds", dateValue.getTime());
        put("value", format.format(dateValue));   
        put("_type", "java.util.Date");
    }   
    
    /** Does a JSON object represent a JSON date object */
    public static boolean isJSONDate(JSONObject object){
        if(object.has("_milliseconds")){
            return true;
        } else {
            return false;
        }
    }
    
    /** Create a Date object from a JSONDate object */
    public static Date createDate(JSONObject json) throws JSONException {
        return new Date(json.getLong("_milliseconds"));
    }
}