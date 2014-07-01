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
package com.connexience.server.jms;

import com.connexience.server.ConnexienceException;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JMSProperties
{
    private static final Logger logger = Logger.getLogger(JMSProperties.class);

    private static Properties properties;
    
    public static String getHostname() throws ConnexienceException
    {
        return getProperties().getProperty("hostname");
    }

    public static String getPassword() throws ConnexienceException
    {
        return getProperties().getProperty("password");
    }
    
    private synchronized static Properties getProperties() throws ConnexienceException
    {
        try
        {
            if (properties == null)
            {
                properties = new Properties();
                InputStream is = JMSProperties.class.getResourceAsStream("/META-INF/jms.properties");
                properties.load(is);
                is.close();
            }
            return properties;
        }
        catch (IOException e)
        {
            logger.warn("Failed to load /META-INF/jms.properties");
            throw new ConnexienceException("Failed to load /META-INF/jms.properties", e);
        }
    }
    
    public static String getUsername() throws ConnexienceException
    {
        return getProperties().getProperty("username");
    }
    
    public static boolean isUser() throws ConnexienceException
    {
        return getUsername() != null;
    }
}
