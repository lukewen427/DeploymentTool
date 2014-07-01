/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pipeline.core.xmlstorage.prefs;

import java.net.URL;

/**
 *
 * @author hugo
 */
public class RemotePrefTest {
    public static void main(String[] args){
        try {
            String myIp = PreferenceManager.getIpAddressFromConfigServer("localhost", 8080);
            System.out.println("Detected IP adrdress: " + myIp);
            boolean ok = PreferenceManager.loadPropertiesFromConfigServer(new URL("http://localhost:8080/workflow/config"), "engine", myIp, "engine.xml");
            System.out.println("Load ok: " + ok);
            if(ok){
                PreferenceManager.saveProperties();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
