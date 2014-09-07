package uk.ac.ncl.cs.esc.cloudMonitor;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool.Clouds;
import uk.ac.ncl.cs.esc.cloudchange.avaClouds;

public class cloudMonitorIm implements cloudMonitor{
	
	avaClouds ava;

	public boolean isCloudAVA(String cloudName) {
		cloudCheck check=new cloudCheck();
	//	String cloudName="Cloud"+cloud;
	//	Cloud thecloud=Clouds.getCloud(cloudName);
//		String cloudip=thecloud.getip();
		if(check.checkCloud(cloudName)){
			return true;
		}else{
			return false;
		}
		
	}
	
 public	void initAvaClouds(Set<Cloud> Clouds){
	 LinkedList<String> temp=new LinkedList<String>();
		Iterator<Cloud> getClouds=Clouds.iterator();
		cloudCheck check=new cloudCheck();
		while(getClouds.hasNext()){
			Cloud c=getClouds.next();
			String name=c.getCloudname();
			if(check.checkCloud(name)){
				temp.add(name);
			}
		}
		
	//	System.out.println(temp);
		this.ava=new avaClouds(temp);
		new Thread(ava).start();
	}

@Override
public boolean statuesChange(LinkedList<String> runningClouds) {
	// TODO Auto-generated method stub
	LinkedList<String> avaclouds=ava.getAvaClouds();
	if(avaclouds.size()>runningClouds.size()||avaclouds.size()<runningClouds.size()){
		return true;
	}else{
		
		for(int a=0;a<avaclouds.size();a++){
			if(!runningClouds.contains(avaclouds.get(a))){
				return true;
			}
		}
		return false;
	}
	
}

   public LinkedList<String> getAvaClouds(){
	   LinkedList<String> avaclouds=ava.getAvaClouds();
	   return avaclouds;
   }  

}
