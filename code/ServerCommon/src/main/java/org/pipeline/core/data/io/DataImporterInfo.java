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

/**
 * This class provides registration information for a DataImporter object.
 * @author hugo
 */
public class DataImporterInfo {
    /** Importer name */
    private String name;
    
    /** Importer ID */
    private String id;
    
    /** Importer class */
    private Class importerClass;
    
    /** Editor class if there is one */
    private Class editorClass;
    
    /** Description text for the related importer object */
    private String description;
    
    /** Creates a new instance of DataImporterInfo */
    public DataImporterInfo(String id, String name, Class importerClass) {
        this.id = id;
        this.name = name;
        this.importerClass = importerClass;
    }

    /** Get the name of this importer */
    public String getName() {
        return name;
    }

    /** Get the id of this importer */
    public String getId() {
        return id;
    }

    /** Get the actual class of the importer */
    public Class getImporterClass() {
        return importerClass;
    }

    /** Get the editor class for this importer */
    public Class getEditorClass() {
        return editorClass;
    }

    /** Set the editor panel class for this importer */
    protected void setEditorClass(Class editorClass) {
        this.editorClass = editorClass;
    }

    /** Get the description of this importer */
    public String getDescription() {
        return description;
    }

    /** Set the description of this importer */
    protected void setDescription(String description) {
        this.description = description;
    }
}
