package uk.ac.ncl.cs.esc.cloudMonitor;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class cloudCheck {

	String url="/Users/zhenyuwen/git/ExceptionHandler/website/statues.txt";
	
	
	Set<String> cloudSet; 
	public cloudCheck(){
		try {
		this.cloudSet=getCloud();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean checkCloud(String cloudName){
	
			
			if(cloudSet.contains(cloudName)){
				return true;
			}
	
	//	String staute=machines.get(cloudip);
	     return false;
		
	}
	/*
	private HashMap<String,String>  getCloud(){
		getClouds clouds=new getClouds();
		HashMap<String,String> machines = null;
		try {
			machines=clouds.getHttp(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return machines;
	}*/
	
public Set<String> getCloud() throws IOException{
	getClouds clouds=new getClouds();
	
	return clouds.getHttp(url);
	
}

	public static void main(String[] arg) throws IOException{
	//	getDeployment test=new getDeployment();
	//	test.getCloud();
	}
}
