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

import com.connexience.server.ejb.util.EJBLocator;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;

import com.connexience.server.model.security.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.security.cert.X509Certificate;

/**
 * This servlet provides a server side handler for the workflow RPC library.
 * @author hugo
 */
public class RPCServlet extends HttpServlet implements CallHandler {
	private static final long serialVersionUID = 1L;

	/** Handler class */
    private Vector<CallHandler> rpcHandlers = new Vector<>();

    /** Cache of methods */
    private Hashtable<String,MethodHolder> methodCache = new Hashtable<>();
    

    /** Provenance Data ThreadLocal */
    
    private static ThreadLocal<XmlDataStore> provenance = new ThreadLocal<XmlDataStore>(){
        protected synchronized XmlDataStore initialValue(){
            return null;
        }
    };
     

    /** Construct without handlers */
    public RPCServlet() {
        super();
        addHandler(this);
    }

 

  
    /** Set the provenance data object */
    public void setProvenanceObject(XmlDataStore provenanceData){
        provenance.set(provenanceData);
    }

    /** Get the provenance data if it exists */
    public XmlDataStore getProvenanceObject(){
        return provenance.get();
    }

    /** Is there any provenance data present */
    public boolean containsProvenance(){
        if(provenance.get()!=null){
            return true;
        } else {
            return false;
        }
    }

    /** Remove the provenance object */
    public void clearProvenance(){
        provenance.remove();
    }
    
    /** Add a CallHandler */
    public void addHandler(CallHandler handler){
        rpcHandlers.add(handler);
    }
    
    /** Find a method in the handlers */
    private MethodHolder findMethod(String methodName) throws NoSuchMethodException {
        if(methodCache.containsKey(methodName)){
            
        } else {
            Iterator <CallHandler> i = rpcHandlers.iterator();
            Class<?> c;
            CallHandler handler;
            Method m;

            while(i.hasNext()){
                handler = i.next();
                c = handler.getClass();

                try {
                    m = c.getMethod(methodName, new Class<?>[]{CallObject.class});    
                    return new MethodHolder(m, handler);
                } catch (NoSuchMethodException nsme){
                    // Do nothing here
                }
            }
        }

        // Nothing found
        throw new NoSuchMethodException();
    }
    
    /** 
    * Handles the HTTP <code>GET</code> method. This method just returns some information
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>RPC Servlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>RPCServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally { 
            out.close();
        }
    } 

    /** 
    * Handles the HTTP <code>POST</code> method. This method recreates the RPC CallObject
    * and uses reflection to find the correct handler method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream stream = response.getOutputStream();


        boolean newStyleCall = false;
        Ticket sessionTicket = null;
        
        /** Put in security data from the session if it exists */
        HttpSession session = null;
        if(request.getSession()!=null){
            session = request.getSession();

            // Setup the ticket
            if(session.getAttribute("TICKET")!=null){
                sessionTicket = (Ticket)session.getAttribute("TICKET");
            }
        } else {
            // Create a new session
            session= request.getSession(true);
        }
        
        CallObject call = null;
        String method = request.getHeader("format");
        boolean binary;
        if(method==null || method.equalsIgnoreCase("xml")){
            binary = false;
        } else {
            binary = true;
        }
            
