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

/**
 *
 * @author hugo
 */
public class InstallerException extends Exception
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


    private InstallResult result = null;
    
    /**
     * Creates a new instance of
     * <code>InstallerException</code> without detail message.
     */
    public InstallerException(InstallResult result) {
        this.result = result;
    }

    /**
     * Constructs an instance of
     * <code>InstallerException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InstallerException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of
     * <code>InstallerException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public InstallerException(InstallResult result, String msg) {
        super(msg);
        this.result = result;
    }

    public InstallerException(InstallResult result, String message, Throwable cause) {
        super(message, cause);
        this.result = result;
    }

    public InstallerException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InstallResult getResult() {
        return result;
    }
    
    
}
