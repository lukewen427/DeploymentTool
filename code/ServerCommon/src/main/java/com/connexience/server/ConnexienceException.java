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
package com.connexience.server;

import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;

/**
 * This is the base exception class for all ejbs
 *
 * @author hugo
 */
public class ConnexienceException extends java.lang.Exception
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


    /**
     * Unsupported operation message
     */
    public static final String UNSUPPORTED_OPERATION_MESSAGE = "Unsupported operation";

    /**
     * Access denied message
     */
    public static final String ACCESS_DENIED_MESSAGE = "Access denied";

    /**
     * No user logged in message
     */
    public static final String NO_USER_LOGGED_IN_MESSAGE = "No user logged in";

    /**
     * No folder has been specified
     */
    public static final String NO_FOLDER_SPECIFIED_MESSAGE = "No folder has been specified";

    /**
     * No such object message
     */
    public static final String OBJECT_NOT_FOUND_MESSAGE = "Could not locate specified object";

    /**
     * Incorrect organisation message
     */
    public static final String INCORRECT_ORGANIATION_MESSAGE = "Incorrect organisation";

    /**
     * No organisation has been specified
     */
    public static final String NO_ORGANISATION_SPECIFIED_MESSAGE = "No organisation has been specified";

    /**
     * Invalid signature message
     */
    public static final String INVALID_CERTIFICATE_MESSAGE = "Incorrect certificate";

    /**
     * The type of the server object is not valid
     */
    public static final String INVALID_SERVER_OBJECT_TYPE = "Invalid type of the server object";

    /**
     * No data store specified message
     */
    public static final String NO_DATA_STORE_MESSGAGE = "No data store available";

    /**
     * Cannot find partnership message
     */
    public static final String CANNOT_FIND_PARTNERSHIP_MESSAGE = "Cannot find requested Partnership";

    /**
     * No partners hold object message
     */
    public static final String CANNOT_LOCATE_PARTNER_OBJECT_MESSAGE = "Cannot locate requested Object in any Partnership";

    /**
     * User must be admin
     */
    public static final String ADMIN_ONLY = "User must be admin to perform this operation";

    /**
     * Creates a new instance of <code>ConnexienceException</code> without detail message.
     */
    public ConnexienceException()
    {
    }


    /**
     * Constructs an instance of <code>ConnexienceException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ConnexienceException(String msg)
    {
        super(msg);
    }


    public ConnexienceException(Throwable t)
    {
        super(t);
    }


    public ConnexienceException(String message, Throwable t)
    {
        super(message, t);
    }


    public ConnexienceException(String messageFormat, Object... args) 
    {
        super(String.format(messageFormat, args));
    }


    public ConnexienceException(String messageFormat, Throwable t, Object... args) 
    {
        super(String.format(messageFormat, args), t);
    }


    public static String formatMessage(Ticket ticket, ServerObject object)
    {
        try
        {
            User user = EJBLocator.lookupUserDirectoryBean().getUser(ticket, ticket.getUserId());
            return "User: " + user.getDisplayName() + " denied access to: " + object.getName();
        }
        catch (ConnexienceException e)
        {
            return "Error getting user: " + ticket.getUserId() + " when trying to create error message";
        }
    }

    public static String formatMessage(User user, ServerObject object)
    {
        return "User: " + user.getDisplayName() + " denied access to: " + object.getName();
    }

}
