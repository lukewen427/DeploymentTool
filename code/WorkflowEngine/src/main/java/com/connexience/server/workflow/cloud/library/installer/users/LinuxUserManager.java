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

import com.connexience.server.util.CommandRunner;
import com.connexience.server.workflow.cloud.library.installer.InstallerException;
import com.connexience.server.workflow.cloud.library.installer.UserManager;
import org.apache.log4j.Logger;

/**
 * This class provides a wrapper for the APT package manager
 *
 * @author hugo
 */
public class LinuxUserManager implements UserManager {
    private static Logger logger = Logger.getLogger(LinuxUserManager.class);

    @Override
    public boolean userExists(String userName) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("id " + userName);
            if(exitCode == 0)
            {
                logger.debug("user " + userName + " exists");
            }
            else
            {
                logger.debug("user " + userName + " does not exist");
            }
            return exitCode == 0;
        } catch (Exception e) {
            throw new InstallerException("Error checking if user exists", e);
        }
    }

    @Override
    public boolean createUser(String userName, int uID, String groupName, int gID) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            logger.debug("Creating user " + userName + " with group " + groupName);
            String command  = "sudo /usr/sbin/useradd -M -N -G " + groupName + " " + userName;
            int exitCode = runner.run(command);
            return exitCode == 0;
        } catch (Exception e) {
            throw new InstallerException("Error adding user", e);
        }
    }

    @Override
    public boolean groupExists(String groupName) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("grep ^" + groupName + ": /etc/group ");
            return exitCode == 0;
        } catch (Exception e) {
            throw new InstallerException("Error checking if user exists", e);
        }
    }

    @Override
    public boolean createGroup(String groupName, int gID) throws InstallerException{
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("sudo /usr/sbin/groupadd " + groupName);
            return exitCode == 0;
        } catch (Exception e) {
            throw new InstallerException("Error adding group", e);
        }

    }


}