        try {

            
            XmlDataStore store = null;
            if(binary){
                ObjectInputStream reader = new ObjectInputStream(request.getInputStream());
                Object data = reader.readObject();
                if(data instanceof CallObject){
                    call = (CallObject)data;
                    newStyleCall = true;
                } else {
                    store = (XmlDataStore)reader.readObject();
                    call = new CallObject();
                    call.recreateObject(store);
                    newStyleCall = false;
                }
                    
            } else {
                XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(request.getInputStream());
                store = reader.read();
                if(store.containsName("Call")){
                    // New style call
                    call = (CallObject)store.xmlStorableValue("Call");
                    newStyleCall = true;
                } else {
                    // Old style call
                    call = new CallObject();
                    call.recreateObject(store);
                    newStyleCall = false;
                }
            }
            
            // Work out if this is a new style call that can contain a
            // signature

            // Validate the signature if the call is signed
            boolean callValid;

            if(newStyleCall && call.getSignatureData().objectSigned()){
                String signerId = call.getSignatureData().getSigningUserId();
                X509Certificate cert = EJBLocator.lookupCertificateBean().getUserCertificate(signerId);
                call.getSignatureData().setCertificate(cert);
                if(call.getSignatureData().verifyObject()){
                    callValid = true;

                    // Create a ticket
                    Ticket t = EJBLocator.lookupTicketBean().createWebTicketForDatabaseId(signerId);
                    call.setTicket(t);
                } else {
                    callValid = false;
                }

            } else if(call.getTicket()!=null){
                // There is a ticket in the call
                callValid = true;
                
            } else {
                call.setTicket(sessionTicket);  // Ticket from the session
                callValid = true;
            }

            // Store the provenance data
            call.setRequestObject(request);
            if(session!=null){
                call.setSessionObject(session);
                if(call.hasProvenanceProperties()){
                    setProvenanceObject(call.getProvenanceProperties());
                    session.setAttribute("PROVENANCE", call.getProvenanceProperties());
                } else {
                    setProvenanceObject(null);
                    session.setAttribute("PROVENANCE", null);
                }
            } else {
                call.setSessionObject(null);
            }
            
            // Invoke the correct handler method
            try {
                String methodName = call.getMethodName();
                MethodHolder m = findMethod(methodName);
                
                if(m!=null && callValid){
                    m.invoke(call);
                    call.clearCallArguments();
                    
                    // Send back response in the correct format
                    if(newStyleCall){
                        XmlDataStore returnData = new XmlDataStore("CallWrapper");
                        returnData.add("Call", call);
                        
                        // Send back in correct format
                        if(binary){
                            ObjectOutputStream writer = new ObjectOutputStream(stream);
                            writer.writeObject(returnData);
                            writer.flush();
                            writer.close();
                        } else {
                            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(returnData);
                            writer.setDescriptionIncluded(true);    // Include description text
                            writer.write(stream);
                        }
                        stream.flush();
                        
                    } else {
                        if(binary){
                            ObjectOutputStream writer = new ObjectOutputStream(stream);
                            writer.writeObject(call.storeObject());
                            writer.flush();
                            writer.close();
                        } else {
                            XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(call.storeObject());
                            writer.setDescriptionIncluded(true);
                            writer.write(stream);
                        }
                        stream.flush();
                    }
                }
            } catch (NoSuchMethodException nsme){
                call.clearCallArguments();
                call.setStatus(CallObject.CALL_EXECUTED_WITH_ERROR);
                call.setStatusMessage("No such method");

                // Send back in the same format
                if(newStyleCall){
                    XmlDataStore returnData = new XmlDataStore("CallWrapper");
                    returnData.add("Call", call);
                    
                    if(binary){
                        ObjectOutputStream writer = new ObjectOutputStream(stream);
                        writer.writeObject(returnData);
                        writer.flush();
                        writer.close();
                    } else {
                        XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(returnData);
                        writer.setDescriptionIncluded(true);        // Include description text
                        writer.write(stream);
                    }
                    stream.flush();
                } else {
                    if(binary){
                        ObjectOutputStream writer = new ObjectOutputStream(stream);
                        writer.writeObject(call.storeObject());
                        writer.flush();
                        writer.close();
                    } else {                            
                        XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(call.storeObject());
                        writer.setDescriptionIncluded(true);
                        writer.write(stream);
                    }
                    stream.flush();
                }
            }
            
        } catch (Exception e){
            // Write an error response back
            call.clearCallArguments();
            call.setStatus(CallObject.CALL_EXECUTED_WITH_ERROR);
            if(e.getCause()!=null){
                call.setStatusMessage(e.getCause().getMessage());
            } else {
                call.setStatusMessage(e.getMessage());
            }
            
            // Send back in the same format
            try {
                if(newStyleCall){
                    XmlDataStore returnData = new XmlDataStore("CallWrapper");
                    returnData.add("Call", call);

                    if(binary){
                        ObjectOutputStream writer = new ObjectOutputStream(stream);
                        writer.writeObject(returnData);
                        writer.flush();
                        writer.close();
                    } else {
                        XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(returnData);
                        writer.setDescriptionIncluded(true);        // Include description text
                        writer.write(stream);
                    }
                    stream.flush();
                } else {
                    if(binary){
                        ObjectOutputStream writer = new ObjectOutputStream(stream);
                        writer.writeObject(call.storeObject());
                        writer.flush();
                        writer.close();
                    } else {                            
                        XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(call.storeObject());
                        writer.setDescriptionIncluded(true);
                        writer.write(stream);
                    }
                    stream.flush();
                }            
            } catch (XmlStorageException xmlse){
                throw new ServletException("Error processing data and could not send back response");
            }
            
        } finally {
            clearProvenance();
            stream.close();
        }

    }

    /** 
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Workflow RPC handler servlet";
    }

    /** Holder for method and object used for invocations */
    private class MethodHolder {
        /** Method to execute */
        private Method method;
        
        /** Object to execute method on */
        private CallHandler handler;
        
        /** Construct with method and handler */
        public MethodHolder(Method method, CallHandler handler){
            this.method = method;
            this.handler = handler;
        }
        
        /** Execute the method */
        public void invoke(CallObject call){
            try {
                call.setStatus(CallObject.CALL_EXECUTED_OK);
                call.setStatusMessage("");
                method.invoke(handler, new Object[]{call});
            } catch (Exception e){
                if(e.getCause()!=null){
                    call.setStatusMessage(e.getCause().getMessage());
                } else {
                    call.setStatusMessage(e.getMessage());
                }
                call.setStatus(CallObject.CALL_EXECUTED_WITH_ERROR);
            }            
        }
    }
}
