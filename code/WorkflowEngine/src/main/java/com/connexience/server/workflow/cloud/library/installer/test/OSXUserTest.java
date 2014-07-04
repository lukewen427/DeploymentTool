/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connexience.server.workflow.cloud.library.installer.test;

import com.connexience.server.workflow.cloud.library.installer.UserManager;
import com.connexience.server.workflow.cloud.library.installer.UserManagerFactory;

/**
 *
 * @author hugo
 */
public class OSXUserTest {
    public static void main(String[] args){
        try {
            UserManager mgr = UserManagerFactory.newInstance();
            System.out.println("Checking user: wfuser1 " + mgr.userExists("wfuser1"));
            if(!mgr.userExists("wfuser1")){
                mgr.createUser("wfuser1", 1001, "wfusers", 400);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
