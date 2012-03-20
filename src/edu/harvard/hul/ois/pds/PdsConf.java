package edu.harvard.hul.ois.pds;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
/**
 * Wrapper to create JNDI resource for commons XMLConfiguration
 * @author spencer
 *
 */
public class PdsConf {

	private String confPath;
	private XMLConfiguration xmlConf;
	
	public PdsConf() {
		System.out.println("PDSCONF init");
	}

	public void setConf(String conf) {
        try {
        	xmlConf = new XMLConfiguration(conf);
        }
        catch(ConfigurationException cex) {
            // something went wrong, e.g. the file was not found
        }
	}

	public XMLConfiguration getConf() {
		return xmlConf;
	}

	public void setConf(XMLConfiguration xmlConf) {
		this.xmlConf = xmlConf;
	}

	public String getConfPath() {
		return confPath;
	}

	public void setConfPath(String confPath) {
		this.confPath = confPath;
		setConf(confPath);
	}
}
