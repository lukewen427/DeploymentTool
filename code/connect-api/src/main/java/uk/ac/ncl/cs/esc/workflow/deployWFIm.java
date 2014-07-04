package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.connexience.api.StorageClient;
import com.connexience.api.model.EscDocument;
import com.connexience.api.model.EscDocumentVersion;

import java.io.*;
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
}
