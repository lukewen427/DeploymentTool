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

import com.connexience.server.ConnexienceException;

import javax.swing.*;
import java.awt.*;
/**
 * This class provides a simplified way of displaying an error dialog
 * for a connexience exception.
 * @author hugo
 */
public abstract class ConnexienceExceptionDialog {
    public static void showDialog(Component parent, ConnexienceException e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Connexience Error", JOptionPane.ERROR_MESSAGE);
    }
}
