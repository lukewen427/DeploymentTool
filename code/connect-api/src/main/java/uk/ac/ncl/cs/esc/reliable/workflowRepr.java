package uk.ac.ncl.cs.esc.reliable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import uk.ac.ncl.cs.esc.cloudMonitor.cloudMonitorIm;
import uk.ac.ncl.cs.esc.read.BlockSet;

import com.google.common.collect.HashBiMap;

public abstract class workflowRepr {

	public abstract ArrayList<ArrayList<String>> getConnections();

	public abstract HashBiMap<String, Integer> getMap();

	public abstract int[][] getDeployment();

	public abstract LinkedList<String> getAvaClouds();

	public abstract cloudMonitorIm getCloudinfo();

	public abstract HashMap<String, ArrayList<String>> getBlockInfo();

	public abstract ArrayList<String> getRootNodes();

	public abstract ArrayList<String> getLeafNodes();

	public abstract BlockSet getBlockSet();
}
