package uk.ac.ncl.cs.esc.cloudMonitor;

import java.util.ArrayList;

import uk.ac.ncl.cs.esc.read.RandomInt;

/*public class writeThread extends Thread {
	public writeThread(){
		String avaCloud="cloud1,cloud2,cloud3";
		write w=new write();
		w.writeFile(avaCloud);
	}
	public void run(){
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		write w=new write();
		w.writeFile("cloud1,cloud3");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		w=new write();
		w.writeFile("cloud1,cloud3");
	}
}*/

public class writeThread extends Thread {
	// initial reliability rate: 0.98, 0.90,0.83
	/*
	 * 
	 * */
	int per;
	boolean stop=false;
	ArrayList<String> clouds=new ArrayList<String>();
	public writeThread(){
		String avaCloud = "cloud1,cloud2,cloud3";	
		clouds.add("cloud1");
		clouds.add("cloud2");
		clouds.add("cloud3");
	//	 System.out.println(avaCloud);
		write w=new write();
		w.writeFile(avaCloud);
	}
	public void run(){
		int a=0;
		while(!stop){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			a+=5;
			 this.per = RandomInt.randomInt(0, 100);
			 boolean c1=cloud1(a);
			 boolean c2=cloud2(a);
			 boolean c3=cloud3(a);
	//		 String avaCloud=getCloud(c1,c2,c3);	
			 String avaCloud = "cloud1,cloud2,cloud3";	
	//		 System.out.println(avaCloud);
			write w=new write();
			w.writeFile(avaCloud);
		}
	
	}
public void kill(){
		stop=true;
	}

	String getCloud(boolean c1, boolean c2, boolean c3) {
		if(!c1){
			if(clouds.contains("cloud1")){
				clouds.remove("cloud1");
			}
		}
		if(!c2){
			if(clouds.contains("cloud2")){
				clouds.remove("cloud2");
			}
		}
		if(!c3){
			if(clouds.contains("cloud3")){
				clouds.remove("cloud3");
			}
		}
		return toString();
		/*if (c1 & c2 & c3) {
			avaCloud = "cloud1,cloud2,cloud3";
		}
		if (!c1 & c2 & c3) {
			avaCloud = "cloud2,cloud3";
		}
		if (c1 & !c2 & c3) {
			avaCloud = "cloud1,cloud3";
		}
		if (c1 & c2 & !c3) {
			avaCloud = "cloud1,cloud2";
		}
		if (!c1 & !c2 & c3) {
			avaCloud = "cloud3";
		}
		if (c1 & !c2 & !c3) {
			avaCloud = "cloud1";
		}
		if (!c1 & c2 & !c3) {
			avaCloud = "cloud2";
		}
		if (!c1 & !c2 & !c3) {
			
			return null;
		}*/
	}
	
	public String toString(){
		String avaClouds="";
	//	StringBuilder sb = new StringBuilder();
		for(String c: clouds){
			avaClouds+=c+ ",";
		}
		return avaClouds;
	}
	boolean cloud1(int time){
		double rate=Math.exp(-0.003*(5+time));
		int temp=(int)(rate*100);
		if(per<temp){
			return true;
		}else{
			return false;
		}
		
	}
	boolean cloud2(int time){
		double rate=Math.exp(-0.003*(30+time));
		int temp=(int)(rate*100);
		if(per<temp){
			return true;
		}else{
			return false;
		}
	}
	boolean cloud3(int time){
		double rate=Math.exp(-0.003*(60+time));
		int temp=(int)(rate*100);
		if(per<temp){
			return true;
		}else{
			return false;
		}
	}
	
	
}
