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
package com.connexience.server.workflow.service;

/**
 * This exception is thrown by the data processor calls
 * @author hugo
 */
public class DataProcessorException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new instance of <code>DataProcessorException</code> without detail message.
     */
    public DataProcessorException() {
    }


    /**
     * Constructs an instance of <code>DataProcessorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DataProcessorException(String msg) {
        super(msg);
    }

    public DataProcessorException(String msg, Throwable cause){
        super(msg, cause);
    }
}
