package uk.ac.ncl.cs.esc.workflow;
import java.util.ArrayList;
import java.util.HashMap;

//import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import org.pipeline.core.xmlstorage.XmlDataStore;
public interface WorkflowInfo {
	public JSONObject getWorkflowAsJsonObject(String workflowId) throws Exception;
	public HashMap<String,String> Blocklist(String workflowId) throws Exception;
	public HashMap<String, ArrayList<String>>  ConnectionMap(String workflowId) throws Exception;
	public HashMap<String,String> Workflowlist() throws Exception;
//	public  HashMap<String,ByteArrayOutputStream> CreateWorkflow(String cloudName,ArrayList<Object> partition,String partitionName,
//			ArrayList<ArrayList<String>> connections,BlockSet blockset,ArrayList<ArrayList<String>>inputs,
//			HashMap<String, ByteArrayOutputStream> theresults,ArrayList<String> heads) throws Exception;
	public String getBlockServiceId(String BlockId,String workflowId) throws Exception;
	public ArrayList<String> getPorts(String workflowId,String sourceId,String endId) throws Exception;
	public ArrayList<ArrayList<String>> getSource(String workflowId) throws Exception;
	public ArrayList<String> getInputports(String workflowId,String blockId) throws Exception;
	public ArrayList<String> getOutputports(String workflowId,String blockId) throws Exception;
	public ArrayList<ArrayList<String>> getConnection(String workflowId) throws Exception;
	public String getBlockName(String BlockId,String workflowId) throws Exception;
	public XmlDataStore getWFdata(String workflowId) throws Exception;
}
