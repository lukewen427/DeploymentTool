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
package com.connexience.server.workflow.json;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
import org.pipeline.core.xmlstorage.replacement.CategoryReplacer;

/**
 * This class categorise properties into standard groups
 * @author hugo
 */
public class StandardPropertiesCategoriser extends CategoryReplacer {

    public StandardPropertiesCategoriser() {
        super();
        setAutomaticDefaultReplacement(true);
        setOnlyNullReplaced(false);
        setDefaultCategory("Block");
    }

    @Override
    public void replaceCategories(XmlDataStore store) throws XmlStorageException {
        // Some well known properties for the replacer
        addReplacement("WaitForDebugConnection", "Debugging");
        addReplacement("DebugConnectionTimeout", "Debugging");
        addReplacement("DebugMode", "Debugging");
        addReplacement("DebugSuspended", "Debugging");
        addReplacement("DebugPort", "Debugging");
        addReplacement("StdOutSize", "Debugging");
        
        addReplacement("AllowRetriesOnTimeout", "Engine");
        addReplacement("StreamingChunkSize", "Engine");
        addReplacement("EnforceInvocationTimeout", "Engine");
        addReplacement("InvocationTimeout", "Engine");
        addReplacement("TimeoutRetries", "Engine");
        addReplacement("persistData", "Engine");
        addReplacement("ProgressUpdateInterval", "Engine");
        
        addReplacement("Name", "Block");
        
        addReplacement("Label", "Display");
        addReplacement("Caption", "Display");
        super.replaceCategories(store);
    }
    
    
}