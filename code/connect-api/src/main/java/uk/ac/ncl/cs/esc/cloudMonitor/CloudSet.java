package uk.ac.ncl.cs.esc.cloudMonitor;

import java.util.Iterator;
import java.util.Set;

public class CloudSet {
	

	public CloudSet(Set<Cloud> Clouds){
		new Clouds(Clouds);
	}
	public static class Clouds{
		static Set<Cloud> clouds;
		public Clouds(Set<Cloud> clouds){
			this.clouds=clouds;
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
