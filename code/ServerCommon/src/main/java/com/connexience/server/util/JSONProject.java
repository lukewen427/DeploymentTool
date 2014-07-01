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
 * This class provides a wrapper for a ServerObject that can be put into a
 * JSONObject container.
 * @author hugo
 */
public class JSONProject extends JSONObject {

    public JSONProject() {
        try {put("_type", "Project");}catch(Exception e){}
        try {put("name", "");}catch(Exception e){}
        try {put("id", "");}catch(Exception e){}
        try {put("value", "");}catch(Exception e){}
    }

    public JSONProject(JSONTokener x) throws JSONException {
        super(x);
    }

    public JSONProject(Map map) {
        super(map);
    }

    public JSONProject(Object bean) {
        super(bean);
    }

    public JSONProject(String source) throws JSONException {
        super(source);
    }

    public JSONProject(JSONObject jo, String[] names) throws JSONException {
        super(jo, names);
    }

    public JSONProject(Object object, String[] names) {
        super(object, names);
    }
    
    public JSONProject(Project obj) throws JSONException {
        put("_type", "Project");
        put("className", obj.getClass().getName());
        put("id", obj.getId());
        put("name", obj.getName());
        put("value", obj.getName());
    }
    
    public int getProjectId() throws JSONException {
        return getInt("id");
    }
    
    public String getProjectName() throws JSONException {
        return getString("name");
    }
    
    public JSONProject(String className, int id, String name) throws JSONException {
        put("_type", "Project");
        put("className", className);
        put("id", id);
        put("name", name);
        put("value", name);        
    }
    
    public JSONProject setServerObjectClassName(String className) throws JSONException {
        put("className", className);
        return this;
    }

    public boolean isProject(JSONObject obj){
        try {
            if(obj.has("_type") && obj.getString("_type").equals("Project")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}