package uk.ac.ncl.cs.esc.read;

import java.util.ArrayList;
import java.util.HashMap;

public class test {
	
	
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
		
		blockInfo.put("0B0AA6EE-784B-D94F-16F8-B11E63695217", security);
		blockInfo.put("F6CA8419-8A3C-4252-F5A8-DB7432D8C99D", security1);
		blockInfo.put("DB4A9B71-B4A5-0292-F43A-11E767CF1B7A", security2);
		blockInfo.put("8C9A90C6-23D7-4A18-5526-18D4EC66C5AA", security3);
		blockInfo.put("3D2C9A3B-8EDE-B750-00E2-0F6503F065D5", security4);
		blockInfo.put("6FE5BDA9-7262-1A98-EC83-2672E64D7F23", security5);
		String workflowId="8ac2c23044e407ab0144e5a1e84a0596";
		ArrayList<ArrayList<String>> connections=new ArrayList<ArrayList<String>>();
	
			ArrayList<String> temp1=new ArrayList<String>();
			    temp1.add("0B0AA6EE-784B-D94F-16F8-B11E63695217");
				temp1.add("F6CA8419-8A3C-4252-F5A8-DB7432D8C99D");
				temp1.add("CSVLoad");
				temp1.add("Add #");
				temp1.add("imported-data");
				temp1.add("input-data");
				temp1.add("0");
				temp1.add("Data");
				temp1.add("10");
				temp1.add("12");
				ArrayList<String> temp2=new ArrayList<String>();
				temp2.add("F6CA8419-8A3C-4252-F5A8-DB7432D8C99D");
				temp2.add("DB4A9B71-B4A5-0292-F43A-11E767CF1B7A");
				temp2.add("Add #");
				temp2.add("Subsample");
				temp2.add("output-data");
				temp2.add("input-data");
				temp2.add("0");
				temp2.add("Data");
				temp2.add("5");
				temp2.add("12");
		     	ArrayList<String> temp3=new ArrayList<String>();
		    	temp3.add("DB4A9B71-B4A5-0292-F43A-11E767CF1B7A");
				temp3.add("8C9A90C6-23D7-4A18-5526-18D4EC66C5AA");
				temp3.add("Subsample");
				temp3.add("Sort");
				temp3.add("subsampled-data");
				temp3.add("input-data");
				temp3.add("1");
				temp3.add("Data");
				temp3.add("20");
				temp3.add("0");
				ArrayList<String> temp4=new ArrayList<String>();
				temp4.add("DB4A9B71-B4A5-0292-F43A-11E767CF1B7A");
				temp4.add("3D2C9A3B-8EDE-B750-00E2-0F6503F065D5");
				temp4.add("Subsample");
				temp4.add("CSVExport");
				temp4.add("remaining-data");
				temp4.add("input-data");
				temp4.add("1");
				temp4.add("Data");
				temp4.add("20");
				temp4.add("12");
				ArrayList<String> temp5=new ArrayList<String>();
				temp5.add("8C9A90C6-23D7-4A18-5526-18D4EC66C5AA"); 
				temp5.add("6FE5BDA9-7262-1A98-EC83-2672E64D7F23");
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
			new readWorkflow(workflowId,connections,blockInfo);
	}
}
