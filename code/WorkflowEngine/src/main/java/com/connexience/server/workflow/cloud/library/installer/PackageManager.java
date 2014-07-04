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

import com.connexience.server.workflow.cloud.library.LibraryPreparationReport;

/**
 * This interface defines a very simple interface to a package manager.
 * @author hugo
 */
public interface PackageManager {

    /** Is a package installed */
    public boolean isInstalled(String packageName, LibraryPreparationReport report) throws InstallerException;
    
    /** Install a package */
    public InstallResult installPackage(String packageName, String extraArguments, LibraryPreparationReport report) throws InstallerException;
    
    /** Update installed packaged */
    public InstallResult updateInstalledPackages(LibraryPreparationReport report) throws InstallerException;
}