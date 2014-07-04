package uk.ac.ncl.cs.esc.read;

import java.util.Iterator;
import java.util.Set;

public class CloudSet {
	Set<Cloud> Clouds;
	public CloudSet(Set<Cloud> Clouds){
		this.Clouds=Clouds;
	}
	public Cloud getCloud(String name){
		Cloud thecloud=null;
		Iterator<Cloud> getClouds=Clouds.iterator();
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
