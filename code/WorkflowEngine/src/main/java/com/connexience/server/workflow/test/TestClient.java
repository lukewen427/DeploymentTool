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

import org.codehaus.jackson.map.ObjectMapper;
import com.connexience.server.model.security.*;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 *
 * @author hugo
 */
public class TestClient {
    public static void main(String[] args){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enableDefaultTyping();
            
            // Login
            ClientRequest cs1 = new ClientRequest("http://localhost:8080/workflow/rest/wf/login");
            cs1.accept("application/json");
            
            cs1.formParameter("username", "h.g.hiden@ncl.ac.uk");
            cs1.formParameter("password", "V1an1W");
            ClientResponse<Ticket> resp = cs1.post(Ticket.class);
            
            
            Ticket t = resp.getEntity();
            System.out.println("UserID: " + t.getUserId());
            
            // Get a user
            ClientRequest cs2 = new ClientRequest("http://localhost:8080/workflow/rest/wf/users/{id}");
            cs2.pathParameter("id", t.getUserId());
            //cs2.body(MediaType.APPLICATION_JSON, mapper.writeValueAsString(t));
            cs2.formParameter("ticket", mapper.writeValueAsString(t));
            cs2.accept("application/json");
            ClientResponse<User> resp2 = cs2.post(User.class);
            
            System.out.println(resp2.getEntity().getName());

            //System.out.println("User name: " + resp2.getEntity().getName());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    

}
