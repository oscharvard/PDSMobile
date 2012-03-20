package edu.harvard.hul.ois.pds.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;

public class PdsUserState implements Serializable{

	private static final long serialVersionUID = 1L;
	private PtoMetadata meta;
	private boolean rAuthorized;
	private String action;
	private Integer ptoID;
	private Integer page;
	private int curZoom;
	private int curRotate;
	private Hashtable<String,List<String>> expandedStates;
	//private List<String> expandedNodes;
	
	public PdsUserState() {	
		meta = new PtoMetadata();
		rAuthorized = false;
		action = "";
		curZoom = 2;
		curRotate = 0;
		expandedStates = new Hashtable<String,List<String>>();
	}
	
	public PdsUserState(PtoMetadata meta) {
		this.meta = meta;
	}

	public PtoMetadata getMeta() {
		return meta;
	}

	public void setMeta(PtoMetadata meta) {
		this.meta = meta;
	}

	public boolean isRAuthorized() {
		return rAuthorized;
	}

	public void setRAuthorized(boolean authorized) {
		rAuthorized = authorized;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getPtoID() {
		return ptoID;
	}

	public void setPtoID(Integer ptoID) {
		this.ptoID = ptoID;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public int getCurZoom() {
		return curZoom;
	}

	public void setCurZoom(int curZoom) {
		this.curZoom = curZoom;
	}

	public int getCurRotate() {
		return curRotate;
	}

	public void setCurRotate(int curRotate) {
		this.curRotate = curRotate;
	}

	public void setExpandedNodes(Integer objId, List<String> expandedNodes) {
		expandedStates.put(objId.toString(), expandedNodes);
	}
	
	public void toggleNode(Integer objId, String node) {
		List<String> expandedNodes = getExpandedNodes(objId);
		boolean removed = false;
		
		ListIterator<String> iter = expandedNodes.listIterator();
		while(iter.hasNext()) {
			String n = iter.next();
			if(n.equals(node)) {
				iter.remove();
				removed = true;
				break;
			}
		}
		if(!removed) {
			expandedNodes.add(node);
		}
		expandedStates.put(objId.toString(), expandedNodes);
	}
	
	public boolean isNodeExpanded(Integer objId,String node) {
		List<String> expandedNodes = getExpandedNodes(objId);
		for(String n : expandedNodes) {
			if(n.equals(node)) {
				return true;
			}
		}
		return false;
	}
	
	private List<String> getExpandedNodes(Integer objId) {
		List<String> expandedNodes = expandedStates.get(objId.toString());
		if(expandedNodes == null) {
			expandedNodes = new ArrayList<String>();
			//Expand citation node by default
			expandedNodes.add("-1");
		}
		return expandedNodes;
	}

}
