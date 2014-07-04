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
package com.connexience.server.workflow.cloud;
import org.apache.log4j.*;
/**
 * This thread attaches a workflow engine to the JMS server and reconnects if there
 * are any errors.
 * @author hugo
 */
public class JMSAttachThread implements Runnable {
    static Logger logger = Logger.getLogger(CloudWorkflowEngine.class);
    
    /** Worker thread */
    private Thread worker = null;
    private String hostname;
    private int port;
    private String user;
    private String password;
    private String queueName;
    private Integer bufferSize;
    private volatile int interval;
    private volatile boolean runFlag = false;
    private WorkflowJMSListener target;
    private volatile boolean autoAttach = true;
    
    
    public JMSAttachThread(WorkflowJMSListener target, String hostname, int port, String user, String password, String queueName, Integer bufferSize) {
        this.hostname = hostname;
        this.user = user;
        this.password = password;
        this.port = port;
        this.queueName = queueName;
        this.bufferSize = bufferSize;
        interval = 3600000;
        this.target = target;
        this.user = user;
    }
    
    /** Set the retry interval */
    public void setInterval(int interval){
        this.interval = interval;
        if(worker!=null){
            worker.interrupt();
        }
    }
        
    public void start(){
        if(worker==null){
            runFlag = true;
            interval = 3600000;
            worker = new Thread(this, "JMSAttacherWorker");
            worker.start();
            logger.debug("JMSAttacher thread started: " + queueName);
        }
    }

    public void stop() {
        if(worker!=null){
            logger.debug("JMSAttacherThread stopping: " + queueName);
            runFlag = false;
            worker.interrupt();
            worker = null;
        }
    }    

    public void detach(){
        autoAttach = false;
        if(target.isJmsAttached()){
            target.detachJms();
        }
    }
    

    public void attach(){
        if(!target.isJmsAttached()){
            try {
                target.attachJms(hostname, port, user, password, queueName, bufferSize);
            } catch (Exception e){
                logger.error("Error attaching JMS: " + e.getMessage());
            }
            autoAttach = true;
            start();
        }
    }
    public void run() {
        while(runFlag){
            if(!target.isJmsAttached()){
                try {
                    if(autoAttach){
                        target.attachJms(hostname, port, user, password, queueName, bufferSize);
                    }
                    interval = 3600000; // Check every hour
                } catch (Exception e){
                	logger.error("Error attaching JMS: " + e.getMessage());
                    interval = 10000;   // 10 second checks if disconnected
                }
            }
            
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ie){
            }
        }
        logger.debug("JMSAttachThread worker finishing: " + queueName);
    }
}