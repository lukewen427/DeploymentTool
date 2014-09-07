package uk.ac.ncl.cs.esc.cloudMonitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class testCloudM {
	public static void main(String args[]) throws InterruptedException{
		
		Set<Cloud> cloudSet=new HashSet<Cloud>();
		String cloud1="cloud1";
		String cloud2="cloud2";
		String cloud3="cloud3";
		String cloud1ip="1";
		String cloud2ip="2";
		String cloud3ip="3";
		
		Cloud c1=new Cloud(cloud1,cloud1ip);
		Cloud c2=new Cloud(cloud2,cloud2ip);
		Cloud c3=new Cloud(cloud3,cloud3ip);
		cloudSet.add(c1);
		cloudSet.add(c2);
		cloudSet.add(c3);
		new CloudPool(cloudSet);
		writeThread p=new writeThread();
		p.start();
		
		cloudMonitorIm cm=new cloudMonitorIm();
		cm.initAvaClouds(cloudSet);
		System.out.println("inital clouds:"+cm.getAvaClouds());
		LinkedList<String> temp=(LinkedList<String>) cm.getAvaClouds().clone();
		Thread.sleep(1000);
		System.out.println("is clouds statues change:"+cm.statuesChange(temp));
		
		Thread.sleep(5000);
		System.out.println("is clouds statues change:"+cm.statuesChange(temp));
		System.out.println("available clouds:"+cm.getAvaClouds());
		LinkedList<String> temp2=(LinkedList<String>) cm.getAvaClouds().clone();
		Thread.sleep(6000);
	
		
		System.out.println("is clouds statues change:"+cm.statuesChange(temp2));
		System.out.println("available clouds:"+cm.getAvaClouds());
	
	
	}
}
