/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.data.io;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class defines a factory that can be used to create data import
 * objects. It is provided so that multiple data import types can be
 * added. Installers can register importers with this factory.
 * @author hugo
 */
public abstract class DataImporterFactory {
    /** Registered importers */
    private static Hashtable importers;
    
    /** Register an importer */
    public static void registerImporter(DataImporterInfo info){
        importers.put(info.getId(), info);
    }
    
    /** Unregister an importer */
    public static void unregisterImporter(String id){
        importers.remove(id);
    }
    
    /** Create an importer */
    public static DataImporter createImporter(String id) throws DataImportException {
        if(importers.containsKey(id)){
            try {
                DataImporterInfo info = (DataImporterInfo)importers.get(id);
                return (DataImporter)info.getImporterClass().newInstance();
            } catch (Exception e){
                throw new DataImportException("Cannot create specified importer: " + e.getMessage());
            }
        } else {
            throw new DataImportException("Specified data importer does not exist");
        }
    }
    
    /** Does a specified importer have an editor */
    public static boolean importerHasEditor(String id) {
        if(importers.containsKey(id)){
            DataImporterInfo info = (DataImporterInfo)importers.get(id);
            if(info.getEditorClass()!=null){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
        
    }
    
    /** Does a specified importer have an editor */
    public static boolean importerHasEditor(DataImporter importer) {
        DataImporterInfo info = findInfoForImporter(importer);
        if(info!=null){
            if(info.getEditorClass()!=null){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /** Create an editor for an importer */
    public static DataImporterEditorPanel createEditor(DataImporter importer) throws DataImportException {
        DataImporterInfo info = findInfoForImporter(importer);
        if(info!=null){
            try {
                DataImporterEditorPanel editor = (DataImporterEditorPanel)info.getEditorClass().newInstance();
                editor.setImporter(importer);
                return editor;
                
            } catch (Exception e){
                throw new DataImportException("Cannot create specified importer");
            }
            
        } else {
            throw new DataImportException("Cannot find information for specified importer");
        }
    }
    
    /** Find the importer info object corresponding to a DataImporter instance */
    public static DataImporterInfo findInfoForImporter(DataImporter importer) {
        Enumeration e = importers.elements();
        DataImporterInfo info;
        while(e.hasMoreElements()){
            info = (DataImporterInfo)e.nextElement();
            if(info.getImporterClass().equals(importer.getClass())){
                return info;
            }
        }
        return null;
    }
    
    /** List all of the importers */
    public Enumeration getImporters(){
        return importers.elements();
    }
}
