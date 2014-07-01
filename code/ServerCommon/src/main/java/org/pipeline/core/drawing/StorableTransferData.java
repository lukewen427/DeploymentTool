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
package org.pipeline.core.drawing;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface defines a storable piece of transfer data
 * that can be saved or loaded from InputStreams and OutputStreams.
 * @author hugo
 */
public interface StorableTransferData {
    /** Store the transfer data contents to an OutputStream */
    public void saveToOutputStream(OutputStream stream) throws DrawingException;
    
    /** Load the transfer data contents from an InputStream */
    public void loadFromInputStream(InputStream stream) throws DrawingException;
}