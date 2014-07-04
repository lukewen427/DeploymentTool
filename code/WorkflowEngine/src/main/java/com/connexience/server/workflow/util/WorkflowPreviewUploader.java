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
package com.connexience.server.workflow.util;

import com.connexience.server.*;
import com.connexience.server.model.workflow.*;
import com.connexience.server.model.security.*;
import com.connexience.server.model.document.*;
import com.connexience.server.model.image.*;
import com.connexience.server.util.StorageUtils;
import com.connexience.server.ejb.util.*;
import org.pipeline.core.xmlstorage.*;
import org.pipeline.core.xmlstorage.io.*;
import org.pipeline.core.drawing.gui.*;

import java.io.*;
import java.awt.Graphics;
import java.awt.image.*;
import javax.imageio.*;
import org.pipeline.core.drawing.model.DefaultDrawingModel;
/**
 * This class creates a preview image for a workflow document.
 * @author hugo
 */
public class WorkflowPreviewUploader {
    /** ID of the workflow document */
    private String documentId = null;

    /** Version ID of the workflow document */
    private String versionId = null;

    /** Security ticket */
    private Ticket ticket = null;

    /** Image width */
    private int width = 640;

    /** Image height */
    private int height = 480;

    public WorkflowPreviewUploader(Ticket ticket, String documentId) {
        this.documentId = documentId;
        this.ticket = ticket;
    }

    public WorkflowPreviewUploader(Ticket ticket, String documentId, String versionId) {
        this.documentId = documentId;
        this.versionId = versionId;
        this.ticket = ticket;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void generatePreview() throws ConnexienceException {
        // Get the document
        WorkflowDocument doc = WorkflowEJBLocator.lookupWorkflowManagementBean().getWorkflowDocument(ticket, documentId);
        if(doc==null){
            throw new ConnexienceException("Workflow does not exist");
        }

        DocumentVersion version = null;
        if(versionId!=null){
            version = EJBLocator.lookupStorageBean().getVersion(ticket, documentId, versionId);
        } else {
            version = EJBLocator.lookupStorageBean().getLatestVersion(ticket, documentId);
        }

        if(version==null){
            throw new ConnexienceException("Cannot find specified version");
        }

        XmlDataStore workflowData = null;
        DefaultDrawingModel drawing = null;
        InputStream stream = null;
        try {
            stream = StorageUtils.getInputStream(ticket, doc, version);
            XmlDataStoreStreamReader reader = new XmlDataStoreStreamReader(stream);
            workflowData = reader.read();
            drawing = new DefaultDrawingModel();
            drawing.recreateObject(workflowData);

        } catch (Exception e){
            throw new ConnexienceException("Error loading workflow data: " + e.getMessage(), e);
        } finally {
            if(stream!=null){
                try {stream.close();}catch(Exception e){}
            }
        }

        try {

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.createGraphics();
            DefaultDrawingRenderer renderer = new DefaultDrawingRenderer(drawing);
            renderer.renderDrawing(g, width, height);

            ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", imgStream);
            imgStream.flush();
            imgStream.close();
            EJBLocator.lookupObjectDirectoryBean().setImageForServerObject(ticket, doc.getId(), imgStream.toByteArray(), ImageData.WORKFLOW_PREVIEW);
        } catch (Exception e){
            throw new ConnexienceException("Error creating image data: " + e.getMessage(), e);
        }
    }
}