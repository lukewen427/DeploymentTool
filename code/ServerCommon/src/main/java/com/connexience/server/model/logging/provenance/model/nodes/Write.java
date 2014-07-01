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
package com.connexience.server.model.logging.provenance.model.nodes;

import com.connexience.server.model.logging.provenance.model.nodes.opm.Process;
import org.neo4j.graphdb.Node;

/**
 * Created by IntelliJ IDEA.
 * User: nsjw7
 * Date: Mar 9, 2011
 * Time: 9:52:29 AM
 */
public class Write extends Process {


    public static final String TIME = "time";

    public Write(Node underlyingNode) {
        super(underlyingNode);
        this.underlyingNode.setProperty("TYPE", "Write");
    }

    public void setTime(String time)
     {
       underlyingNode.setProperty(TIME, time);
     }

     public String getTime()
     {
       return (String) underlyingNode.getProperty(TIME);
     }

}
