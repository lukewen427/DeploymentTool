package uk.ac.ncl.cs.esc.deployment.HEFT;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public  class dataCenter {
	 
	 

	 public static class dataStorage{
		 
		 public static HashMap<String,ByteArrayOutputStream> results=new HashMap<String,ByteArrayOutputStream> ();
		 public static HashMap<String,ByteArrayOutputStream> getData(){
			 
			 return results;
		 }
		 public static void setData(HashMap<String,ByteArrayOutputStream> newResults){
			 if(!newResults.isEmpty()){
					Iterator<String> name=newResults.keySet().iterator();
					while(name.hasNext()){
						String singalParition=name.next();
						ByteArrayOutputStream data=newResults.get(singalParition);
						if(results.isEmpty()){
							results.put(singalParition, data);
							System.out.println("storing data of "+singalParition);
						}else{
							Set<String> storedData=results.keySet();
							if(!storedData.contains(singalParition)){
								results.put(singalParition, data);
								System.out.println("storing data of "+singalParition);
							}
						}
						
					}
				}
		 }
	 }
	 
}
