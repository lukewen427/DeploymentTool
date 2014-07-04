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
import com.connexience.server.workflow.api.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.log4j.*;
/**
 * This class manages the fetching of dependencies for a cloud workflow item.
 * @author nhgh
 */
public class DependencyFetcher {
    Logger logger = Logger.getLogger(DependencyFetcher.class);
    
    /** Item having dependencies fetched for it */
    private CloudWorkflowServiceLibraryItem item;

    /** List of dependencies for the item */
    private CopyOnWriteArrayList<CloudWorkflowItemDependency> dependencies = new CopyOnWriteArrayList<>();

    /** Resolved dependencies */
    private CopyOnWriteArrayList<CloudWorkflowServiceLibraryItem> resolvedDependencies = new CopyOnWriteArrayList<>();

    /** Current position in dependency list */
    private int currentIndex = 0;

    /** Has the resolution finished */
    private boolean resolutionFinished = false;

    /** Have all of the dependencies been fetched ok */
    private boolean resolutionOk = false;

    /** First item that caused the fetching to fail */
    private CloudWorkflowItemDependency failedItem = null;

    /** Service library containing dependencies */
    private ServiceLibrary library;

    /** Callback object */
    private LibraryCallback callback;

    /** API Link to fetch data on */
    private API apiLink;

    /** Download report containing the status of individual items */
    private LibraryPreparationReport report;

    public DependencyFetcher(CloudWorkflowServiceLibraryItem item, ServiceLibrary library, API apiLink, LibraryPreparationReport report) {
        this.item = item;
        this.library = library;
        this.apiLink = apiLink;
        this.report = report;
                
        // List the dependencies
        Iterator<CloudWorkflowItemDependency> i = item.dependencies();
        CloudWorkflowItemDependency dep;
        while(i.hasNext()){
            dep = i.next();
            if(!dep.getLibraryName().equals(item.getLibraryName())){
                dependencies.add(dep);
            } else {
                report.addMessage(LibraryPreparationReport.INFORMATION_MESSAGE, "Removing dependency: " + dep.getLibraryName());
            }
        }
    }

    public void fetchDependencies(final LibraryCallback callback){
        this.callback = callback;
        fetchNext();
    }

    public boolean isResolutionOk() {
        return resolutionOk;
    }

    public boolean isResolutionFinished() {
        return resolutionFinished;
    }


    public CloudWorkflowItemDependency getFailedItem() {
        return failedItem;
    }

    /** Fetch the next dependency from the library */
    public void fetchNext(){
        if(!isResolutionFinished()){


            CloudWorkflowItemDependency dep = dependencies.get(currentIndex);
            LibraryCallback cb = new LibraryCallback() {

                public void libraryReady(CloudWorkflowServiceLibraryItem library, LibraryPreparationReport report) {
                    logger.debug("Fetched dependency: " + library.getLibraryName() + " InvocationId=" + report.getInvocationId());
                    currentIndex++;
                    resolvedDependencies.add(library);
                    if(currentIndex<dependencies.size()){
                        fetchNext();
                    } else {
                        resolutionFinished = true;
                        resolutionOk = true;
                        item.setResolvedDependencies(resolvedDependencies);
                        callback.libraryReady(item, report);
                    }                    
                }

                public void libraryPreparationFailed(String message, LibraryPreparationReport report) {
                    logger.debug("Dependency fetch failed: " + message + " for InvocationID=" + report.getInvocationId());
                    resolutionFinished = true;
                    resolutionOk = false;
                    callback.libraryPreparationFailed(message, report);
                }

            };

            // Check to see if this dependency is being downloaded in a development
            // engine.
            boolean needsFetch = true;

            if(library.isDevelopmentEngine() && dep.isRuntimeOnlyDependency()){
                needsFetch = false;
            }

            if(needsFetch){
                logger.debug("Fetching dependency: " + dep.getLibraryName() + " InvocationID=" + report.getInvocationId());
                if(dep.isLatestVersion()){
                    library.prepareDependency(apiLink, dep.getLibraryName(), cb, report, true);
                } else {
                    library.prepareDependency(apiLink, dep.getLibraryName(), dep.getVersionNumber(), cb, report, true);
                }
            } else {
                // Pass library
                report.addMessage(LibraryPreparationReport.DOWNLOAD_MANAGER_MESSAGE, "Not downloading runtime only dependency: " + dep.getLibraryName());
                cb.libraryReady(item, report);
            }
            
        } else {
            callback.libraryReady(item, report);
        }
    }
}
