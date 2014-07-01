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
package com.connexience.server.util;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * This class provides a single static registry for registering RMI servers
 * @author hugo
 */
public class RegistryUtil {
    /** RMI Registry */
    private static Registry rmiRegistry;
    
    /** Default RMI Port */
    public static final int DEFAULT_PORT = 2099;

    /** Registry to a registry and start a new server on the specified port if it isn't running */
    public static void registerToRegistry(String name, Remote object, int port, boolean create) throws RemoteException {
        Registry r = null;
        try {
            r = LocateRegistry.getRegistry(port);
            
        } catch (RemoteException ex){
            if(create){
                r = LocateRegistry.createRegistry(port);
            } else {
                throw ex;
            }
        }
        
        if(r!=null){
            r.rebind(name, object);
        }
    }

    /** Start this server and a Naming server if it isn't already running */
    public static void registerToRegistry(String name, Remote obj, boolean create) throws RemoteException, MalformedURLException {
        if (name == null) throw new IllegalArgumentException("registration name can not be null");
        
        // Create registry if it doesn't exist
        if(rmiRegistry==null){
            try {
                rmiRegistry = LocateRegistry.getRegistry(DEFAULT_PORT);
            } catch (Exception e){
                
            }
            
            try {
                rmiRegistry = LocateRegistry.createRegistry(DEFAULT_PORT);
            } catch (Exception e){
                if(create){
                    rmiRegistry = LocateRegistry.createRegistry(DEFAULT_PORT);
                }
            }
            
            if(rmiRegistry==null && create){
                rmiRegistry = LocateRegistry.createRegistry(DEFAULT_PORT);
            }
        }
        
        if(rmiRegistry!=null){
            try {
                rmiRegistry.unbind(name);
            } catch (Exception e){}
            rmiRegistry.rebind(name, obj);
        }


    }	
    
    /** Unregister an object from the registry */
    public static void unregisterFromRegistry(String name) throws RemoteException, NotBoundException, MalformedURLException {
        // Create registry if it doesn't exist
        if(rmiRegistry==null){
            try {
                rmiRegistry = LocateRegistry.getRegistry(DEFAULT_PORT);
            } catch (Exception e){
                
            }
            
            try {
                rmiRegistry = LocateRegistry.createRegistry(DEFAULT_PORT);
            } catch (Exception e){
            }
        }
        
        if(rmiRegistry!=null){
            rmiRegistry.unbind(name);
        }
    }
    
    /** Lookup the compute manager on a specified server */
    public static Object lookup(String host, String name) throws RemoteException {
        try {
            Registry r = LocateRegistry.getRegistry(host, DEFAULT_PORT);
            return r.lookup(name);
        } catch (Exception e){
            throw new RemoteException("Cannot locate object: " + e.getMessage());
        }
    }

    /** Lookup on a port and host */
    public static Object lookup(String host, int port, String name) throws RemoteException, NotBoundException {
        Registry r = LocateRegistry.getRegistry(host, port);
        return r.lookup(name);
    }

    /** Find the next available registry port */
    public static synchronized int findNextRegistryPort(int startPort, int maxPorts) throws RemoteException {
        boolean found = false;
        int i = startPort;
        int foundPort = -1;

        while(i<startPort+maxPorts && found == false){
            if(!registryExistsOnPort(i)){
                found = true;
                foundPort = i;
            }
            i++;
        }
        return foundPort;
    }

    /** Does a registry exist on a specified port on this machine */
    public static synchronized boolean registryExistsOnPort(int port) {
        try {
            Registry r = LocateRegistry.getRegistry(port);
            if(r!=null){
                r.list();       // This will fail for no registry
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
}
