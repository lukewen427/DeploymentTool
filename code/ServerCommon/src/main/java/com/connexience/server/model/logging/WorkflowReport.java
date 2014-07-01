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
package com.connexience.server.model.logging;

import java.io.Serializable;
import java.util.Date;

public class WorkflowReport implements Serializable {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 1L;


    private String username = "";

    private String userId = "";

    private int numWorkflowsRun = 0;

    private long totalExectionTime = 0;

    private long totalDataStored = 0;

    public WorkflowReport() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumWorkflowsRun() {
        return numWorkflowsRun;
    }

    public void setNumWorkflowsRun(int numWorkflowsRun) {
        this.numWorkflowsRun = numWorkflowsRun;
    }

    public long getTotalExectionTime() {
        return totalExectionTime;
    }

    public void setTotalExectionTime(long totalExectionTime) {
        this.totalExectionTime = totalExectionTime;
    }

    public int addWorkflowRun() {
        numWorkflowsRun++;
        return numWorkflowsRun;
    }


    public long addWorkflowDuration(long duration) {
        totalExectionTime += duration;
        return totalExectionTime;
    }

    public long getTotalDataStored() {
        return totalDataStored;
    }

    public void setTotalDataStored(long totalDataStored) {
        this.totalDataStored = totalDataStored;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {


        return "WorkflowReport{" +
                "username='" + username + '\'' +
                ", numWorkflowsRun=" + numWorkflowsRun +
                ", totalExectionTime=" + new Date(totalExectionTime) +
                '}';
    }
}
