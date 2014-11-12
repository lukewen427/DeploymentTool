package uk.ac.ncl.cs.esc.reliable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool;
import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.cloudMonitor.writeThread;
import uk.ac.ncl.cs.esc.read.Cloud;

public class newworkflow {
	static HashMap<String,ArrayList<String>> blockInfo=new HashMap<String,ArrayList<String>>();
	static ArrayList<ArrayList<String>> connections=new ArrayList<ArrayList<String>>();
	 /*
	 * block: security, data size, execution time,
	 * */
  	static Set<Cloud> cloudSet=new HashSet<Cloud>();
  	static String workflowId="1191";
	public newworkflow(){
		blockInfo();
		CommuInfo();
		CloudInfo();
	}
	void blockInfo(){
		ArrayList<String> block1= new ArrayList<String>();
		// Location
		block1.add("0");
		// Clearance
		block1.add("1");
		block1.add("Service");
		// Execution time with hour unit
		block1.add("1");
		
		ArrayList block2= new ArrayList();
		// Location
		block2.add("0");
		// Clearance
		block2.add("1");
		block2.add("Service");
		// Execution time with hour unit
		block2.add("1.5");
		
		ArrayList block3= new ArrayList();
		// Location
		block3.add("0");
		// Clearance
		block3.add("1");
		block3.add("Service");
		// Execution time with hour unit
		block3.add("3");
		
		ArrayList block4= new ArrayList();
		// Location
		block4.add("0");
		// Clearance
		block4.add("1");
		block4.add("Service");
		// Execution time with hour unit
		block4.add("0.1");
		
		ArrayList block5= new ArrayList();
		// Location
		block5.add("0");
		// Clearance
		block5.add("2");
		block5.add("Service");
		// Execution time with hour unit
		block5.add("10");
		
		ArrayList block6= new ArrayList();
		// Location
		block6.add("0");
		// Clearance
		block6.add("1");
		block6.add("Service");
		// Execution time with hour unit
		block6.add("7");
		
		ArrayList block7= new ArrayList();
		// Location
		block7.add("0");
		// Clearance
		block7.add("0");
		block7.add("Service");
		// Execution time with hour unit
		block7.add("20");
		
		ArrayList block8= new ArrayList();
		// Location
		block8.add("0");
		// Clearance
		block8.add("2");
		block8.add("Service");
		// Execution time with hour unit
		block8.add("0.1");
		
		ArrayList block9= new ArrayList();
		// Location
		block9.add("0");
		// Clearance
		block9.add("2");
		block9.add("Service");
		// Execution time with hour unit
		block9.add("0.3");
		ArrayList block10= new ArrayList();
		// Location
		block10.add("0");
		// Clearance
		block10.add("1");
		block10.add("Service");
		// Execution time with hour unit
		block10.add("5");
		
		blockInfo.put("396EB93E-3C16-1AED-138B-D33FA77094E5",block1);
		blockInfo.put("D5CD89A9-9EEC-03D7-7E84-E3E0D8744495",block2);
		blockInfo.put("8E15DA6F-99E8-ABED-0CD3-9C0AF0FA1BA9",block3);
		blockInfo.put("DFE9C9C9-0832-6E20-FB54-CC75BEFC7BCA",block4);
		blockInfo.put("F87EF706-6806-EEB4-23B7-0B5D5324B0C7",block5);
		blockInfo.put("EC843F44-B9FC-127C-4231-AC4E52DDA717",block6);
		blockInfo.put("D7F38A8D-FCF6-FCA1-EABF-ED47E0772716",block7);
		blockInfo.put("904705CE-650A-14FD-39BF-9AF60332BC1A",block8);
		blockInfo.put("2FD0DF1E-88F9-EB49-A947-A351139BE30B",block9);
		blockInfo.put("B35325AC-06F3-1FBD-FBE4-945BF2BD8042",block10);
		
	}
	
