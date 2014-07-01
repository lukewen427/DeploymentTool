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
package com.connexience.server.model.image;

import java.io.Serializable;
import java.util.Arrays;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * This class is a holder for image blobs that can be associated with a ServerObject
 * Date: Jul 31, 2009
 * Time: 1:05:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "workflowservicelogs")
public class ImageData implements Serializable
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


    public static final String LARGE_PROFILE = "large profile";
    public static final String SMALL_PROFILE = "small profile";
    public static final String WORKFLOW_PREVIEW = "workflow preview";
    public static final String WORKFLOW_BLOCK_ICON = "blockicon";


    /**
     * Id of the image
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    /**
     * The Id of the server object that this image is associated with
     * */
    @Column(name = "serverobjectid")
    private String serverObjectId;

    /**
     * The image data
     * */
    @Lob
    private byte[] data;

    /**
     * The type of the image - large or small profile picture etc.
     * */
    @Basic
    private String type;

    public ImageData()
    {
    }

    public ImageData getCopy(){
        ImageData copy = new ImageData();
        copy.setData(Arrays.copyOf(data, data.length));
        copy.setServerObjectId(serverObjectId);
        copy.setType(type);
        return copy;
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

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
