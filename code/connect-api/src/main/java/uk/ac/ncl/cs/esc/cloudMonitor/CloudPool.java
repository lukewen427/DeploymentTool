package uk.ac.ncl.cs.esc.cloudMonitor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import uk.ac.ncl.cs.esc.read.Cloud;

public class CloudPool {
	

	public CloudPool(Set<Cloud> Clouds){
		new Clouds(Clouds);
	}
	public static class Clouds{
		static Set<Cloud> clouds;
		public Clouds(Set<Cloud> clouds){
			this.clouds=clouds;
		}
		
		public static Set<String> getallClouds(){
			Set<String> cloudSet=new HashSet<String>();
			Iterator<Cloud> getClouds=clouds.iterator();
			while(getClouds.hasNext()){
				Cloud getCloud=getClouds.next();
				String getname=getCloud.getCloudname();
				cloudSet.add(getname);
			}
			
		  return cloudSet;
		}
		public static Cloud getCloud(String name){
			Cloud thecloud=null;
			Iterator<Cloud> getClouds=clouds.iterator();
			while(getClouds.hasNext()){
				Cloud getCloud=getClouds.next();
				String getname=getCloud.getCloudname();
				if(name.equals(getname)){
					thecloud=getCloud;
					break;
				}
			}
			return thecloud;
		}
	}
	
	
}
