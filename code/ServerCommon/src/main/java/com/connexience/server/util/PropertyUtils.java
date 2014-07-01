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

import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.properties.PropertyItem;
import com.connexience.server.model.security.Ticket;

/**
 * This class provides some simple property utility methods
 * @author nhgh
 */
public class PropertyUtils
{
    public static String getSystemProperty(String groupName, String propertyName, String defaultValue){
        try {
            Ticket ticket = EJBLocator.lookupTicketBean().createPublicWebTicket();
            PropertyItem item = EJBLocator.lookupPropertiesBean().getSystemProperty(ticket, groupName, propertyName);
            if(item!=null){
                return item.getValue();
            } else {
                System.out.println("Warning: Property " + groupName + "/" + propertyName + " is undefined using: " + defaultValue);
                return defaultValue;
            }
        } catch (Exception e){
            System.out.println("Error reading system property: " + groupName + "/" + propertyName + ": " + e.getMessage());
            return defaultValue;
        }
    }

    public static String getSystemProperty(Ticket ticket, String groupName, String propertyName, String defaultValue){
        try {
            PropertyItem item = EJBLocator.lookupPropertiesBean().getSystemProperty(ticket, groupName, propertyName);
            if(item!=null){
                return item.getValue();
            } else {
                System.out.println("Warning: Property " + groupName + "/" + propertyName + " is undefined using: " + defaultValue);
                return defaultValue;
            }
        } catch (Exception e){
            System.out.println("Error reading system property: " + groupName + "/" + propertyName + ": " + e.getMessage());
            return defaultValue;
        }
    }
}
