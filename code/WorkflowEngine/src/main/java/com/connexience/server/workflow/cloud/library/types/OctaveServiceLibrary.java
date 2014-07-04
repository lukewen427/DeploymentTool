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
package com.connexience.server.workflow.cloud.library.types;

import com.connexience.server.workflow.cloud.library.*;
import com.connexience.server.workflow.service.DataProcessorCallMessage;
import com.connexience.server.workflow.util.*;

import org.w3c.dom.*;
import java.io.*;

/**
 * This class wraps up an octave service so that the execution code can
 * access the various .m files that are needed.
 * @author nhgh
 */
public class OctaveServiceLibrary extends LibraryWrapper {

    public OctaveServiceLibrary() {
    }

    public OctaveServiceLibrary(CloudWorkflowServiceLibraryItem libraryItem) {
        super(libraryItem);
    }

    @Override
    public void setupWrapper(Document doc, LibraryPreparationReport report) throws Exception {

    }

    /** Copy all the files from the /scripts directory into the invocation directory */
    @Override
    public void prepareInvocationDirectory(File invocationDir, DataProcessorCallMessage message) throws Exception {
        File invocationContextDir = new File(invocationDir, message.getContextId());
        if(!invocationContextDir.exists()){
            invocationContextDir.mkdir();
        }

        File mFilesDir = new File(invocationContextDir, "mfiles");
        if(!mFilesDir.exists()){
            mFilesDir.mkdir();
        }

        // Copy scripts
        /*
        if(getLibraryItem().containsFile("/scripts")){
            File scriptsDir = getLibraryItem().getFile("/scripts");
            if(scriptsDir.isDirectory()){
                ZipUtils.copyDirTree(scriptsDir, mFilesDir);
            }
        }
        */
        
        // Copy the init.m file
        File initM = getLibraryItem().getFile("/init.m");
        if(initM.exists()){
            ZipUtils.copyFileToDirectory(initM, mFilesDir);
        }

        // Copy the main.m file
        File mainM = getLibraryItem().getFile("/main.m");
        if(mainM.exists()){
            ZipUtils.copyFileToDirectory(mainM, mFilesDir);
        }
    }
}