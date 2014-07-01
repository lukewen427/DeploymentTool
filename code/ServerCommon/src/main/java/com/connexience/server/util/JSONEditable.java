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

import org.json.JSONObject;

/**
 * Classes that implement this interface can have properties edited via a JSON
 * object. This is used to populate the editor in the website.
 * @author hugo
 */
public interface JSONEditable {
    /** Read properties from a JSON object */
    public void readJson(JSONObject json) throws Exception;
    
    /** Write properties to a JSON object */
    public JSONObject toJson() throws Exception;
}