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
package org.pipeline.gui.data.table;

import org.pipeline.core.data.Data;
import org.pipeline.core.data.io.DelimitedTextDataImporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
/**
 *
 * @author hugo
 */
public class ColoredDataTableTest extends JFrame {

    public ColoredDataTableTest() {
        try {
            setLayout(new BorderLayout());
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            DelimitedTextDataImporter importer = new DelimitedTextDataImporter();
            importer.setForceTextImport(true);
            Data data = importer.importFile(new File("/Users/hugo/Desktop/weetabix.csv"));
            ColoredDataTable table = new ColoredDataTable();
            getContentPane().add(table, BorderLayout.CENTER);
            table.setData(data);
            setSize(800, 600);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        ColoredDataTableTest test = new ColoredDataTableTest();
        test.setVisible(true);
    }

}
