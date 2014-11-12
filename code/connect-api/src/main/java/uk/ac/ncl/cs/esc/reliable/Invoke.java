package uk.ac.ncl.cs.esc.reliable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;




public class Invoke {
	Hashtable<workflowInv,Thread> runningTable=new Hashtable<workflowInv,Thread>();

	public void exec(ArrayList<Object> order) {
		
		for (int a = 0; a < order.size(); a++) {
			ArrayList<ArrayList<String>> step = (ArrayList<ArrayList<String>>) order
					.get(a);
			step(step);
			
			while (!runningTable.isEmpty()) {
				Iterator<workflowInv> key = runningTable.keySet().iterator();
				while (key.hasNext()) {
					workflowInv inv = null;
					try {
						inv = key.next();
					} catch (Exception e) {
						if (!runningTable.isEmpty()) {
							key = runningTable.keySet().iterator();
						}
					}
					if (inv != null) {
						Thread t = runningTable.get(inv);

						if (!t.isAlive()) {
							removeWF(inv);
						}
					}

				}
			}
		}
	}
	
	
	void step(ArrayList<ArrayList<String>> step){
		for(ArrayList<String> w:step){
			runWorkflow(w.get(0),w.get(1));
		}
	}
	void runWorkflow(String workflowId,String cloud){
		workflowInv wk=new workflowInv(workflowId,cloud);
		Thread t= new Thread(wk);
		t.start();
		addNewWF(wk,t);
	}
	
    synchronized void addNewWF(workflowInv excu,Thread t){
		
    	runningTable.put(excu, t);
	}

    synchronized void removeWF(workflowInv excu){
		
    	runningTable.remove(excu);
	}
	
	public static void main(String [] args) throws Exception{
	//	costWF w=new costWF();
		optWF w= new optWF();
		ArrayList<Object> order=w.getOrder();
		Invoke t=new Invoke();
		t.exec(order);
	}
	
}
