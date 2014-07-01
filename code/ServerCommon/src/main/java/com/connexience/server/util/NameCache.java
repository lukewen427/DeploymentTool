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

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.security.Ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class caches object names and uses the object directory to look
 * up any unknown objects.
 * @author hugo
 */
public class NameCache implements Serializable {
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


    /** Lookup list */
    private HashMap<String,String> nameMap = new HashMap<>();

    /** Age list */
    private ArrayList<String> ageMap = new ArrayList<>();

    /** Maximum cache size */
    private int maxSize;

    public NameCache(int maxSize) {
        this.maxSize = maxSize;
    }

    /** Get the name of an object */
    public synchronized String getObjectName(Ticket ticket, String id) throws ConnexienceException {
        if(nameMap.containsKey(id)){
            return nameMap.get(id);
        } else {
            String name = EJBLocator.lookupObjectInfoBean().getObjectName(ticket, id);
            if(name!=null){
                if(nameMap.size()>=maxSize){
                    evictOldest();
                }
                nameMap.put(id, name);
                ageMap.add(id);
                return name;
            } else {
                return "UNKNOWN";
            }
        }
    }

    /** Evict the oldest entry */
    private void evictOldest(){
        int lastIndex = ageMap.size() - 1;
        if(lastIndex>0){
            String id = ageMap.get(lastIndex);
            ageMap.remove(lastIndex);
            nameMap.remove(id);
        }
    }

    /** Evict an in from the cache */
    public synchronized void evictId(String id){
        int index = ageMap.indexOf(id);
        if(index!=-1){
            ageMap.remove(index);
            nameMap.remove(id);
        }
    }
}