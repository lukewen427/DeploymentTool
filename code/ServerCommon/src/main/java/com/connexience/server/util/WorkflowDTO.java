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

import java.io.Serializable;

/**
 * Author: Simon
 * Date: Feb 9, 2010
 */
public class WorkflowDTO implements Serializable
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


    private String id;

    private String name;

    private Long numInvoations;

    private Long numSuccess;

    private Long numFailures;

    public WorkflowDTO()
    {
    }

    public WorkflowDTO(String id, String name, Long numInvoations, Long numSuccess, Long numFailures)
    {
        this.id = id;
        this.name = name;
        this.numInvoations = numInvoations;
        this.numSuccess = numSuccess;
        this.numFailures = numFailures;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Long getNumInvoations()
    {
        return numInvoations;
    }

    public void setNumInvoations(Long numInvoations)
    {
        this.numInvoations = numInvoations;
    }

    public Long getNumSuccess()
    {
        return numSuccess;
    }

    public void setNumSuccess(Long numSuccess)
    {
        this.numSuccess = numSuccess;
    }

    public Long getNumFailures()
    {
        return numFailures;
    }

    public void setNumFailures(Long numFailures)
    {
        this.numFailures = numFailures;
    }
}
