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
package com.connexience.server.workflow.cloud.library.installer.users;

import com.connexience.server.workflow.cloud.library.installer.InstallerException;
import com.connexience.server.workflow.cloud.library.installer.UserManager;

/**
 * This class provides a wrapper for the APT package manager
 *
 * @author hugo
 */
public class WindowsUserManager implements UserManager {

    @Override
    public boolean userExists(String userName) throws InstallerException {
        System.out.println("WindowsUserManager.userExists");
        System.out.println("Not yet implemented.  Called with: " + userName);
        return false;
    }

    @Override
    public boolean createUser(String userName, int uID, String groupName, int gID) throws InstallerException {
        System.out.println("WindowsUserManager.createUser");
        System.out.println("Not yet implemented.  Called with: " + userName + " " + groupName);
        return false;
    }

    @Override
    public boolean groupExists(String groupName) throws InstallerException {
        System.out.println("WindowsUserManager.groupExists");
        System.out.println("Not yet implemented.  Called with: " + groupName);
        return false;
    }

    @Override
    public boolean createGroup(String groupName, int gID) throws InstallerException{
        System.out.println("WindowsUserManager.createGroup");
        System.out.println("Not yet implemented.  Called with: " + groupName);
        return false;
    }


}
