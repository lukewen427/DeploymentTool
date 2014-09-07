package uk.ac.ncl.cs.esc.cloudMonitor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class write {
	 String url= "/Users/zhenyuwen/git/ExceptionHandler/website/statues.txt";
	 
	 public void writeFile(String params){
		OutputStream outs = null;
		
		 try {
			outs = new FileOutputStream(url);
			byte[] ob = params.getBytes();
				outs.write(ob);
		 	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
