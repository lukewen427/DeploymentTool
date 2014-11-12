package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.pipeline.core.drawing.layout.BlockModelPosition;
import org.pipeline.core.drawing.model.DefaultDrawingModel;

import uk.ac.ncl.cs.esc.connection.cloudConnection;
import uk.ac.ncl.cs.esc.connection.connection;
import uk.ac.ncl.cs.esc.read.Block;

import com.connexience.api.StorageClient;
import com.connexience.server.model.document.DocumentRecord;
import com.connexience.server.workflow.api.API;
import com.connexience.server.workflow.blocks.processor.DataProcessorBlock;

public class eSCBlock {

	String cloudName;
	Block Block;
	HashMap<String, ByteArrayOutputStream> theresults;
	ArrayList<ArrayList<String>> inputs;
	ArrayList<ArrayList<String>> outputs;
	deployWF wf;
	String serviceId;
	String importData = "blocks-core-io-csvimport-2";
	String importfile = "blocks-core-io-importfile";
	String exportscsv="blocks-core-io-csvexport";
	String exportsfile="blocks-core-io-exportfiles";
	// the output port of CSVImport
		String importPORT="imported-data";
		//output port of fileimport
		String importfileP="imported-file";
		// the input port of CSVExport
		String exportPORT="input-data";
		// the input port for fileExport
		String exportfile="file-list";
	public eSCBlock(String cloudName, Block Block,
			HashMap<String, ByteArrayOutputStream> theresults,
			ArrayList<ArrayList<String>> inputs,
			ArrayList<ArrayList<String>> outputs) {
		this.cloudName = cloudName;
		this.Block = Block;
		this.theresults = theresults;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	public HashMap<String, ByteArrayOutputStream> blockInv() throws Exception {
		cloudConnection coCloud = new cloudConnection();
		connection con = coCloud.creatCon(cloudName);
		API api = con.getAPI();
		this.wf = new deployWFIm(api);
		StorageClient Sclient = con.getStorageAPI();
		HashMap<String, String> resultInfo = null;
		if (!theresults.isEmpty()) {
			resultInfo = wf.fileUpload(theresults, Sclient);
		}
	//	System.out.println(resultInfo);
		int b = 0;
		DefaultDrawingModel drawing = new DefaultDrawingModel();
		DataProcessorBlock serviceBlock = null;
		String blockid = Block.getBlockId();
		String serviceId = Block.getserviceId();
	//	System.out.println(Block.getBlockName());
	//	System.out.println(inputs);
	//	System.out.println(outputs);
		if (serviceId.equals(importData)) {
			String documentId = null;
			String serviceName = null;
			serviceBlock = wf.createBlock(serviceId);
			createblock(b, serviceBlock, documentId, serviceId, api, drawing,
					serviceName);
			b++;
		}

		if (serviceId.equals(importfile)) {
			String documentId = null;
			String serviceName = null;
			serviceBlock = wf.createBlock(serviceId);
			createblock(b, serviceBlock, documentId, serviceId, api, drawing,
					serviceName);
			b++;
		}
		
		if(!serviceId.equals(importfile)&&!serviceId.equals(importData)){
			String documentId = null;
			String serviceName = null;
			serviceBlock = wf.createBlock(serviceId);
			createblock(b, serviceBlock, documentId, serviceId, api, drawing,
					serviceName);
			b++;
		}
		
		// input blocks
		if(inputs!=null){
			
			for(int a=0;a<inputs.size();a++){
				ArrayList<String> link=inputs.get(a);
				String inName=link.get(0);
				String inType=link.get(5);
				
				if(inType.equals("Data")){
					String findId=inName+","+blockid;
					String documentId;
					if(resultInfo==null||!resultInfo.containsKey(findId)||!resultInfo.containsKey(findId)){
						documentId="Book1";
					
					}else{
						documentId=resultInfo.get(findId);
					}
					String theserviceId=importData;
					DataProcessorBlock importBlock = wf.createBlock(theserviceId);
					createblock(b,importBlock,documentId,theserviceId, api,drawing,"import");
					b++;
					drawing.connectPorts(importBlock.getOutput(importPORT), serviceBlock.getInput(inType));
				}
				if(inType.equals("files")){
					String findId=inName+","+blockid;
					String documentId;
					if(resultInfo==null||!resultInfo.containsKey(findId)){
						documentId="images";
					}else{
						documentId=resultInfo.get(findId);
					}
					
					String theserviceId=importfile;
					DataProcessorBlock importBlock = wf.createBlock(theserviceId);
					createblock(b,importBlock,documentId,theserviceId, api,drawing,"import");
					b++;
					drawing.connectPorts(importBlock.getOutput(importfileP), serviceBlock.getInput(inType));
				}
				
			}
		}
		// output blocks
		if(outputs!=null){
			
			for(int a=0;a<outputs.size();a++){
				ArrayList<String> link=outputs.get(a);
				String outName=link.get(1);
				String outType=link.get(4);
				if(outType.equals("Data")){
					String theserviceName=blockid+","+outName;
					String theserviceId=exportscsv;
					DataProcessorBlock exportBlock = wf.createBlock(theserviceId);
					createblock(b,exportBlock,null,theserviceId, api,drawing,theserviceName);
					b++;
					drawing.connectPorts(serviceBlock.getOutput(outType), exportBlock.getInput(exportPORT));
				}
				if(outType.equals("files")){
					String theserviceName=blockid+","+outName;
				//	System.out.println(theserviceName);
					String theserviceId=exportsfile;
					DataProcessorBlock exportBlock = wf.createBlock(theserviceId);
					createblock(b,exportBlock,null,theserviceId, api,drawing,theserviceName);
					b++;
					drawing.connectPorts(serviceBlock.getOutput(outType), exportBlock.getInput(exportfile));
				}
			}
		}
		HashMap<String, ByteArrayOutputStream> result = new HashMap<String, ByteArrayOutputStream>();
		 wf.executeWF(drawing, blockid+"cost", Sclient, result);
//		wf.executeWF(drawing, blockid, Sclient, result);
	//	 System.out.println(result);
		return result;
	}

	private static void createblock(int b, DataProcessorBlock Block,
			String documentId, String serviceId, API api,
			DefaultDrawingModel drawing, String theServiceName)
			throws Exception {

		if (theServiceName == null) {
			theServiceName = "out";
		}
		if (serviceId.equals("blocks-core-io-csvimport-2")) {
			documentId = "211";
			DocumentRecord doc = api.getDocument(documentId);
			DocumentRecord wrapper = new DocumentRecord();
			wrapper.populateCopy(doc);
			Block.getEditableProperties().add("Source", doc);

		}
		
		if(serviceId.equals("blocks-core-io-importfile")){
			documentId = "879";
			DocumentRecord doc = api.getDocument(documentId);
			DocumentRecord wrapper = new DocumentRecord();
			wrapper.populateCopy(doc);
			Block.getEditableProperties().add("Source", doc);
		}

		if (serviceId.equals("blocks-core-io-csvexport")) {
			Block.getEditableProperties().add("FileName",
					theServiceName + ".csv");
		}
		
		if(serviceId.equals("blocks-core-io-exportfiles")){
			Block.getEditableProperties().add("FileName",
					theServiceName + ".jpeg");
		}
		
		
		drawing.addBlock(Block);
		BlockModelPosition p = new BlockModelPosition();
		p.setHeight(60);
		p.setWidth(60);
		p.setTop(100);
		p.setLeft(50 + b * 100);
		drawing.getDrawingLayout().addLocationData(Block, p);
	}
}
