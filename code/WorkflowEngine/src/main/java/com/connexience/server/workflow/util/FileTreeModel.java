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
package com.connexience.server.workflow.util;

import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.File;
import java.util.*;

/**
 * This class provides a simple tree model for a file system
 * @author nhgh
 */
public class FileTreeModel implements TreeModel {
    // We specify the root directory when we create the model.
    protected FileHolder root;

    public FileTreeModel(File rootFile) {
        this.root = new FileHolder(rootFile);
    }

    /** Rebuild the model */
    public void rebuild(){
        File rootFile = root.getFile();
        root = new FileHolder(rootFile);
        notifyChange();
    }
    
    private ArrayList<TreeModelListener> listeners = new ArrayList<>();

    // The model knows how to return the root object of the tree
    public Object getRoot() {
        return root;
    }

    // Tell JTree whether an object in the tree is a leaf or not
    public boolean isLeaf(Object node) {
        if(node instanceof FileHolder){
            return !((FileHolder)node).isDirectroy();
        } else {
            return true;
        }
    }

    // Tell JTree how many children a node has
    public int getChildCount(Object parent) {
        if(parent instanceof FileHolder){
            return ((FileHolder)parent).getChildCount();
        } else {
            return 0;
        }
    }

    // Fetch any numbered child of a node for the JTree.
    // Our model returns File objects for all nodes in the tree.  The
    // JTree displays these by calling the File.toString() method.
    public Object getChild(Object parent, int index) {
        if(parent instanceof FileHolder){
            FileHolder h = (FileHolder)parent;
            if(!h.isDirectroy()){
                return null;
            } else {
                return h.getChildFile(index);
            }
        } else {
            return "NOTHING";
        }
    }

    // Figure out a child's position in its parent node.
    public int getIndexOfChild(Object parent, Object child) {
        if(parent instanceof FileHolder){
            return ((FileHolder)parent).indexOf((FileHolder)child);
        } else {
            return -1;
        }
    }

    // This method is only invoked by the JTree for editable trees.
    // This TreeModel does not allow editing, so we do not implement
    // this method.  The JTree editable property is false by default.
    public void valueForPathChanged(TreePath path, Object newvalue) {
    }

    // Since this is not an editable tree model, we never fire any events,
    // so we don't actually have to keep track of interested listeners.
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void notifyChange() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
        }
    }

    /** Class to hold a File / Directory. This is used so that the name can be
     * shortened to something more sane */
    public class FileHolder {
        File file;
        ArrayList<FileHolder> children = new ArrayList<>();
        
        public FileHolder(File file) {
            this.file = file;
            buildChildren();
        }

        public File getFile(){
            return file;
        }
        
        private void buildChildren(){
            children.clear();
            if(file.isDirectory()){
                File[] files = file.listFiles();

                // Directories first
                for(int i=0;i<files.length;i++){
                    if(files[i].isDirectory()){
                        children.add(new FileHolder(files[i]));
                    }
                }

                // Now children
                for(int i=0;i<files.length;i++){
                    if(files[i].isFile()){
                        children.add(new FileHolder(files[i]));
                    }
                }
            }
        }

        public int indexOf(FileHolder child){
            if(!isDirectroy()){
                return children.indexOf(child);
            } else {
                return -1;
            }
        }

        public FileHolder getChildFile(int index){
            return children.get(index);
        }

        public int getChildCount(){
            return children.size();
        }

        public boolean isDirectroy(){
            return file.isDirectory();
        }

        @Override
        public String toString() {
            return file.getName();
        }
    }
}
