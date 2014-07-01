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
package org.pipeline.gui.xmlstorage.prefs;

import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.prefs.PreferenceManager;
import org.pipeline.gui.xmlstorage.table.XmlDataStoreEditorTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * This tool provides an editor window that can be used to edit PreferenceManager
 * objects
 * @author hugo
 */
public class PreferenceManagerEditor extends JFrame {
    DefaultListModel systemGroupNames = new DefaultListModel();
    DefaultListModel editableGroupNames = new DefaultListModel();
    boolean viewingSystemProperties = true;
    JList namesList;
    XmlDataStoreEditorTable table;
    JSplitPane splitter;
    JToolBar toolbar;
    JButton systemButton;
    JButton editableButton;
    JButton saveButton;
    JButton loadButton;
    JPanel leftPanel;
    JPanel rightPanel;
    JLabel listLabel;
    JLabel propertiesLabel;
    boolean autoSave = false;
    
    public PreferenceManagerEditor() {
        setPreferredSize(new Dimension(640, 480));


        setLayout(new BorderLayout());
        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());

        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        splitter = new JSplitPane();
        
        namesList = new JList();
        namesList.setBorder(BorderFactory.createEtchedBorder());
        leftPanel.add(namesList, BorderLayout.CENTER);
        listLabel = new JLabel("Nothing Selected");
        leftPanel.add(listLabel, BorderLayout.NORTH);

        splitter.setLeftComponent(leftPanel);

        table = new XmlDataStoreEditorTable();


        rightPanel.add(table, BorderLayout.CENTER);
        propertiesLabel = new JLabel("Property List");
        rightPanel.add(propertiesLabel, BorderLayout.NORTH);

        splitter.setRightComponent(rightPanel);
        getContentPane().add(splitter, BorderLayout.CENTER);

        toolbar = new JToolBar();
        toolbar.setRollover(false);
        systemButton = new JButton("System Properties");
        systemButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/server_link.png")));
        systemButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                viewingSystemProperties = true;
                listLabel.setText("System Properties");
                namesList.setModel(systemGroupNames);
            }
        });
        systemButton.setBorderPainted(false);

        editableButton = new JButton("Editable Properties");
        editableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/user.png")));
        editableButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                viewingSystemProperties = false;
                listLabel.setText("Editable Properties");
                namesList.setModel(editableGroupNames);
            }
        });
        editableButton.setBorderPainted(false);

        loadButton = new JButton("Load");
        loadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/folder.png")));
        loadButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editFile();
            }
        });
        loadButton.setBorderPainted(false);


        saveButton = new JButton("Save");
        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/pipeline/gui/xmlstorage/resource/disk.png")));
        saveButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                PreferenceManager.saveProperties();
            }
        });
        saveButton.setBorderPainted(false);
        
        toolbar.add(systemButton);
        toolbar.add(editableButton);
        toolbar.add(loadButton);
        toolbar.add(saveButton);
        getContentPane().add(toolbar, BorderLayout.NORTH);

        pack();
        setSize(640, 480);
        setLocation(20, 20);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if(autoSave){
                    PreferenceManager.saveProperties();
                } else {
                    System.exit(0);
                }
            }

        });

        namesList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(namesList.getSelectedValue()!=null){
                    String name = namesList.getSelectedValue().toString();
                    XmlDataStore properties = null;
                    if(viewingSystemProperties){
                        properties = PreferenceManager.getSystemPropertyGroup(name);
                        table.setProperties(properties);
                    } else {
                        properties = PreferenceManager.getEditablePropertyGroup(name);
                        table.setProperties(properties);
                    }
                    if(properties!=null){
                        propertiesLabel.setText(name);
                    } else {
                        propertiesLabel.setText("Nothing Selected");
                    }
                }
            }
        });
        splitter.setDividerLocation(200);
        showProperties();
    }

    /** Display the editable properties */
    public void showEditableProperties(){
        viewingSystemProperties = false;
        listLabel.setText("Editable Properties");
        namesList.setModel(editableGroupNames);
    }

    /** Disable the load button */
    public void disableLoadButton(){
        loadButton.setEnabled(false);
    }

    /** Disable the system button */
    public void disableSystemButton(){
        systemButton.setEnabled(false);
    }

    /** Set the auto save flag */
    public void setAutoSave(boolean autoSave){
        this.autoSave = autoSave;
    }
    
    /** Show the properties */
    private void showProperties(){
        ArrayList<String> names = PreferenceManager.getSystemPropertyGroupNames();
        systemGroupNames.clear();
        for(int i=0;i<names.size();i++){
            systemGroupNames.addElement(names.get(i));
        }

        names = PreferenceManager.getEditablePropertyGroupNames();
        editableGroupNames.clear();
        for(int i=0;i<names.size();i++){
            editableGroupNames.addElement(names.get(i));
        }
        listLabel.setText("System Properties");
        namesList.setModel(systemGroupNames);
    }

    /** Choose a file to edit */
    private void editFile(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileHidingEnabled(false);
        chooser.showOpenDialog(this);
        if(chooser.getSelectedFile()!=null){
            PreferenceManager.loadPropertiesFromFile(chooser.getSelectedFile());
            showProperties();
        }
    }

    public static void main(String[] args) {
        if(args.length==1){
            String fileName = args[0];
            PreferenceManager.loadPropertiesFromFile(new File(fileName));
            PreferenceManagerEditor editor = new PreferenceManagerEditor();
            editor.setVisible(true);
        } else {
            PreferenceManagerEditor editor = new PreferenceManagerEditor();
            editor.setVisible(true);
            editor.editFile();
        }
    }
}
