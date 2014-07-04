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
package com.connexience.server.workflow.cloud.library;

/**
 * This interface defines a class that can act as a callback to the service
 * library. Typically, when a service is invoked, the library prepares the
 * service deployment directory with the latest version of the service
 * code. This may be instantaneous if the service has already been deployed
 * or may happen at some point in the future. This callback is used to notify
 * that a library is ready for use and that a service can be called.
 * @author nhgh
 */
public interface LibraryCallback {
    /** A library and its dependencies are ready for use */
    public void libraryReady(CloudWorkflowServiceLibraryItem library, LibraryPreparationReport report);

    /** Preparation of a library has failed */
    public void libraryPreparationFailed(String message, LibraryPreparationReport report);
}