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

import org.apache.log4j.*;

import java.util.*;
import java.io.*;

/**
 * This class provides an installer that can create package managers for 
 * a number of Unix like operating systems. It supports Macports, APT and YUM
 * @author hugo
 */
public class Installer {
    private static Logger logger = Logger.getLogger(Installer.class);
    
    /** List of package managers */
    private static HashMap<String, Class<?>> managers = new HashMap<>();
    static {
        managers.put("macports", com.connexience.server.workflow.cloud.library.installer.installer.MacportsPackageManager.class);
        managers.put("apt", com.connexience.server.workflow.cloud.library.installer.installer.AptPackageManager.class);
        managers.put("yum", com.connexience.server.workflow.cloud.library.installer.installer.YumPackageManager.class);
    }
    
    private static boolean isWindows = false;
    private static boolean isLinux = false;
    private static boolean isMac = false;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        isWindows = os.contains("win");
        isLinux = os.contains("nux") || os.contains("nix");
        isMac = os.contains("mac");
        logger.debug("Detected OS: " + Installer.getOsName());
        logger.debug("Detected package manager: " + Installer.getPackageManagerName());
    }

    public static boolean isWindows() { return isWindows; }
    public static boolean isLinux() { return isLinux; }
    public static boolean isMac() { return isMac; }
    
    public static String getOsName(){
        if(isLinux){
            return "linux";
        } else if(isMac){
            return "osx";
        } else if(isWindows){
            return "windows";
        } else {
            return null;
        }
    }
    
    /** Create a new package manager */
    public static PackageManager createManager(String type) throws InstallerException {
        if(managers.containsKey(type)){
            try {
                Object mgr = managers.get(type).newInstance();
                return (PackageManager)mgr;
            } catch (Exception e){
                throw new InstallerException("Error instantiating package manager: " + e.getMessage(), e);
            }
        } else {
            throw new InstallerException("Package manager: " + type + " not found");
        }
    }
    
    /** Try and work out what package manager to use */
    public static String getPackageManagerName() {
        File macports = new File("/opt/local/bin/port");
        File apt = new File("/usr/bin/apt-get");
        File yum = new File("/usr/bin/yum");
        
        if(macports.exists() && macports.isFile()){
            // Have macports
            return "macports";
            
        } else if(apt.exists() && apt.isFile()){
            // Have apt
            return "apt";
            
        } else if(yum.exists() && yum.isFile()){
            // Have yum
            return "yum";
        } else {
            return null;
        }
    }
}