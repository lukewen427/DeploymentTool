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
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.document.DocumentVersion;
import com.connexience.server.model.folder.Folder;
import com.connexience.server.model.security.Ticket;
import com.connexience.server.model.security.User;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;

import java.util.List;
import java.io.*;

import org.pipeline.core.drawing.DrawingException;
import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.io.XmlDataStoreStreamWriter;

import uk.ac.ncl.cs.esc.connection.connection;
public class deployWFIm implements deployWF {

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
	public DataProcessorBlock createBlock(API api, String serviceId) throws Exception {
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

	@Override
	public DocumentRecord createWorkflow(String name,API api,DefaultDrawingModel drawing) throws Exception {
		// TODO Auto-generated method stub
		Ticket t = api.getTicket();
		 User u = api.getUser(t.getUserId());
		 DocumentRecord workflow =api.getOrCreateDocumentRecord(u.getHomeFolderId(), name);
		 XmlDataStore wfData = drawing.storeObject();
		    XmlDataStoreStreamWriter writer = new XmlDataStoreStreamWriter(wfData);
		    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		    writer.write(outStream);
		    ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
		    DocumentVersion  versionId =api.upload(workflow, inStream);
		    return workflow;
	}
}
