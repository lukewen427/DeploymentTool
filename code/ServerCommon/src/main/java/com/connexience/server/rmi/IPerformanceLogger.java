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
package com.connexience.server.rmi;


import com.connexience.server.model.logging.performance.Execution;

/**
 * User: nsjw7
 * Date: 21/06/2011
 * Time: 16:02
 * This interface represents how clients can log provenance data
 */
public interface IPerformanceLogger
{
    /**
     * Log a graph opertaion.  It will be logged in either the SQLDB and/or the Neo4J graph database
     * @param operation operation to be logged
     */
    public void log(Execution operation);
}
