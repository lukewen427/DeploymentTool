package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool;
import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.cloudMonitor.writeThread;
import uk.ac.ncl.cs.esc.read.Cloud;

public class test1 {
public static void main(String args[]) throws Exception{
		
	HashMap<String,ArrayList<String>> blockInfo=new HashMap<String,ArrayList<String>>();
	/*first element is "Location",second is Clearance*/
	ArrayList security= new ArrayList();
	security.add("0");
	security.add("1");
	security.add("Service");
	security.add("60");
	ArrayList security1= new ArrayList();
	security1.add("0");
	security1.add("1");
	security1.add("Service");
	security1.add("100");
	ArrayList security2= new ArrayList();
	security2.add("0");
	security2.add("1");
	security2.add("Service");
	security2.add("500");
	ArrayList security3= new ArrayList();
	security3.add("0");
	security3.add("1");
	security3.add("Service");
	security3.add("40");
	ArrayList security4= new ArrayList();
	security4.add("1");
	security4.add("1");
	security4.add("Service");
	security4.add("10");
	ArrayList security5= new ArrayList();
	security5.add("1");
	security5.add("1");
	security5.add("Service");
	security5.add("14");
	
	blockInfo.put("153603AB-5611-A0DD-0F89-4379B14BC17A", security);
	blockInfo.put("6A64975D-4393-1DE3-402C-B58992FE2AC6", security1);
	blockInfo.put("89C67A33-C7C4-A44B-CDC4-CBE68FCECC0D", security2);
	blockInfo.put("8805C8A3-B7FB-CEB6-2F00-1C1D41ABD2A1", security3);
	blockInfo.put("791F7E0A-0C4B-50A0-6EC0-576E17656BB0", security4);
	blockInfo.put("2837AFB6-B61F-7873-A3B8-7FE4F5E4EAB6", security5);
	String workflowId="1045";
	ArrayList<ArrayList<String>> connections=new ArrayList<ArrayList<String>>();

		ArrayList<String> temp1=new ArrayList<String>();
		    temp1.add("153603AB-5611-A0DD-0F89-4379B14BC17A");
			temp1.add("6A64975D-4393-1DE3-402C-B58992FE2AC6");
			temp1.add("CSVImport");
			temp1.add("Add #");
			temp1.add("imported-data");
			temp1.add("input-data");
			temp1.add("0");
			temp1.add("Data");
			temp1.add("10");
			temp1.add("12");
			ArrayList<String> temp2=new ArrayList<String>();
			temp2.add("6A64975D-4393-1DE3-402C-B58992FE2AC6");
			temp2.add("89C67A33-C7C4-A44B-CDC4-CBE68FCECC0D");
			temp2.add("Add #");
			temp2.add("Subsample");
			temp2.add("output-data");
			temp2.add("input-data");
			temp2.add("0");
			temp2.add("Data");
			temp2.add("5");
			temp2.add("12");
	     	ArrayList<String> temp3=new ArrayList<String>();
	    	temp3.add("89C67A33-C7C4-A44B-CDC4-CBE68FCECC0D");
			temp3.add("8805C8A3-B7FB-CEB6-2F00-1C1D41ABD2A1");
			temp3.add("Subsample");
			temp3.add("Sort");
			temp3.add("subsampled-data");
			temp3.add("input-data");
			temp3.add("1");
			temp3.add("Data");
			temp3.add("20");
			temp3.add("0");
			ArrayList<String> temp4=new ArrayList<String>();
			temp4.add("89C67A33-C7C4-A44B-CDC4-CBE68FCECC0D");
			temp4.add("791F7E0A-0C4B-50A0-6EC0-576E17656BB0");
			temp4.add("Subsample");
			temp4.add("CSVExport");
			temp4.add("remaining-data");
			temp4.add("input-data");
			temp4.add("1");
			temp4.add("Data");
			temp4.add("20");
			temp4.add("12");
			ArrayList<String> temp5=new ArrayList<String>();
			temp5.add("8805C8A3-B7FB-CEB6-2F00-1C1D41ABD2A1"); 
			temp5.add("2837AFB6-B61F-7873-A3B8-7FE4F5E4EAB6");
			temp5.add("Sort");
			temp5.add("CSVExport");
			temp5.add("sorted-data");
			temp5.add("input-data");
			temp5.add("1");
			temp5.add("Data");
			temp5.add("30");
			temp5.add("10");
		connections.add(temp1);
		connections.add(temp2);
		connections.add(temp3);
		connections.add(temp4);
		connections.add(temp5);
		Set<Cloud> cloudSet=new HashSet<Cloud>();
		
		String cloud1="cloud1";
		String cloud2="cloud2";
		String cloud3="cloud3";
		String cloud1ip="10.66.66.176";
		String cloud2ip="10.66.66.252";
		String cloud3ip="10.66.66.252";
		
		Cloud c1=new Cloud(cloud1,"0",cloud1ip,2,2,5);
		Cloud c2=new Cloud(cloud2,"1",cloud2ip,5,5,10);
		Cloud c3=new Cloud(cloud3,"2",cloud3ip,5,5,10);
		cloudSet.add(c1);
		cloudSet.add(c2);
		cloudSet.add(c3);
		new CloudPool(cloudSet);
		writeThread p=new writeThread();
		p.start();
		
		cloudMonitorIm cm=new cloudMonitorIm();
		cm.initAvaClouds(cloudSet);
	  //   new readInfo(blockInfo,workflowId,connections);
	//	cloudMonitorIm cm = new cloudMonitorIm();
	       new prepareDeployment(workflowId, connections, blockInfo,cm);
		
	}
}
