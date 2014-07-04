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

import com.connexience.server.workflow.cloud.library.installer.users.LinuxUserManager;
import com.connexience.server.workflow.cloud.library.installer.users.MacUserManager;
import com.connexience.server.workflow.cloud.library.installer.users.WindowsUserManager;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * This class provides an installer that can create package managers for 
 * a number of Unix like operating systems. It supports Macports, APT and YUM
 * @author hugo
 */
public class UserManagerFactory {
    private static Logger logger = Logger.getLogger(UserManagerFactory.class);
    
    /** List of package managers */
    private static HashMap<String, Class<?>> managers = new HashMap<>();
    static {
        managers.put("linux", LinuxUserManager.class);
        managers.put("osx", MacUserManager.class);
        managers.put("windows", WindowsUserManager.class);
    }
    
    private static boolean isWindows = false;
    private static boolean isLinux = false;
    private static boolean isMac = false;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        isWindows = os.contains("win");
        isLinux = os.contains("nux") || os.contains("nix");
        isMac = os.contains("mac");
        logger.debug("Detected OS: " + UserManagerFactory.getOsName());
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
    
    /** Create a new user manager */
    public static UserManager newInstance() throws InstallerException {
        String type = getOsName();
        if(managers.containsKey(type)){
            try {
                Object mgr = managers.get(type).newInstance();
                return (UserManager)mgr;
            } catch (Exception e){
                throw new InstallerException("Error instantiating user manager: " + e.getMessage(), e);
            }
        } else {
            throw new InstallerException("User manager: " + type + " not found");
        }
    }
    

}