package uk.ac.ncl.cs.esc.workflow;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.connexience.api.StorageClient;

public interface deployWF {
public HashMap<String,String> fileUpload(HashMap<String, ByteArrayOutputStream> theresults,StorageClient Sclient)throws Exception;
}
