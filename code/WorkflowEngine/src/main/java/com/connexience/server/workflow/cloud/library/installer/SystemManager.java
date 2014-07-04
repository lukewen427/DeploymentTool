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
package com.connexience.server.workflow.cloud.library.installer;

import java.io.File;

/**
 * This class can set permissions on files and directories
 * @author hugo
 */
public interface SystemManager {
    /** set the owner of a directory */
    public boolean setOwnerOnFile(File f, String userName, boolean recurse) throws InstallerException;
    
    /** Set the group of a directory */
    public boolean setGroupOnFile(File f, String groupName, boolean recurse) throws InstallerException;
    
    /** Force removal of a directory tree */
    public boolean removeFile(File f) throws InstallerException;
    
    /** Change permissions on a file */
    public boolean changePermissions(File f, boolean userReadWrite, boolean groupReadWrite, boolean publicReadWrite, boolean recurse) throws Exception;
    
    /** Kill all of the tasks executed by a user */
    public boolean killAllUserTasks(String username) throws Exception;
}
