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
package com.connexience.server.ejb.notifications;

import com.connexience.server.ConnexienceException;
import com.connexience.server.model.notifcations.Notification;

import javax.ejb.Remote;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: martyn
 * Date: 16-Nov-2009
 * Time: 15:48:12
 * To change this template use File | Settings | File Templates.
 */

@Remote
public interface NotificationsRemote
{
    //Notification Keys
    public static final String EMAIL_ON_MESSAGE_RECIEVE = "sendEmailOnMessageRecieve";

    public static final String EMAIL_ON_WORKFLOW_COMPLETION = "sendEmailOnMessageRecieve";

    public static final String MESSAGE_ON_WORKFLOW_COMPLETION = "sendEmailOnMessageRecieve";

    public static final String EMAIL_ON_BLOG_POST_COMMENT = "sendEmailOnBlogPostComment";

    public static final String MESSAGE_ON_BLOG_POST_COMMENT = "sendMessageOnBlogPostComment";

    public static final String EMAIL_ON_BLOG_POST_COMMENT_COMMENT = "sendEmailOnBlogPostCommentComment";

    public static final String MESSAGE_ON_BLOG_POST_COMMENT_COMMENT = "sendMessageOnBlogPostCommentComment";
    
    void sendNotification(Notification notification) throws ConnexienceException;

    boolean getNotificationValue(String notificationKey, String userId) throws ConnexienceException;

    ArrayList<String> getNotificationKeys();
}
