package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import org.pipeline.core.drawing.model.DefaultDrawingModel;
import org.pipeline.core.xmlstorage.XmlDataStore;

import uk.ac.ncl.cs.esc.connection.connection;

import com.connexience.api.StorageClient;
import com.connexience.api.WorkflowClient;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.model.workflow.WorkflowDocument;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;
import com.connexience.server.workflow.service.DataProcessorServiceDefinition;

public interface deployWF {
	
public HashMap<String,String> fileUpload(HashMap<String, ByteArrayOutputStream> theresults,StorageClient Sclient)throws Exception;
public DataProcessorBlock createBlock(String serivceId )throws Exception ;
public DocumentRecord  createWorkflow(String name,DefaultDrawingModel drawing,String wfFolderId) throws Exception;
public void executeWF(DefaultDrawingModel drawing,String partitionName,StorageClient Sclient,
															HashMap<String, ByteArrayOutputStream> result) throws Exception;

public DocumentRecord loadWorkflow(String name, String wfFolderId, XmlDataStore wfdata) throws Exception;

public void execute(String partitionName,StorageClient Sclient,XmlDataStore wfdata) throws Exception;

public void killExe(String invocationId);

}
