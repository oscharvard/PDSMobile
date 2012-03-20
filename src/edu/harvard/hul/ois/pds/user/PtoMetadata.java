package edu.harvard.hul.ois.pds.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class PtoMetadata implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int ptoID;
	private Date dbLastModified;
	private String accessFlag;
	private String mime;
	private String filepath;
	private String owner;
	private String urn;
	private HashMap<String, String> urnMappings;
	
	public PtoMetadata() {
		
	}


	public int getPtoID() {
		return ptoID;
	}


	public void setPtoID(int ptoID) {
		this.ptoID = ptoID;
	}


	public Date getDBLastModified() {
		return dbLastModified;
	}


	public void setDBLastModified(Date lastModified) {
		this.dbLastModified = lastModified;
	}


	public String getAccessFlag() {
		return accessFlag;
	}


	public void setAccessFlag(String accessFlag) {
		this.accessFlag = accessFlag;
	}


	public String getMime() {
		return mime;
	}


	public void setMime(String mime) {
		this.mime = mime;
	}


	public String getFilepath() {
		return filepath;
	}


	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}


	public String getUrn() {
		return this.urn;
	}


	public void setUrn(String urn) {
		this.urn = urn;
	}


	public HashMap<String, String> getUrnMappings() {
		return this.urnMappings;
	}

	public void setUrnMappings(HashMap<String, String> urnMappings) {
		this.urnMappings = urnMappings;
	}
	
	public String getUrnFromList(String nValue) {
		String retVal = "";
		if (nValue == null || nValue.equals("") || nValue.equals("?n=1")) {
			retVal = getUrn();
		}
		else {
			for (String key:getUrnMappings().keySet()) {
				if (key.endsWith(nValue)) {
					retVal = getUrnMappings().get(key);
					break;
				}
				
			}
			if (retVal.equals("")) {
				retVal = getUrn() + nValue;
			}
		}
		return retVal;
	}
}
