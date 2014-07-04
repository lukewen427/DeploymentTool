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
import java.io.*;

/**
 * This class contains the results of installing a package.
 * @author hugo
 */
public class InstallResult implements Serializable
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


    public enum InstallStatus {
        INSTALLED_OK,
        INSTALL_FAILED
    }
    
    /** System out data */
    private String systemOut;
    
    /** System error data */
    private String systemErr;
    
    /** Exit code */
    private int exitCode;

    /** Status code */
    private InstallStatus status = InstallStatus.INSTALLED_OK;
    
    public InstallResult(InstallStatus status, String systemOut, String systemErr, int exitCode) {
        this.systemOut = systemOut;
        this.systemErr = systemErr;
        this.exitCode = exitCode;
        this.status = status;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getSystemErr() {
        return systemErr;
    }

    public String getSystemOut() {
        return systemOut;
    }

    public InstallStatus getStatus() {
        return status;
    }    
    
}