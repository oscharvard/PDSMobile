package edu.harvard.hul.ois.pds.index;

public class IndexItem implements Comparable<IndexItem>{
	
	private String objId;
	private String filepath;
	private String title;
	
	public IndexItem(String objId, String filepath) {
		this.objId = objId;
		this.filepath = filepath;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public int compareTo(IndexItem o) {
		String a = title;
		String b = o.getTitle();;
			
		//if there is no title compare using the object id
		if(title == null || title.equals("")) {
			a = objId;
		}
		if(b == null || b.equals("")) {
			b = o.getObjId();
		}
		return a.compareTo(b);
	}

}
