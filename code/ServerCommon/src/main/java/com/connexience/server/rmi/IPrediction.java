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
 * User: nsjw7
 * Date: Mar 15, 2011
 * Time: 2:18:55 PM
 * This interface represents how clients can query provenance data
 */
public interface IPrediction extends Remote
{
    public Double getPrediction(String blockUUID, int inputSize) throws RemoteException, Exception;
}
