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
package com.connexience.server.workflow.cloud.test;

import com.connexience.server.model.folder.Folder;
import com.connexience.server.workflow.api.*;
import com.connexience.server.workflow.api.ApiProvider;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;


/**
 *
 * @author hugo
 */
public class NewApiTest {
    public static void main(String[] args){
        try {
            ApiProvider provider = new ApiProvider();
            API api = provider.createApi();
            api.authenticate("h.g.hiden@ncl.ac.uk", "V1an1W");
            Folder f = api.getHomeFolder(api.getTicket().getUserId());
            System.out.println("Home folder: " + f.getName());
            DataProcessorServiceDefinition def = api.getService("blocks-core-io-csvimport");
            System.out.println(def.getName());            
            api.notifyEngineStartupAsync("IP:10.0.0.1");
            api.logWorkflowDequeuedAsync("4028808a38fc5dcd0138fc8121e00007");
            api.logWorkflowDequeuedAsync("4028808a38fc5dcd0138fc8121e00007");
            /*
            DocumentRecord testDoc = new DocumentRecord();
            testDoc.setName("pgadmin.log");
            testDoc = api.saveDocument(f, testDoc);
            
            FileInputStream inStream = new FileInputStream("/Users/hugo/pgadmin.log");
            DocumentVersion v = api.upload(testDoc, inStream);
            System.out.println(v.getSize());
*/
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
