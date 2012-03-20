package edu.harvard.hul.ois.pds.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.harvard.hul.ois.pdx.util.CitationDiv;
import edu.harvard.hul.ois.pdx.util.Div;
import edu.harvard.hul.ois.pdx.util.IntermediateDiv;
import edu.harvard.hul.ois.pdx.util.InternalMets;
import edu.harvard.hul.ois.pdx.util.PageDiv;

public class CacheItem {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private Date cachedTime;
	private Date lastAccessed;
	private Date lastModified;
	private String accessFlag;
	private String label;
	private InternalMets mets;
	private String imageCaption;
	
	public CacheItem(int id, InternalMets mets, String accessFlag, Date lastModified) {
		this.id = id;
		this.cachedTime = new Date();
		this.lastAccessed = new Date();
		//this.jdom = jdom;
		this.accessFlag = accessFlag;	
		this.mets = mets;
		this.lastModified = lastModified;
	}
	
	public int getId() {
		return id;
	}

	public Date getCachedTime() {
		return cachedTime;
	}

	public String getAccessFlag() {
		return accessFlag;
	}
	
	public Date getLastModifiedDate() {
		return lastModified;
	}
	
	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
	
	public Date getLastAccessed() {
		return lastAccessed;
	}

	public String getLabel() {
		return label;
	}

	public InternalMets getMets() {
		return mets;
	}

	public void setMets(InternalMets mets) {
		this.mets = mets;
	}
	
	public String getImageCaption() {
		return imageCaption;
	}
	
	public void setImageCaption(String caption) {
		imageCaption = caption;
	}
	
	public List<String> getAllNodesIndices() {
		List<String> allNodes = new ArrayList<String>();
		//for citation node
		allNodes.add("-1");
		CitationDiv cite = mets.getCitationDiv();
		int nodeCnt = 0;
		for(Div child : cite.getChildren()) {
			if(child instanceof IntermediateDiv) {
				allNodes.add(String.valueOf(nodeCnt));
				getSubNodeIndices((IntermediateDiv)child,String.valueOf(nodeCnt),allNodes);
			}
			nodeCnt++;
		}
		
		return allNodes;
	}
	
	private void getSubNodeIndices(IntermediateDiv inter, String curIndex, List<String> allNodes) {
		int subNodeCnt = 0;
		for(Div child : inter.getChildren()) {
			//if(child instanceof IntermediateDiv && ((IntermediateDiv)child).anyInterChildren())
                        if(child instanceof IntermediateDiv)
                        {
				String newIndex = curIndex+"."+String.valueOf(subNodeCnt);
				allNodes.add(newIndex);
				getSubNodeIndices((IntermediateDiv)child,newIndex,allNodes);
			}
			subNodeCnt++;
		}
	}
}
