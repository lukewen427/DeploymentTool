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
package com.connexience.server.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains a list of Permission objects and can be
 * queried for read / write / etc access
 * @author hugo
 */
public class PermissionList implements Serializable {
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


    /** List of permissions */
    private ArrayList<Permission> permissionList = new ArrayList<>();
    
    /** Object that the list refers to */
    private String objectId = "";
    
    /** Creates a new instance of PermissionList */
    public PermissionList() {
    }
    
    /** Creates a new PermissionList with a list of existing permissions */
    public PermissionList(String objectId, List<Permission> permissions){
        this.objectId = objectId;
        addPermissions(permissions);
    }
    
    /** Get the PermissionList array */
    public ArrayList<Permission> getPermissionList(){
        return permissionList;
    }
    
    /** Set the PermissionList array */
    public void setPermissionList(ArrayList<Permission> permissionList){
        this.permissionList = permissionList;
    }
    
    /** Add permissions from a list */
    public void addPermissions(List<Permission> permissions){
        Permission permission;
        for(int i=0;i<permissions.size();i++){
            permission = permissions.get(i);
            if(permission.getTargetObjectId().equals(objectId)){
                permissionList.add(permission);
            }
        }
    }
    
    /** Is there a specific permission allowing the specified action from a 
     * group of principals */
    public boolean permissionExists(String permissionType, List<String> principalList){
        Permission permission;
        Iterator<Permission> it = permissionList.iterator();
        while(it.hasNext()){
            permission = it.next();
            if(principalList.contains(permission.getPrincipalId())){
                if(permission.getType().equals(permissionType)){
                    return true;
                }
            }
        }
        return false;
    }
    
    /** Does a specified permission exist */
    public boolean permissionExists(String permissionType){
        Iterator<Permission> it = permissionList.iterator();
        while(it.hasNext()){
            if(it.next().getType().equals(permissionType)){
                return true;
            }
        }
        return false;
    }
    
    /** Does a specified permission exist for a single principal */
    public boolean permissionExists(String permissionType, String principalId){
        Permission permission;
        Iterator<Permission> it = permissionList.iterator();
        while(it.hasNext()){
            permission = it.next();
             if(permission.getType().equals(permissionType) && permission.getPrincipalId().equals(principalId)){
                return true;
            }
        }
        return false;
    }

    /** Get the size of this list */
    public final int getSize(){
        return permissionList.size();
    }
    
    /** Add a set of permissions */
    public void addPermissions(Permission[] p){
        for(int i=0;i<p.length;i++){
            addPermission(p[i]);
        }
    }
    
    /** Add a permission */
    public void addPermission(Permission p){
        if(p.getTargetObjectId().equals(objectId)){
            permissionList.add(p);
        }
    }
    
    /** Remove a permission */
    public void removePermission(Permission p){
        permissionList.remove(p);
    }
    
    /** Remove a set of permissions */
    public void removePermissions(Permission[] p){
        for(int i=0;i<p.length;i++){
            removePermission(p[i]);
        }
    }
    
    /** Get a specific permission */
    public Permission getPermission(int index){
        return (Permission)permissionList.get(index);
    }
    
    /** Get a list of permissions by index */
    public Permission[] getPermissions(int[] index){
        Permission[] results = new Permission[index.length];
        for(int i=0;i<index.length;i++){
            results[i] = getPermission(index[i]);
        }
        return results;
    }
    
    /** Get the object id that this list refers to */
    public String getObjectId() {
        return objectId;
    }

    /** Set the object id that this list refers to */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    /** Get all of the objectIds from this permission list */
    public String[] getAllPrincipalIds(){
        String[] ids = new String[permissionList.size()];
        int count = 0;
        Iterator<Permission> i = permissionList.iterator();
        while(i.hasNext()){
            ids[count] = ((Permission)i.next()).getPrincipalId();
            count++;
        }
        return ids;
    }
    
    /** Create a copy for a new target object */
    public PermissionList createCopyForObject(String objectId){
        Iterator<Permission> i = permissionList.iterator();
        Permission existing;
        Permission copy;
        PermissionList newList = new PermissionList();
        newList.setObjectId(objectId);
        
        while(i.hasNext()){
            existing = (Permission)i.next();
            copy = new Permission();
            copy.setTargetObjectId(objectId);
            copy.setPrincipalId(existing.getPrincipalId());
            copy.setType(existing.getType());
            newList.addPermission(copy);
        }
        return newList;
    }
}