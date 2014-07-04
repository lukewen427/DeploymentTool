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
import com.connexience.server.util.CommandSet;
import com.connexience.server.workflow.cloud.library.installer.InstallerException;
import com.connexience.server.workflow.cloud.library.installer.UserManager;

/**
 * This class provides a wrapper for the APT package manager
 *
 * @author hugo
 */
public class MacUserManager implements UserManager {

    @Override
    public boolean userExists(String userName) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("sudo dscl . -list /Users/" + userName);
            return exitCode==0;
        } catch (Exception e){
            throw new InstallerException("Error checking if user: " + userName + " exists", e);
        }
    }

    @Override
    public boolean createUser(String userName, int uID, String groupName, int gID) throws InstallerException {
        CommandSet commands = new CommandSet();
        commands.add("sudo dscl . -create /Users/" + userName);
        commands.add("sudo dscl . -create /Users/" + userName + " UniqueID " + uID);
        commands.add("sudo dscl . -create /Users/" + userName +" UserShell /usr/bin/false");
        commands.add("sudo dscl . -create /Users/" + userName + " RealName 'Workflow User'");
        commands.add("sudo dscl . -create /Users/" + userName + " NFSHomeDirectory /");
        commands.add("sudo dscl . -create /Users/" + userName + " PrimaryGroupID " + gID);
        commands.add("sudo dscl . -create /Users/" + userName + " Password \\*");
        boolean success = commands.execute();
        if(success){
            return true;
        } else {
            throw new InstallerException("Error executing command: " + commands.getFailedCommand());
        }
    }

    @Override
    public boolean groupExists(String groupName) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("sudo dscl . -read /Groups/" + groupName);
            return exitCode == 0;
        } catch (Exception e) {
            throw new InstallerException("Error checking if user exists", e);
        }
    }

    @Override
    public boolean createGroup(String groupName, int gID) throws InstallerException {
        CommandSet commands = new CommandSet();
        if (!groupExists(groupName)) {
            commands.add("sudo dscl . -create /Groups/" + groupName);
            commands.add("sudo dscl . -create /Groups/" + groupName + " PrimaryGroupID " + gID);
            commands.add("sudo dscl . -create /Groups/" + groupName + " Password \\*");
            boolean success = commands.execute();
            if (success) {
                return true;
            } else {
                throw new InstallerException("Error executing command: " + commands.getFailedCommand());
            }
        } else {
            return true;
        }
    }
}