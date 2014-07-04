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
package com.connexience.server.workflow.cloud.cmd;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import org.pipeline.core.xmlstorage.prefs.*;
import org.pipeline.gui.xmlstorage.prefs.PreferenceManagerEditor;

/**
 * This command opens an editor window for the preferences file
 * @author hugo
 */
public class EditProperties {
    public static void main(String[] args){
        try {
            File propertiesFile = new File(System.getProperty("user.home") + File.separator + ".inkspot" + File.separator + "engine.xml");

            if(PreferenceManager.loadPropertiesFromFile(propertiesFile)){
                PreferenceManagerEditor editor = new PreferenceManagerEditor();
                editor.setAutoSave(true);
                editor.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosed(WindowEvent we) {
                        super.windowClosed(we);
                        System.exit(0);
                    }

                    @Override
                    public void windowClosing(WindowEvent we) {
                        super.windowClosing(we);
                        System.exit(0);
                    }
                });
                editor.setVisible(true); 
                
            }            
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
