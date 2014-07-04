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
package com.connexience.server.workflow.cloud.library.installer.system;

import com.connexience.server.util.CommandRunner;
import com.connexience.server.workflow.cloud.library.installer.SystemManager;
import com.connexience.server.workflow.cloud.library.installer.InstallerException;
import java.io.File;

/**
 * This class provides a UNIX implementation of the FilesystemManager
 * @author hugo
 */
public class UnixSystemManager implements SystemManager {

    @Override
    public boolean setOwnerOnFile(File f, String userName, boolean recurse) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        
        if(recurse){
            try {
                int exitCode = runner.run("sudo chown -R " + userName + " " + f.getCanonicalPath());
                return exitCode==0;
            } catch (Exception e){
                throw new InstallerException("Error setting owner recursively", e);
            }
                       
        } else {
            try {
                int exitCode = runner.run("sudo chown " + userName + " " + f.getCanonicalPath());
                return exitCode==0;
            } catch (Exception e){
                throw new InstallerException("Error setting owner", e);
            }            
        }
    }

    @Override
    public boolean setGroupOnFile(File f, String groupName, boolean recurse) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        
        if(recurse){
            try {
                int exitCode = runner.run("sudo chgrp -R " + groupName + " " + f.getCanonicalPath());
                return exitCode==0;
            } catch (Exception e){
                throw new InstallerException("Error setting owner recursively", e);
            }
                       
        } else {
            try {
                int exitCode = runner.run("sudo chown " + groupName + " " + f.getCanonicalPath());
                return exitCode==0;
            } catch (Exception e){
                throw new InstallerException("Error setting owner", e);
            }            
        }
    }

    @Override
    public boolean removeFile(File f) throws InstallerException {
        CommandRunner runner = new CommandRunner();
        try {
            int exitCode = runner.run("sudo rm -rf " + f.getCanonicalPath());
            return exitCode==0;
        } catch (Exception e){
            throw new InstallerException("Error removing file", e);
        }        
    }

    @Override
    public boolean changePermissions(File f, boolean userReadWrite, boolean groupReadWrite, boolean publicReadWrite, boolean recurse) throws Exception {
        CommandRunner runner = new CommandRunner();
        
        // Build correct permissions mask
        StringBuilder value = new StringBuilder("0");
        if(userReadWrite){
            value.append("7");
        } else {
            value.append("0");
        }

        if(groupReadWrite){
            value.append("7");
        } else {
            value.append("0");
        }

        if(publicReadWrite){
            value.append("7");
        } else {
            value.append("0");
        }
            
        // Set on file / directory
        if(recurse){
            int exitCode = runner.run("sudo chmod -R " + value.toString() + " " + f.getCanonicalPath());
            return exitCode==0;
        } else {
            int exitCode = runner.run("sudo chmod " + value.toString() + " " + f.getCanonicalPath());
            return exitCode==0;
        }
    }

    @Override
    public boolean killAllUserTasks(String username) throws Exception {
        CommandRunner runner = new CommandRunner();
        int exitCode = runner.run("sudo killall -u " + username + " -9");
        return exitCode==0;
    }
    
}
