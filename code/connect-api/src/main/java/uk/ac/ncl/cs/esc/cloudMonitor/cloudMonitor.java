package uk.ac.ncl.cs.esc.cloudMonitor;

import java.util.LinkedList;
import java.util.Set;

import uk.ac.ncl.cs.esc.read.Cloud;

public interface cloudMonitor {
	
   public boolean isCloudAVA(String cloudName);
   public void  initAvaClouds(Set<Cloud> Clouds);
   boolean statuesChange(LinkedList<String> runningCloud);
   public LinkedList<String> getAvaClouds();
}