	/*
	 * communication information
	 * */
	void CommuInfo() {
		// 2-5
		ArrayList<String> link1 = new ArrayList<String>();
		link1.add("D5CD89A9-9EEC-03D7-7E84-E3E0D8744495");
		link1.add("F87EF706-6806-EEB4-23B7-0B5D5324B0C7");
		link1.add("Sleep IO Block");
		link1.add("Sleep IO Block");
		link1.add("data");
		link1.add("data");
		link1.add("0");
		link1.add("Data");
		link1.add("1.1");
		// 7-9
		ArrayList<String> link2 = new ArrayList<String>();
		link2.add("D7F38A8D-FCF6-FCA1-EABF-ED47E0772716");
		link2.add("2FD0DF1E-88F9-EB49-A947-A351139BE30B");
		link2.add("Sleep IO Block");
		link2.add("Sleep IO Block");
		link2.add("data");
		link2.add("data");
		link2.add("0");
		link2.add("Data");
		link2.add("3.6");
		// 9-10
		ArrayList<String> link3 = new ArrayList<String>();
		link3.add("2FD0DF1E-88F9-EB49-A947-A351139BE30B");
		link3.add("B35325AC-06F3-1FBD-FBE4-945BF2BD8042");
		link3.add("Sleep IO Block");
		link3.add("CSVExport");
		link3.add("data");
		link3.add("input-data");
		link3.add("0");
		link3.add("Data");
		link3.add("0.05");
		// 8-9
		ArrayList<String> link4 = new ArrayList<String>();
		link4.add("904705CE-650A-14FD-39BF-9AF60332BC1A");
		link4.add("2FD0DF1E-88F9-EB49-A947-A351139BE30B");
		link4.add("Sleep IO Block");
		link4.add("Sleep IO Block");
		link4.add("references");
		link4.add("references");
		link4.add("0");
		link4.add("Data");
		link4.add("0");
		// 3-8
		ArrayList<String> link5 = new ArrayList<String>();
		link5.add("8E15DA6F-99E8-ABED-0CD3-9C0AF0FA1BA9");
		link5.add("904705CE-650A-14FD-39BF-9AF60332BC1A");
		link5.add("CSVImport");
		link5.add("Sleep IO Block");
		link5.add("imported-data");
		link5.add("data");
		link5.add("2");
		link5.add("Data");
		link5.add("0");
		//1-8
		ArrayList<String> link6 = new ArrayList<String>();
		link6.add("396EB93E-3C16-1AED-138B-D33FA77094E5");
		link6.add("904705CE-650A-14FD-39BF-9AF60332BC1A");
		link6.add("Import File");
		link6.add("Sleep IO Block");
		link6.add("imported-file");
		link6.add("files");
		link6.add("1");
		link6.add("Data");
		link6.add("0");
		//6-7
		ArrayList<String> link7 = new ArrayList<String>();
		link7.add("EC843F44-B9FC-127C-4231-AC4E52DDA717");
		link7.add("D7F38A8D-FCF6-FCA1-EABF-ED47E0772716");
		link7.add("Import File");
		link7.add("Sleep IO Block");
		link7.add("imported-file");
		link7.add("files");
		link7.add("0");
		link7.add("Data");
		link7.add("10.3");
		//5-7
		ArrayList<String> link8 = new ArrayList<String>();
		link8.add("F87EF706-6806-EEB4-23B7-0B5D5324B0C7");
		link8.add("D7F38A8D-FCF6-FCA1-EABF-ED47E0772716");
		link8.add("Sleep IO Block");
		link8.add("Sleep IO Block");
		link8.add("references");
		link8.add("references");
		link8.add("0");
		link8.add("Data");
		link8.add("6.2");
		//4-5
		ArrayList<String> link9 = new ArrayList<String>();
		link9.add("DFE9C9C9-0832-6E20-FB54-CC75BEFC7BCA");
		link9.add("F87EF706-6806-EEB4-23B7-0B5D5324B0C7");
		link9.add("Sleep IO Block");
		link9.add("Sleep IO Block");
		link9.add("files");
		link9.add("files");
		link9.add("0");
		link9.add("Data");
		link9.add("0.005");
		//4-7
		ArrayList<String> link10 = new ArrayList<String>();
		link10.add("DFE9C9C9-0832-6E20-FB54-CC75BEFC7BCA");
		link10.add("D7F38A8D-FCF6-FCA1-EABF-ED47E0772716");
		link10.add("Sleep IO Block");
		link10.add("Sleep IO Block");
		link10.add("data");
		link10.add("data");
		link10.add("0");
		link10.add("Data");
		link10.add("0.005");
		connections.add(link1);
		connections.add(link2);
		connections.add(link3);
		connections.add(link4);
		connections.add(link5);
		connections.add(link6);
		connections.add(link7);
		connections.add(link8);
		connections.add(link9);
		connections.add(link10);
		
	}
	
	/*
	 * cloud :  security, ip, transIn, transOut, CPU, startTime, StorageCost
	 *  */	
	
	void CloudInfo(){
		
		String cloud1="cloud1";
		String cloud2="cloud2";
		String cloud3="cloud3";
		String cloud1ip="10.66.66.176";
		String cloud2ip="10.66.66.252";
		String cloud3ip="10.66.66.253";
		//private cloud
		Cloud c1=new Cloud(cloud1,"2",cloud1ip,0.05, 0.1, 3.41,5,0.3);
		// school VM
		Cloud c2=new Cloud(cloud2,"1",cloud2ip,0, 0.08, 2.40,30.8,0.2);
		// Azure
		Cloud c3=new Cloud(cloud3,"0",cloud3ip,0.01, 0.02,1.28,60.5,0.1);
		cloudSet.add(c1);
		cloudSet.add(c2);
		cloudSet.add(c3);
	}
	
	public static void main(String args[]) throws Exception{
	
		new newworkflow();
		new CloudPool(cloudSet);
		writeThread p=new writeThread();
		p.start();
	//	Thread.sleep(100000);
    //	p.kill();
		cloudMonitorIm cm=new cloudMonitorIm();
		cm.initAvaClouds(cloudSet);
       new prepareWorkflow(workflowId, connections, blockInfo,cm);
	
	
	}
}
