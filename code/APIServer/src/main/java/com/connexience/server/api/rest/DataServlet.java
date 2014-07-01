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
package com.connexience.server.api.rest;

import com.connexience.server.ejb.util.EJBLocator;
import com.connexience.server.ejb.util.WorkflowEJBLocator;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentType;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.util.StorageUtils;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This servlet deals with sending and receiving data from the API
 * @author hugo
 */
public class DataServlet extends HttpServlet
{
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


    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String[] path = splitRequestPath(request.getRequestURI().toString());
        if(path.length>=3){
            String documentId = "";
            String versionId = "";
            if(path.length==8){
                documentId = path[6];
                versionId = path[7];
            } else {
                documentId = path[2];
                if(path.length>=4){
                    versionId = path[3];
                }       
            }

            
            if(request.isUserInRole("StorageAPI") || request.isUserInRole("WebserviceAPI") || request.isUserInRole("Users")){
                try {
                    Ticket ticket = EJBLocator.lookupTicketBean().createWebTicket(request.getUserPrincipal().getName());
                    ServerObject docObject = (DocumentRecord)EJBLocator.lookupObjectDirectoryBean().getServerObject(ticket, documentId, ServerObject.class);
                    if(docObject instanceof DocumentRecord){
                        DocumentRecord doc = (DocumentRecord)docObject;
                        String mimeType;
                        if(doc.getDocumentTypeId()!=null){
                            DocumentType docType = EJBLocator.lookupStorageBean().getDocumentType(ticket, doc.getDocumentTypeId());
                            if(docType!=null){
                                mimeType = docType.getMimeType();
                            } else {
                                mimeType = "application/octet-stream";
                            }
                        } else {
                            mimeType = "application/octet-stream";
                        }
                        response.setContentType(mimeType);
                        response.addHeader("Content-Disposition", "attachment; filename=" + docObject.getName());

                        if(versionId!=null){
                            // Latest version
                            DocumentVersion version;
                            if(versionId.equals("latest")){
                                version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, documentId);
                                response.setContentLength((int)version.getSize());
                                StorageUtils.downloadFileToOutputStream(ticket, doc, version, response.getOutputStream());
                                
                            } else {
                                // Use as version ID
                                version = EJBLocator.lookupStorageBean().getVersion(ticket, documentId, versionId);
                                response.setContentLength((int)version.getSize());
                                StorageUtils.downloadFileToOutputStream(ticket, doc, version, response.getOutputStream());                                     
                                
                            }
                        } else {
                            if(!response.isCommitted()){
                                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No version information found");
                            }                               
                        }
                    } else {
                        if(!response.isCommitted()){
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }                        
                    }
                } catch (Exception e){
                    if(!response.isCommitted()){
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending data: " + e.getMessage());
                    }
                }
                
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            if(!response.isCommitted()){
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No VersionID supplied");
            }
        }
        
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] path = splitRequestPath(request.getRequestURI().toString());
        if(path.length==3 || path.length==7){
            String documentId;
            if(path.length==3){
                documentId = path[2];
            } else {
                documentId = path[6];
            }
            
            
            if(request.isUserInRole("StorageAPI") || request.isUserInRole("WebserviceAPI") || request.isUserInRole("Users")){
                try {
                    Ticket ticket = EJBLocator.lookupTicketBean().createWebTicket(request.getUserPrincipal().getName());
                    ServerObject docObject = (DocumentRecord)EJBLocator.lookupObjectDirectoryBean().getServerObject(ticket, documentId, ServerObject.class);
                    DocumentVersion version = null;
                    UserTransaction tx = null;
                    if(docObject instanceof DocumentRecord){
                        DocumentRecord doc = (DocumentRecord)docObject;
                        try {
                            tx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
                            tx.begin();
                            version = StorageUtils.upload(ticket, request.getInputStream(), doc, "Uploaded by API");

                            //re-get the document so that we can set the project id
                            doc = (DocumentRecord) EJBLocator.lookupObjectDirectoryBean().getServerObject(ticket, doc.getId(), DocumentRecord.class);
                            if(doc.getProjectId()!=null && !doc.getProjectId().isEmpty()){
                                EJBLocator.lookupStorageBean().checkProjectId(ticket, doc, doc.getProjectId());
                                
                            } else if(request.getHeader("ProjectID")!=null && !request.getHeader("ProjectID").isEmpty()){
                                EJBLocator.lookupStorageBean().checkProjectId(ticket, doc, request.getHeader("ProjectID"));
                                
                            }

                            tx.commit();
                        } catch (Exception e){
                            if(tx!=null){
                                tx.rollback();
                            }
                            throw new Exception("Error storing document: " + e.getMessage(), e);
                        }
  
                        PrintWriter writer = response.getWriter();
                        writer.println(version.getId());
                        writer.flush();
                        
                        // Run workflow triggers
                        WorkflowEJBLocator.lookupWorkflowManagementBean().runTriggersForDocument(ticket, doc);
                        
                    } else {
                        if(!response.isCommitted()){
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }                        
                    }
                } catch (Exception e){
                    if(!response.isCommitted()){
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending data: " + e.getMessage());
                    }
                }
                
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            if(!response.isCommitted()){
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No VersionID supplied");
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Data Servlet for the API Storage service";
    }
    
  private String[] splitRequestPath(String path){
      if(path.startsWith("/")){
          // Split leading /
          path = path.substring(1);
      }
      return path.split("/");
  }
    
}
