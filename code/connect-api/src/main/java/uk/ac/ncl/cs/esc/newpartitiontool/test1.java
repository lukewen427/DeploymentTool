package uk.ac.ncl.cs.esc.newpartitiontool;

import java.util.ArrayList;
import java.util.HashMap;

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
		
		blockInfo.put("02E3CDFC-ED97-B068-E96C-8174C644CC23", security);
		blockInfo.put("AECEF0B2-4A7F-8A46-BFE9-C8968DB9C5D0", security1);
		blockInfo.put("4066D0E6-BED7-ABA2-792B-FB14927E5000", security2);
		blockInfo.put("2B5AB1EA-DA28-5E19-EE2B-70468763837D", security3);
		blockInfo.put("B9C91BF5-3E84-1022-8BED-CB45B4A8C68A", security4);
		blockInfo.put("806C05C8-C774-9595-29E8-D2DFCE4EECAD", security5);
		String workflowId="00000000450453f901450456059e0001";
		ArrayList<ArrayList<String>> connections=new ArrayList<ArrayList<String>>();
	
			ArrayList<String> temp1=new ArrayList<String>();
			    temp1.add("02E3CDFC-ED97-B068-E96C-8174C644CC23");
				temp1.add("AECEF0B2-4A7F-8A46-BFE9-C8968DB9C5D0");
				temp1.add("CSVLoad");
				temp1.add("Subsample");
				temp1.add("imported-data");
				temp1.add("input-data");
				temp1.add("0");
				temp1.add("Data");
				temp1.add("10");
				temp1.add("12");
				ArrayList<String> temp2=new ArrayList<String>();
				temp2.add("AECEF0B2-4A7F-8A46-BFE9-C8968DB9C5D0");
				temp2.add("4066D0E6-BED7-ABA2-792B-FB14927E5000");
				temp2.add("Subsample");
				temp2.add("Sort");
				temp2.add("subsampled-data");
				temp2.add("input-data");
				temp2.add("0");
				temp2.add("Data");
				temp2.add("5");
				temp2.add("12");
		     	ArrayList<String> temp3=new ArrayList<String>();
		    	temp3.add("AECEF0B2-4A7F-8A46-BFE9-C8968DB9C5D0");
				temp3.add("2B5AB1EA-DA28-5E19-EE2B-70468763837D");
				temp3.add("Subsample");
				temp3.add("Shuffle");
				temp3.add("remaining-data");
				temp3.add("input-data");
				temp3.add("0");
				temp3.add("Data");
				temp3.add("20");
				temp3.add("0");
				ArrayList<String> temp4=new ArrayList<String>();
				temp4.add("4066D0E6-BED7-ABA2-792B-FB14927E5000");
				temp4.add("B9C91BF5-3E84-1022-8BED-CB45B4A8C68A");
				temp4.add("Sort");
				temp4.add("Column Join");
				temp4.add("sorted-data");
				temp4.add("left-hand-data");
				temp4.add("1");
				temp4.add("Data");
				temp4.add("20");
				temp4.add("12");
				
				ArrayList<String> temp5=new ArrayList<String>();
				temp5.add("2B5AB1EA-DA28-5E19-EE2B-70468763837D"); 
				temp5.add("B9C91BF5-3E84-1022-8BED-CB45B4A8C68A");
				temp5.add("Shuffle");
				temp5.add("Column Join");
				temp5.add("shuffled-data");
				temp5.add("right-hand-data");
				temp5.add("1");
				temp5.add("Data");
				temp5.add("30");
				temp5.add("10");
				ArrayList<String> temp6=new ArrayList<String>();
				temp6.add("B9C91BF5-3E84-1022-8BED-CB45B4A8C68A"); 
				temp6.add("806C05C8-C774-9595-29E8-D2DFCE4EECAD");
				temp6.add("Column Join");
				temp6.add("CSVExport");
				temp6.add("joined-data");
				temp6.add("input-data");
				temp6.add("1");
				temp6.add("Data");
				temp6.add("30");
				temp6.add("10");
			connections.add(temp1);
			connections.add(temp2);
			connections.add(temp3);
			connections.add(temp4);
			connections.add(temp5);
			connections.add(temp6);
			
	  //   new readInfo(blockInfo,workflowId,connections);
			new prepareDeployment(workflowId, connections, blockInfo);
		
	}
}
