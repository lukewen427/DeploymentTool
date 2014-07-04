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
package com.connexience.server.workflow.rpc;

/**
 * This interface defines a listener to the RPCClient that can be notified
 * about data transfers.
 * @author hugo
 */
public interface RPCClientListener {
    /** Some data has been received */
    public void dataReceived(int bytesReceived);

    /** Transfer started. This is called when there is at least one all object waiting */
    public void transferStarted();

    /** Transfer finished. This is called when there are no call objects waiting */
    public void transferFinished();
}