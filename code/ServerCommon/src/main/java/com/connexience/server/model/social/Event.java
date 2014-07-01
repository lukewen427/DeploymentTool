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
package com.connexience.server.model.social;

import com.connexience.server.model.folder.LinksFolder;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Simon
 * Date: 15-Jul-2008
 * <p/>
 * Class to represent a generic event such as meeting, conference etc.
 * May be subclassed in the future to provide specific events
 */
public class Event extends LinksFolder implements Serializable
{
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


    private Date startDate = new Date();
    private Date endDate = new Date();


    public Event()
    {
        super();
    }

    public long getStartDateTimestamp(){
        return startDate.getTime();
    }

    public void setStartDateTimestamp(long startDateTimestamp){
        startDate = new Date(startDateTimestamp);
    }

    public long getEndDateTimestamp(){
        return endDate.getTime();
    }

    public void setEndDateTimestamp(long endDateTimestamp){
        endDate = new Date(endDateTimestamp);
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }  
}
