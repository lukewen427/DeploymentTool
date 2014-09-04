package uk.ac.ncl.cs.esc.cloudMonitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cloud {
	private String name;
//	private String securityLevel;
	private String cloudip;
//	private int StorageCost;
//	private double TransferIn;
//	private double TransferOut;
//	private double CPUCost;
	public Cloud(String name,String cloudip){
		this.name=name;
	//	this.securityLevel=securityLevel;
		this.cloudip=cloudip;
//		this.StorageCost=StorageCost;
//		this.TransferIn=TransferIn;
//		this.TransferOut= TranferOut;
//		this.CPUCost=CPUCost;
	}
	public String getCloudname(){
		
		return name;
	}
	

	public String getip(){
		
		return cloudip;
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
