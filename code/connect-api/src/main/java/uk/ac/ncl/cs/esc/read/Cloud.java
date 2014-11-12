package uk.ac.ncl.cs.esc.read;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cloud {
	private String name;
	private String securityLevel;
	private String cloudip;
//	private int StorageCost;
	private double TransferIn;
	private double TransferOut;
	private double CPUCost;
	private double startTime;
	private double storageCost;
	public Cloud(String name,String securityLevel,String cloudip,
			double TransferIn, double TranferOut,double CPUCost,double startTime,double storageCost){
		this.name=name;
		this.securityLevel=securityLevel;
		this.cloudip=cloudip;
//		this.StorageCost=StorageCost;
		this.TransferIn=TransferIn;
		this.TransferOut= TranferOut;
		this.CPUCost=CPUCost;
		this.startTime=startTime;
		this.storageCost=storageCost;
	}
	public String getCloudname(){
		
		return name;
	}
	
	public String getCloudsecurityLevel(){
		
		return securityLevel;
	}
	
	
	public String getip(){
		
		return cloudip;
	}
	
	public double getTransferin(){
		
		return TransferIn;
	}
	public double getTransferout(){
		return TransferOut;
		
	}
	public double getCPUcost(){
		return CPUCost;
	}
	
	public double getStartTime(){
		return startTime;
	}
	
	public double getStorageCost(){
		return storageCost;
	}
	// get the number in string
	public int getNumber(){
		int number = 0;
		Pattern pattern = Pattern.compile("\\d+"); 
		Matcher matcher = pattern.matcher(name);  
		while(matcher.find()){
			number=Integer.valueOf(matcher.group(0));
		}
		return number;
	}
}
