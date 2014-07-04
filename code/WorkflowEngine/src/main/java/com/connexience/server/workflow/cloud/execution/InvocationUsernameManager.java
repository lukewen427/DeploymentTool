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
package com.connexience.server.workflow.cloud.execution;
import com.connexience.server.ConnexienceException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;

/**
 * This class provides a map between a invocation ID and a username. It also 
 * stores the group ID and user ID of the workflow engine so that invocation 
 * directory security properties can be managed.
 * @author hugo
 */
public class InvocationUsernameManager {
    private static Logger logger = Logger.getLogger(InvocationUsernameManager.class);
    
    /** Base username */
    private String baseUserName;
    
    /** Capacity */
    private int capacity;

    /** Map */
    private CopyOnWriteArrayList<InvocationUsernameMapEntry> map = new CopyOnWriteArrayList<>();
    
    /** Group name for engine */
    private String engineGroupName = "";
    
    public InvocationUsernameManager(String baseUserName, int capacity) {
        setup(baseUserName, capacity);
    }

    public InvocationUsernameManager() {
        setup("wfuser", 10);
    }

    public void setEngineGroupName(String engineGroupName) {
        this.engineGroupName = engineGroupName;
    }

    public String getEngineGroupName() {
        return engineGroupName;
    }
    
    public final synchronized void setup(String baseUserName, int capacity){
        logger.debug("Setting up username manager with: " + capacity + " entries");
        this.baseUserName = baseUserName;
        this.capacity = capacity;
        map.clear();
        if(capacity>0){
            for(int i=1;i<=capacity + 1;i++){
                map.add(new InvocationUsernameMapEntry(baseUserName + i));
            }
        }        
    }
        
    /** Reserve an entry for an invocation */
    public final synchronized String reserveUsername(String invocationId) throws ConnexienceException {
        InvocationUsernameMapEntry entry = findNextFreeEntry();
        if(entry!=null){
            entry.setInUse(true);
            entry.setInvocationId(invocationId);
            logger.debug("Reserved username: " + entry.getUsername());
            return entry.getUsername();
        } else {
            logger.error("No available usernames in map");
            throw new ConnexienceException("No available usernames in map");
        }
    }
    
    /** Release an entry for an invocation */
    public final synchronized void releaseUsername(String invocationId) {
        InvocationUsernameMapEntry entry = findEntryForInvocation(invocationId);
        if(entry!=null){
            entry.setInUse(false);
            entry.setInvocationId(null);
            logger.debug("Released username: " + entry.getUsername());
        }
    }
    
    /** Find an entry for an invocation */
    private InvocationUsernameMapEntry findEntryForInvocation(String invocationId){
        for(InvocationUsernameMapEntry i : map){
            if(invocationId.equals(i.getInvocationId())){
                return i;
            } 
        }
        return null;
    }
    
    /** Find the first non-reserved entry */
    private InvocationUsernameMapEntry findNextFreeEntry(){
        for(InvocationUsernameMapEntry i : map){
            if(!i.isInUse()){
                return i;
            }
        }
        return null;
    }
    
    /** Entry within the username map */
    private class InvocationUsernameMapEntry {
        private String invocationId;
        private String username;
        private boolean inUse;

        public InvocationUsernameMapEntry(String username) {
            this.username = username;
            this.inUse = false;
        }

        public String getInvocationId() {
            return invocationId;
        }

        public void setInUse(boolean inUse) {
            this.inUse = inUse;
        }

        public boolean isInUse() {
            return inUse;
        }

        public void setInvocationId(String invocationId) {
            this.invocationId = invocationId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}