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
package com.connexience.server.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * This class defines an input stream server that can be used to transfer
 * data via RMI
 * @author hugo
 */
public interface IRMIInputStream extends Remote {
    /** Is the source finished */
    public boolean finished() throws RemoteException;

    /** Fetch the next chunk of data */
    public byte[] next() throws RemoteException;

    /** Close the connection and finish with the stream */
    public void close() throws RemoteException;
}