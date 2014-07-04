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

import static com.connexience.server.workflow.cloud.library.installer.UserManagerFactory.getOsName;
import com.connexience.server.workflow.cloud.library.installer.system.UnixSystemManager;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * This class provides a factory that can create file system managers for a set
 * of different platforms.
 * @author hugo
 */
public class SystemManagerFactory {
    private static Logger logger = Logger.getLogger(SystemManagerFactory.class);
    
    /** List of package managers */
    private static HashMap<String, Class<?>> managers = new HashMap<>();
    static {
        managers.put("linux", UnixSystemManager.class);
        managers.put("osx", UnixSystemManager.class);
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
    public static SystemManager newInstance() throws InstallerException {
        String type = getOsName();
        if(managers.containsKey(type)){
            try {
                Object mgr = managers.get(type).newInstance();
                return (SystemManager)mgr;
            } catch (Exception e){
                throw new InstallerException("Error instantiating system manager: " + e.getMessage(), e);
            }
        } else {
            throw new InstallerException("System manager: " + type + " not found");
        }
    }    
}