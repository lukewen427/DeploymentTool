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

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Simon
 * Date: Jun 15, 2009
 * <p/>
 * This class represents a mapping from a server object to a tag.
 */
public class TagToObject implements Serializable
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


    public static final int MACHINE_GENERATED = 1;
    public static final int USER_GENERATED = 2;

    /*
     * The id of this object - required as Hibernate doesn't like having composite primary keys
     * */
    private String id;

    /*
     * id of the object
     * */
    private String serverObjectId;

    /*
     * id of the com.connexience.server.social.tag
     * */
    private String tagId;

    /**
     * The weight of the tag so that we can have different weights for user and machine generated tags
     * */
    private int weight;

    /**
     * User id of the person who created this tag
     * */
    private String creatorId;

    /**
     * The date and time that this tag was created*/
    private Date createDate;


    public TagToObject()
    {
    }

    public TagToObject(String serverObjectId, String tagId, String creatorId, int weight)
    {
        this.serverObjectId = serverObjectId;
        this.tagId = tagId;
        this.creatorId = creatorId;
        this.weight = weight;
        this.createDate = new Date();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getServerObjectId()
    {
        return serverObjectId;
    }

    public void setServerObjectId(String serverObjectId)
    {
        this.serverObjectId = serverObjectId;
    }

    public String getTagId()
    {
        return tagId;
    }

    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public String getCreatorId()
    {
        return creatorId;
    }

    public void setCreatorId(String creatorId)
    {
        this.creatorId = creatorId;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }
}

