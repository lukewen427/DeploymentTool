package uk.ac.ncl.cs.esc.cloudchange;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.cloudMonitor.CloudPool.Clouds;
import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;

public class avaClouds implements Runnable{
	
LinkedList<String> avClouds;
Set<String> allClouds;	
boolean isChange;

public avaClouds(LinkedList<String>  avClouds){
	this.avClouds =avClouds;
	this.allClouds=Clouds.getallClouds();
}

@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {
			isChange = false;
			this.allClouds = Clouds.getAvaClouds();
			Iterator<String> clouds = allClouds.iterator();
		//	System.out.println(allClouds);
			cloudMonitorIm cm = new cloudMonitorIm();
			ArrayList<String> change = new ArrayList<String>();
			while (clouds.hasNext()) {

				String cloudName = clouds.next();
				if (cm.isCloudAVA(cloudName)) {
					change.add(cloudName);
				}
			}

			if (change.isEmpty() && !allClouds.isEmpty()) {

			} else {
				if (allClouds.isEmpty()) {
					break;
				} else {
				//	 System.out.println(change);

					isChange = compare(change);
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			allClouds.clear();
		}
	}

boolean compare(ArrayList<String> change){
	
	LinkedList<String> temp=deepClone(avClouds);
	boolean isChange=false;
	for(int a=0;a<temp.size();a++){
		String oldCloud=temp.get(a);
		if(!change.contains(oldCloud)){
			removeCloud(oldCloud);
			isChange=true;
		}
	}
	
	change.removeAll(avClouds);
	if(!change.isEmpty()){
		isChange=true;
		for(String re: change){
			 addCloud(re);
		}
	}
	return isChange;
}

LinkedList<String> deepClone(LinkedList<String> av){
	LinkedList<String> temp=new LinkedList<String>();
	for(String name:av){
		temp.add(name);
	}
	return temp;
}

public boolean isChange(){
	
	return isChange;
}

public LinkedList<String> getAvaClouds(){
	return avClouds;
}

public synchronized void  addCloud(String cloudName){
	if(!avClouds.contains(cloudName)){
		avClouds.add(cloudName);
	}
}

public synchronized void removeCloud(String cloudName){
	if(avClouds.contains(cloudName)){
		avClouds.remove(cloudName);
	}
}


}
