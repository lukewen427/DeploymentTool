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
package com.connexience.server.workflow.api;

import java.net.URI;
import java.net.URISyntaxException;

import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.storage.DataStore;
import com.connexience.server.workflow.api.impl.JMSAPIHelper;
import com.connexience.server.workflow.api.impl.RESTClient;
import com.connexience.server.workflow.api.impl.RMIClient;
import com.connexience.server.workflow.api.impl.RMIWorkflowManager;
import com.connexience.server.workflow.engine.WorkflowEngine;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;


/**
 * This class generates API objects to support the cloud workflow engine
 * @author nhgh
 */
public class ApiProvider {
    private static Logger logger = Logger.getLogger(ApiProvider.class);
    private static final String _DefaultServerContext = "/workflow";
    private boolean useRmi = true;
    private boolean rmiError = false;
    private int rmiRegistryPort = 2199;
    private boolean useJMS = true;
    private JMSAPIHelper jmsHelper = new JMSAPIHelper(this);
    private WorkflowEngine engine;
    
    /** URL of the download servlet */
    private String serverContext = _DefaultServerContext;

    /** Web host name */
    private String hostName = "localhost";
    
    /** Webserver port of the host */
    private int httpPort = 8080;
    
    /** Organisation data store */
    private DataStore dataStore = null;
    
    public ApiProvider() {
    }

    private RMIWorkflowManager lookupRMIWorkflowManger(){
        if(useRmi){
            try {
                Registry registry = LocateRegistry.getRegistry(hostName, rmiRegistryPort);
                RMIWorkflowManager mgr = (RMIWorkflowManager)registry.lookup("RMIWorkflowManager");
                rmiError = false;
                return mgr;
            } catch (Exception e){
                logger.error("Cannot create remote API Provider. Falling back to REST: " + e.getMessage());
                rmiError = true;
                return null;
            }
        } else {
            return null;
        }
    }

    public WorkflowEngine getEngine(){
        return engine;
    }
    
    public void setEngine(WorkflowEngine engine){
        this.engine = engine;
    }
    
    public boolean invocationPresent(String invocationId){
        if(engine!=null){
            return engine.invocationPresent(invocationId);
        } else {
            return false;
        }
    }
    
    public JMSAPIHelper getJmsHelper() {
        return jmsHelper;
    }

    public void setUseJMS(boolean useJMS) {
        this.useJMS = useJMS;
    }

    public boolean isUseJMS() {
        return useJMS;
    }

    public void resetRmiStatus(){
        logger.debug("Resetting RMI communications");
        rmiError = false;
    }
    
    public void setUseRmi(boolean useRmi) {
        this.useRmi = useRmi;
        logger.debug("APIProvider uses RMI: " + useRmi);
    }

    public boolean isUseRmi() {
        return useRmi;
    }

    public int getRmiRegistryPort() {
        return rmiRegistryPort;
    }

    public void setRmiRegistryPort(int rmiRegistryPort) {
        this.rmiRegistryPort = rmiRegistryPort;
        logger.debug("APIProvider RMI registry port: " + rmiRegistryPort);
    }
    
    private boolean isRmiUsable(){
        if(useRmi==true && rmiError==false){
            return true;
        } else {
            return false;
        }
    }
    
    private RMIClient createRemoteAPI() throws Exception {
        if(isRmiUsable()){
            RMIWorkflowManager mgr = lookupRMIWorkflowManger();
            if(mgr!=null){
                return new RMIClient(this, null, mgr.createService());
            } else {
                throw new Exception("No Remote Workflow manager available");
            }
            
        } else {
            return null;
        }
    }
    
    private RMIClient createRemoteAPI(Ticket ticket) throws Exception {
        if(isRmiUsable()){
            RMIWorkflowManager mgr = lookupRMIWorkflowManger();
            if(mgr!=null){
                return new RMIClient(this, ticket, mgr.createService(ticket));
            } else {
                throw new Exception("No Remote Workflow manager available");
            }
        } else {
            return null;
        }
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
        logger.debug("APIProvider server host: " + hostName);
    }

    public String getHostName() {
        return hostName;
    }

    public void setServerContext(String serverContext) {
        this.serverContext = serverContext;
        logger.debug("APIProvider REST server context: " + serverContext);
    }

    public String getServerContext() {
        return serverContext;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
        logger.debug("APIProvider HTTP port: " + httpPort);
    }

    public int getHttpPort() {
        return httpPort;
    }
    
    public void setURL(String url) throws URISyntaxException {
        logger.debug("Setting API provider properties using a URL string: " + url);
    	URI uri = new URI(url);
    	
    	setHostName(uri.getHost());

        int port = uri.getPort();
    	if (port == -1) {
            setHttpPort(80);
    	} else {
            setHttpPort(port);
        }

        String context = uri.getPath();
    	if ("".equals(serverContext)) {
            setServerContext(_DefaultServerContext);
    	} else {
            setServerContext(context);
        }
    }

    private void checkDataStore(API api) {
        try {
            if(dataStore==null){
                dataStore = api.loadDataStore();
                logger.debug("Got datastore: " + dataStore.getClass().getName());
            } else {
            	api.setDataStore(dataStore);
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Failed to get datastore: " + e.getMessage(), e);
        }
    }
        
    
    public API createApi() {
        API api;
        if(isRmiUsable()){
            try {
                api = createRemoteAPI();
            } catch (Exception e){
                logger.warn("Switching to REST Client because RMI Client creation failed: " + e.getMessage());
                rmiError = true;
                api = new RESTClient(this);
            }
            
        } else {
            api = new RESTClient(this);
        }
        api.setServerContext(serverContext);
        api.setHttpPort(httpPort);
        api.setHostName(hostName);
        checkDataStore(api);

        return api;
    }

    public API createApi(Ticket ticket) throws Exception {
        API api;
        if(isRmiUsable()){
            try {
                api = createRemoteAPI(ticket);
            } catch (Exception e){
                logger.warn("Switching to REST Client because RMI Client creation failed: " + e.getMessage());
                rmiError = true;
                api = new RESTClient(this);
                api.setTicket(ticket);
            }
                    
        } else {
            api = new RESTClient(this);
            api.setTicket(ticket);
        }
        
        
        api.setServerContext(serverContext);
        api.setHttpPort(httpPort);
        api.setHostName(hostName);
        checkDataStore(api);

        return api;
    }
}