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

import com.connexience.server.model.ServerObject;
import com.connexience.server.model.project.Project;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This object provides a wrapper for a ServerObject that can be put into 
 * a JSONObject.
 * @author hugo
 */
public class JSONServerObject extends JSONObject {

    public JSONServerObject(Class c) {
        try {put("_type", "ServerObject");}catch(Exception e){}
        try {put("className", c.getName());}catch(Exception e){}
        try {put("name", "");}catch(Exception e){}
        try {put("id", "");}catch(Exception e){}
        try {put("value", "");}catch(Exception e){}
    }

    public JSONServerObject(JSONTokener x) throws JSONException {
        super(x);
    }

    public JSONServerObject(Map map) {
        super(map);
    }

    public JSONServerObject(Object bean) {
        super(bean);
    }

    public JSONServerObject(String source) throws JSONException {
        super(source);
    }

    public JSONServerObject(JSONObject jo, String[] names) throws JSONException {
        super(jo, names);
    }

    public JSONServerObject(Object object, String[] names) {
        super(object, names);
    }
    
    public JSONServerObject(ServerObject obj) throws JSONException {
        put("_type", "ServerObject");
        put("className", obj.getClass().getName());
        put("id", obj.getId());
        put("name", obj.getName());
        put("value", obj.getName());
    }
}