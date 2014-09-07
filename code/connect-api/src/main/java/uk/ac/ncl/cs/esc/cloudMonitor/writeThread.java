package uk.ac.ncl.cs.esc.cloudMonitor;

public class writeThread extends Thread {
	public writeThread(){
		String avaCloud="cloud1,cloud2";
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
		w.writeFile("cloud1,cloud2,cloud3");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		w=new write();
		w.writeFile("cloud1,cloud3");
	}
}
