/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.connexience.server.workflow.test;

import com.connexience.server.util.SerializationUtils;
import com.connexience.server.workflow.util.XmlSerializationUtils;
import java.lang.reflect.Method;
import java.util.Vector;
import org.pipeline.core.xmlstorage.XmlDataStore;

/**
 *
 * @author hugo
 */
public class JMSTest {
    public double testMethod(String string1, double d1){
        return (int)Double.parseDouble(string1) + d1;
    }
    
    public static void main(String[] args){
        try {
            Class c = JMSTest.class;
            Object o = c.newInstance();
            
            XmlDataStore s = new XmlDataStore();
            s.add("string1", "15.366");
            s.add("d1", 35.1);
            s.add("aaa", true);
            s.add("bbbb", false);
            
            byte[] data = SerializationUtils.serialize(s);
            XmlDataStore s2 = (XmlDataStore)SerializationUtils.deserialize(data);
            
            Vector v = s2.getNames();
            for(int i=0;i<v.size();i++){
                System.out.println(v.get(i));
            }
            Method[] m = c.getMethods();
            for(int i=0;i<m.length;i++){
                System.out.println(m[i].getName());
            }
        } catch (Exception e){
            
        }
    }
}
