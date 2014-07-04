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
package com.connexience.server.workflow.test;

import com.connexience.server.workflow.rpc.*;
import org.pipeline.core.xmlstorage.prefs.*;
import java.io.*;

/**
 *
 * @author hugo
 */
public class ServiceTest {
    public static void main(String[] args){
        try {
            // Load public and private keys
            if(!PreferenceManager.loadKeystoreFromHomeDir(".inkspot", "ServiceHost.keystore")) {
                System.out.println("Need to fetch the keystore from the server first");
                System.exit(1);
            }

            RPCClient client = new RPCClient("http://localhost:8080/WorkflowServer/WorkflowServlet");
            client.setSecurityMethod(RPCClient.PRIVATE_KEY_SECURITY);
            client.setPrivateKey(PreferenceManager.getPrivateKey());
            client.setSigningUserId(PreferenceManager.getCertificateOwnerId());

            CallObject call = new CallObject("SVCFindRunningService");
            call.getCallArguments().add("ServiceName", "DataService");

            client.syncCall(call);
            if(call.getStatus()==CallObject.CALL_EXECUTED_OK){
                PrintWriter writer = new PrintWriter(System.out);
                call.getReturnArguments().debugPrint(writer, 5);
                writer.flush();
                writer.close();
            } else {
                System.out.println("Error: " + call.getStatusMessage());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
