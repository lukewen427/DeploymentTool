/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.connexience.server.workflow.cloud.library.installer.test;
import com.connexience.server.workflow.cloud.library.installer.*;

/**
 *
 * @author hugo
 */
public class OSXGroupTest {
    public static void main(String[] args){
        try {
            UserManager mgr = UserManagerFactory.newInstance();
            System.out.println(mgr.getClass().getName());
            System.out.println("Checking wfusers: " + mgr.groupExists("wfusers"));
            if(!mgr.groupExists("wfusers")){
                mgr.createGroup("wfusers", 400);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
