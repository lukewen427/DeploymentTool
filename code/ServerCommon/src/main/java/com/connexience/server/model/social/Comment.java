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

import com.connexience.server.model.ServerObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Simon
 * Date: 03-Jul-2008
 */
public class Comment extends ServerObject implements Serializable
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


    // Id of the blog post that this comment is attached to
    private String objectId;

    // The body of the comment
    private String text;

    // database id of the comment
    private String id;

    // the time that the comment was left
    private Date timestamp;

    //name of the author who left the comment.  This may be different from the username for public users
    private String authorName;

    // Whether or not the user should be notified if anyone comments the BlogPost this comment has commented on
    private boolean notification;

    public Comment()
    {
        this.timestamp = new Date();
    }

    public String getObjectType()
    {
        return "Comment";
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getPostId(){
        return objectId;
    }

    public void setPostId(String postId){
        this.objectId = postId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    public boolean isNotification()
    {
        return notification;
    }

    public void setNotification(boolean notification)
    {
        this.notification = notification;
    }
}
