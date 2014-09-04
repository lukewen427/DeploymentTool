package uk.ac.ncl.cs.esc.cloudMonitor;


import uk.ac.ncl.cs.esc.cloudMonitor.CloudSet.Clouds;

public class cloudMonitorIm implements cloudMonitor{
	

public cloudMonitorIm(){ 
	
}
	public boolean checkCloud(int cloud) {
		cloudCheck check=new cloudCheck();
		String cloudName="Cloud"+cloud;
		Cloud thecloud=Clouds.getCloud(cloudName);
		String cloudip=thecloud.getip();
		if(check.checkCloud(cloudip)){
			return true;
		}else{
			return false;
		}
		
	}

}
