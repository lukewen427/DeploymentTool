package uk.ac.ncl.cs.esc.workflow;

import java.util.Enumeration;

import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.xmlstorage.XmlDataObject;
import org.pipeline.core.xmlstorage.XmlDataStore;


public class XmlPartitionImp implements XmlPartition {
	XmlDataStore copy=new XmlDataStore();
	DefaultDrawingModel drawing =new DefaultDrawingModel();
	public void setWFData(XmlDataStore wfData){
		getCopy(wfData);
		 try {
			drawWF();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		print();
	}
	
	void getCopy(XmlDataStore wfData){
		Enumeration ele=wfData.elements();
		while(ele.hasMoreElements()){
			Object obj=ele.nextElement();
			XmlDataObject xobj=(XmlDataObject)obj;
			copy.add(xobj.getCopy());
	//		System.out.println(xobj.getName());
		}
	}
	
	public DefaultDrawingModel drawWF() throws Exception{
		 drawing.recreateObject(copy);
		 return drawing;
	}
	
	public void addBlock(){
	
	}
	
	public void removeBlock(){
		
	}
	void print(){
	
		Enumeration ele=copy.elements();
		while(ele.hasMoreElements()){
			Object obj=ele.nextElement();
			XmlDataObject xobj=(XmlDataObject)obj;
		//	copy.add(xobj);
			System.out.println(xobj.getName());
		}

	}
	
}
