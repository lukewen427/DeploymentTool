package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.api.model.EscDocument;
import com.connexience.api.model.EscDocumentVersion;
import com.connexience.api.model.EscFolder;
import com.connexience.api.model.EscWorkflowInvocation;
import com.connexience.api.model.EscWorkflowParameterList;
import com.connexience.server.model.ServerObject;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.model.workflow.WorkflowInvocationFolder;
import com.connexience.server.model.workflow.WorkflowParameterList;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;

import java.util.List;
import java.io.*;

import org.pipeline.core.drawing.BlockModel;
import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;

import uk.ac.ncl.cs.esc.connection.connection;
public class deployWFIm implements deployWF {
	API api;
	public deployWFIm(API api){
		this.api=api;
	}
	// EscDocument is the stored data
	@Override
	public HashMap<String, String> fileUpload(
			HashMap<String, ByteArrayOutputStream> theresults,
			StorageClient Sclient) throws Exception {
		HashMap<String,String> DocumentSet=new HashMap<String,String>();
		Set<String> resultSet=theresults.keySet();
		Iterator<String> getDocument=resultSet.iterator();
		String folderId=Sclient.homeFolder().getId();
		while(getDocument.hasNext()){
			EscDocument tempFile=null;	
			String BlockName=(String) getDocument.next();
			tempFile=Sclient.createDocumentInFolder(folderId, BlockName);
			
			ByteArrayOutputStream inStream=theresults.get(BlockName);
			byte[] inString = inStream.toByteArray();
			long contentLength=inString.length;
			InputStream input= new ByteArrayInputStream(inString);
			EscDocumentVersion  versionId =Sclient.upload(tempFile, input, contentLength);
		
			String fileId=tempFile.getId();
			DocumentSet.put(BlockName, fileId);
		}
		return DocumentSet;
	
	}

	  public DataProcessorServiceDefinition loadServiceXml() throws Exception {
		  DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
	        InputStream xmlStream = getClass().getResourceAsStream("/service.xml");
	        def.loadXmlStream(xmlStream);
	        return def;
	    }
	  

	@Override
	public DataProcessorBlock createBlock(String serviceId) throws Exception {
		// TODO Auto-generated method stub
			DocumentRecord serviceDoc=api.getDocument(serviceId);
	        List<DocumentVersion>serviceVersions=api.getDocumentVersions(serviceDoc);
	        DocumentVersion version=null;
	        int versionNumber=0;
	        for(DocumentVersion v:serviceVersions){
	        	if(v.getVersionNumber()>=versionNumber){
	                version = v;
	            }
	        }
	        if(version!=null){
	            DataProcessorBlock block = new DataProcessorBlock();
	           
	            DataProcessorServiceDefinition def;
	            // if not from out side
	            if(true){
	            	def=api.getService(serviceId);
	            }else{
	            	def=loadServiceXml();
	            }
	        //    String serviceXml = api.getServiceXml(serviceDoc);
	       //     DataProcessorServiceDefinition def = new DataProcessorServiceDefinition();
	      //      def.loadXmlString(serviceXml);
	            
	            block.setServiceDefinition(def);
	            block.initialiseForService();
	            block.setServiceId(serviceDoc.getId());
	            block.setVersionId(version.getId());
	            block.setVersionNumber(version.getVersionNumber());
	            block.setUsesLatest(true);
			
			    return block;
		}else{
			throw new Exception("cannot find latest version of block");
		}
	}
	// DocumentRecod is the workflow document
	@Override
	public  WorkflowDocument createWorkflow(String name,DefaultDrawingModel drawing,String wfFolderId) throws Exception {
		// TODO Auto-generated method stub
	   
	
		
//		DocumentRecord workflow =api.getOrCreateDocumentRecord(wfFolderId, name);
	     	Folder f=api.getFolder(wfFolderId);
	     	
//	     	workflow =(DocumentRecord) api.saveDocument(f,workflow);
		    XmlDataStore wfData = drawing.storeObject();
		    WorkflowDocument  workflow=new WorkflowDocument();
		    workflow.recreateObject(wfData);
		    workflow.setName(name);
		    workflow=(WorkflowDocument)api.saveDocument(f,workflow);
		    XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(wfData);
		    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		    writer.write(outStream);
		    ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		    DocumentVersion  versionId =api.upload(workflow, inStream);
		    return  workflow;
	}
	
	@Override
	public WorkflowDocument loadWorkflow(String name, String wfFolderId, XmlDataStore wfData) throws Exception {
		// TODO Auto-generated method stub
		
		Folder f=api.getFolder(wfFolderId);
     	
//     	workflow =(DocumentRecord) api.saveDocument(f,workflow);
	 //   XmlDataStore wfData = drawing.storeObject();
	    WorkflowDocument  workflow=new WorkflowDocument();
	    workflow.recreateObject(wfData);
	    workflow.setName(name);
	    workflow=(WorkflowDocument)api.saveDocument(f,workflow);
	    XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(wfData);
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    writer.write(outStream);
	    ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
	    DocumentVersion  versionId =api.upload(workflow, inStream);
	    return  workflow;
	}
	
	public void execute(String partitionName,StorageClient Sclient,XmlDataStore wfdata) throws Exception{
		String wfFolderId=null;
		EscFolder home=Sclient.homeFolder();
		EscFolder[] flist=Sclient.listChildFolders(home.getId());
		for(EscFolder f:flist){
			if(f.getName().equals("Workflows")){
				wfFolderId=f.getId();
				break;
			}
		}
		  WorkflowDocument newDoc =loadWorkflow(partitionName,wfFolderId,wfdata);
		  WorkflowParameterList parameters =new WorkflowParameterList();
		 api.executeWorkflow((WorkflowDocument) newDoc, parameters, (long)-1, null);
	}
	
	public void executeWF(DefaultDrawingModel drawing,String partitionName,StorageClient Sclient,HashMap<String, ByteArrayOutputStream> result) throws Exception{
	//	JSONDrawingExporter exporter = new JSONDrawingExporter(drawing); 
			String wfFolderId=null;
			EscFolder home=Sclient.homeFolder();
			EscFolder[] flist=Sclient.listChildFolders(home.getId());
			for(EscFolder f:flist){
				if(f.getName().equals("Workflows")){
					wfFolderId=f.getId();
					break;
				}
			}
		
	     WorkflowDocument newDoc = createWorkflow(partitionName, drawing,wfFolderId);
		 WorkflowParameterList parameters =new WorkflowParameterList();
		 
		api.executeWorkflow((WorkflowDocument) newDoc, parameters, (long)-1, null);
	///	EscWorkflowParameterList p = new EscWorkflowParameterList();
		
	/*	EscWorkflowInvocation invocation=wfClient.executeWorkflow("937");
		while(invocation.getStatus().equals("Queued")||invocation.getStatus().equals("Running")||
				invocation.getStatus().equals("Debugging")){
			Thread.sleep(5000);
			invocation=wfClient.getInvocation(invocation.getId());
		}
		
		  WorkflowInvocationFolder inv = api.getWorkflowInvocation(invocation.getId());
		  EscDocument[] produces=Sclient.folderDocuments(inv.getId());
		 for(EscDocument doc:produces){
			 String name=doc.getName();
			 String[] thename=name.split("\\.");
			 ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			 Sclient.download(doc, outStream);
			 outStream.flush();
			 result.put(thename[0], outStream);
		 }*/
	}
	
}
