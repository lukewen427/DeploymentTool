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

import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Group;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * This class is used to build a folder tree.
 * @author hugo
 */
public class FolderHolder {
    /** ID of folder */
    public String id;

    /** ID of the parent folder */
    public String parentId;

    /** Actual folder object */
    public Folder folder;

    /** Name of this folder */
    public String name;

    /** Array of child holders */
    public ArrayList<FolderHolder> children = new ArrayList<>();

    /** Create a JSON Object for this folder */
    public JSONObject createJson() throws Exception {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("hash", id);
        json.put("read", true);
        json.put("write", true);

        JSONArray dirs = new JSONArray();
        for(int i=0;i<children.size();i++){
            dirs.put(children.get(i).createJson());
        }
        json.put("dirs", dirs);
        return json;
    }

    /** Find a specifc folder holder by id */
    public FolderHolder findHolder(String folderId){
        FolderHolder child = getChild(folderId);
        if(child!=null){
            return child;
        } else {
            for(int i=0;i<children.size();i++){
                child = children.get(i).findHolder(folderId);
                if(child!=null){
                    return child;
                }
            }
            return null;
        }
    }

    /** Get a child folder by ID */
    private FolderHolder getChild(String folderId){
        for(int i=0;i<children.size();i++){
            if(children.get(i).id.equals(folderId)){
                return children.get(i);
            }
        }
        return null;
    }

    /** Create a tree for the group folder heirarchy */
    public static FolderHolder createGroupFolderTree(Ticket ticket, String groupId) throws Exception {
        Group g = (Group) EJBLocator.lookupObjectDirectoryBean().getServerObject(ticket, groupId, Group.class);

        List folders = new ArrayList();
        folders.addAll(EJBLocator.lookupObjectDirectoryBean().getAllContainedObjectsUserHasAccessTo(ticket, groupId, Folder.class, 0, 0));
        folders.addAll(EJBLocator.lookupObjectDirectoryBean().getAllObjectsUserHasAccessTo(ticket, groupId, Folder.class, 0, 0));
        folders.addAll(EJBLocator.lookupStorageBean().listGroupLinkFolders(ticket, groupId));

        Hashtable<String,FolderHolder> holderMap = new Hashtable<>();
        Folder folder;
        FolderHolder holder;

        // Create holders
        for(int i=0;i<folders.size();i++){
            folder = (Folder)folders.get(i);
            holder = new FolderHolder();
            holder.folder = folder;
            holder.id = folder.getId();
            holder.parentId = folder.getContainerId();
            holder.name = folder.getName();
            holderMap.put(holder.id, holder);
        }


        // Assign to parent holders
        FolderHolder parent = new FolderHolder();
        parent.folder = new Folder();
        parent.id = g.getId();
        parent.name = g.getName();
        parent.parentId = g.getContainerId();
        holderMap.put(parent.id, parent);

        for(int i=0;i<folders.size();i++){
            folder = (Folder)folders.get(i);
            if(holderMap.containsKey(folder.getContainerId())){
                parent = holderMap.get(folder.getContainerId());
                holder = holderMap.get(folder.getId());
                parent.children.add(holder);
            }
        }

        FolderHolder top = holderMap.get(groupId);
        return top;
    }

    /** Create a tree for a users folders */
    public static FolderHolder createFullFolderTree(Ticket ticket) throws Exception {
        List folders = EJBLocator.lookupObjectDirectoryBean().getOwnedObjects(ticket, ticket.getUserId(), Folder.class, 0, 0);
        folders.addAll(EJBLocator.lookupObjectDirectoryBean().getOwnedObjects(ticket, ticket.getUserId(), WorkflowInvocationFolder.class, 0, 0));
        Hashtable<String,FolderHolder> holderMap = new Hashtable<>();
        Folder folder;
        FolderHolder holder;

        // Create holders
        for(int i=0;i<folders.size();i++){
            folder = (Folder)folders.get(i);
            holder = new FolderHolder();
            holder.folder = folder;
            holder.id = folder.getId();
            holder.parentId = folder.getContainerId();
            holder.name = folder.getName();
            holderMap.put(holder.id, holder);
        }

        // Assign to parent holders
        FolderHolder parent;

        for(int i=0;i<folders.size();i++){
            folder = (Folder)folders.get(i);
            if(holderMap.containsKey(folder.getContainerId())){
                parent = holderMap.get(folder.getContainerId());
                holder = holderMap.get(folder.getId());
                parent.children.add(holder);
            }
        }
        User u = EJBLocator.lookupUserDirectoryBean().getUser(ticket, ticket.getUserId());

        FolderHolder top = holderMap.get(u.getHomeFolderId());
        return top;
    }

    /** Zip from here downwards in the heirarchy */
    public void zipFromHere(Ticket ticket, DocumentRecord zipDocument, String comments) throws Exception {
        Zipper zipper = new Zipper(ticket, zipDocument, comments);
        zipper.setupStreams();
        appendToZipper(ticket, zipper, this);
        zipper.closeStreams();
    }

    /** Append entries to a zipper */
    public void appendToZipper(Ticket ticket, Zipper zipper, FolderHolder fullTree) throws Exception {
        // Add the documents
        List docs = EJBLocator.lookupStorageBean().getFolderDocumentRecords(ticket, id);
        String parentPath;
        DocumentRecord doc;
        for(int i=0;i<docs.size();i++){
            doc = (DocumentRecord)docs.get(i);
            parentPath = fullTree.getPathToFolder(doc.getContainerId());
            zipper.appendDocumentRecord(parentPath, doc);
        }

        // Add the child folders
        FolderHolder child;
        Folder childFolder;
        for(int i=0;i<children.size();i++){
            child = children.get(i);
            childFolder = children.get(i).folder;
            zipper.appendEntry(fullTree.getPathToFolder(childFolder.getId()));
            child.appendToZipper(ticket, zipper, fullTree);
            zipper.closeEntry();
        }
    }
    
    /** Get the path to a folder */
    public String getPathToFolder(String folderId) throws Exception {
        FolderHolder pathEndHolder = this.findHolder(folderId);
        ArrayList<FolderHolder>path = new ArrayList<>();
        
        if(pathEndHolder!=null){
            addFolderToPath(path, pathEndHolder);
            StringBuilder pathBuffer = new StringBuilder();
            for(int i=0;i<path.size();i++){
                pathBuffer.append(path.get(i).name);
                pathBuffer.append("/");
            }
            return pathBuffer.toString();
        } else if(folderId.equals(id)){
            // This is the holder
            return "";
        } else {
            throw new Exception("Folder is not in this tree");
        }
    }
    
    protected void addFolderToPath(ArrayList<FolderHolder> path, FolderHolder folder){
        path.add(0, folder);
        FolderHolder parent = findHolder(folder.parentId);
        if(parent!=null){
            addFolderToPath(path, parent);
        }
    }
}
