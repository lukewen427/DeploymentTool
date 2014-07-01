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
package com.connexience.server.model.workflow;

import javax.persistence.*;
import java.io.Serializable;


/**
 * This class contains the output from the service process after it has finished
 * executing. This is updated for each service invocation during a workflow run
 * so that intermediate results can be viewed in the editor.
 * @author nhgh
 */
@Entity
@Table(name = "workflowservicelogs")
public class WorkflowServiceLog implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final org.jboss.logging.Logger Logger = org.jboss.logging.Logger.getLogger(WorkflowServiceLog.class.getName());
	
	private static final int DefaultMaxStringLength = 255;
	private static final int DefaultMaxLobLength = 65535;
	private static final int MaxStatusMessageLength = 1023;

	/** Service has not been executed yet */
    public static final String SERVICE_NOT_EXECUTED_YET = "notexecuted";
    
    /** Service executed Ok */
    public static final String SERVICE_EXECUTION_OK = "ok";
    
    /** Service executed with an error */
    public static final String SERVICE_EXECUTION_ERROR = "error";
    
    /** Log message ID */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    /** Workflow invocation ID */
    @Basic
    private String invocationId;

    /** Block context id */
    @Basic
    private String contextId;

    /** Message data */
    @Lob
    private String outputText;

    /** Status text */
    @Basic
    private String statusText = SERVICE_NOT_EXECUTED_YET;
    
    /** Status message */
    @Column(length = MaxStatusMessageLength)
    private String statusMessage = "";


    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvocationId() {
        return invocationId;
    }

    public void setInvocationId(String invocationId) {
        this.invocationId = invocationId;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = trimIfTooLong(outputText, DefaultMaxLobLength);
    }

    public void setStatusText(String statusText) {
        this.statusText = trimIfTooLong(statusText, DefaultMaxStringLength);
    }

    public String getStatusText() {
        return statusText;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = trimIfTooLong(statusMessage, MaxStatusMessageLength);
    }


    private static String trimIfTooLong(String inputText, int maxLength)
    {
    	if (inputText.length() <= maxLength)
    		return inputText;
    	
    	String startMsg = "Text too long";
    	StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    	if (trace != null && trace.length >= 3) {
    		startMsg += " when calling: " + trace[2].getMethodName();
    	}

    	Logger.warnf(startMsg + "; truncated to %s characters.\nFull text: '%s'", maxLength - 3, inputText);
    	return inputText.substring(0, maxLength - 3) + "...";
    }
}