package edu.harvard.hul.ois.pds.cache;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.configuration.XMLConfiguration;
import edu.harvard.hul.ois.pds.PdsConf;
import edu.harvard.hul.ois.pds.UnescapeHtml;
import edu.harvard.hul.ois.pds.user.PtoMetadata;
import edu.harvard.hul.ois.pdx.util.InternalMets;
import edu.harvard.hul.ois.util.AESUtil;
import edu.harvard.hul.ois.util.Encryptor;

public class CacheController {
	
	private ConcurrentHashMap<String, CacheItem> memCache;
	private ConcurrentLinkedQueue<String> activeStages;
	private File cacheDir;
	private XMLConfiguration conf;
	private boolean debug;
	private Encryptor cipher;
	
	public CacheController() {
		memCache = new ConcurrentHashMap<String, CacheItem>(100);
		activeStages = new ConcurrentLinkedQueue<String>();
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			PdsConf pdsConf = (PdsConf) envContext.lookup("bean/PdsConf");
			conf = pdsConf.getConf();
			debug = conf.getBoolean("CacheDebug",false);	
			System.out.println("PDS OBJECT CACHE INITIALIZED");
		} catch (NamingException e) {
			e.printStackTrace();
		}		
		
		//encryption for passing captions text throuh IDS url
		File key = new File(conf.getString("aeskey"));
		try {
			cipher = new AESUtil(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//create and start cache scrubber
		CacheScrubber scrubber = new CacheScrubber(conf,memCache);
		new Thread(scrubber).start();
	}
	
	public CacheItem getObject(PtoMetadata meta) {
		String id = String.valueOf(meta.getPtoID());
		Date drsDate = meta.getDBLastModified();
		//if memCache contains the requested object
		if(memCache.containsKey(id)) {
			CacheItem item = memCache.get(id);
			item.setLastAccessed(new Date());
			//check if memCache item is stale
			if(item.getLastModifiedDate() != null && item.getLastModifiedDate().before(drsDate)) {
				return stageFromDRS(meta); //restage it if it is
			}
			else {
				if(debug) {
					Date now = new Date();
					System.out.println(now.toString() + " - " + meta.getPtoID()+" in memory");
				}
				return item; // else just return it
			}
		}
		else {
			return stageFromDRS(meta);
		}

	}
	
	private CacheItem stageFromDRS(PtoMetadata meta) {
		String id = String.valueOf(meta.getPtoID());
		//If the requested object is already being staged then wait
		boolean waitedForStage = false;
		while(activeStages.contains(id)) {
			waitedForStage = true;
			if(debug) {
				Date now = new Date();
				System.out.println(now.toString() + " - " + meta.getPtoID()+" is being staged, waiting...");
			}
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		//If the request didn't have to wait for previous active staging requests then latest 
		// object will not be in the cache and will still need to be staged.  If the request
		// had to wait for a previous active staging request then the latest object will 
		// already be in the cache, thus it won't need to be staged again.
		if(!waitedForStage) { 
			CacheItem item = null;
			try {
				activeStages.add(id);
				String filepath = meta.getFilepath();
				InternalMets mets = null;
				mets = new InternalMets(filepath);
				mets.escapeAllHtmlChars();
				item = new CacheItem(meta.getPtoID(),mets,meta.getAccessFlag(), meta.getDBLastModified());
				
				//configure caption for images
		        String metsDisplayLabel = mets.getCitationDiv().getDisplayLabel();		   
		        String owner = null;
				if(mets.getCitationDiv().getpdfheader()==null) {
					owner = meta.getOwner();
				}
				else {
					owner = mets.getCitationDiv().getpdfheader();
				}
				try {
					metsDisplayLabel = UnescapeHtml.unescape(metsDisplayLabel,0);
			        String caption = "huloisHarvard University - "+owner+" / "+metsDisplayLabel;
			        //encrypt caption string 
			        caption = cipher.encrypt(caption);
			        caption = URLEncoder.encode(caption,"UTF-8");
			        item.setImageCaption(caption);			
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				memCache.put(id,item);
			}
			finally {
				activeStages.remove(id);	
			}
			if(debug) {
				Date now = new Date();
				System.out.println(now.toString() + " - " + meta.getPtoID()+" staged from drs to memory");
			}
			item.setLastAccessed(new Date());
			return item;
		}
		//else just return the object from the cache
		else {
			if(debug) {
				Date now = new Date();
				System.out.println(now.toString() + " - " + meta.getPtoID()+" has already been staged from drs");
			}
			CacheItem item = memCache.get(id);
			item.setLastAccessed(new Date());
			return item;
		}	
	}
	
	public String printState() {
		Collection<CacheItem> objs = memCache.values();
		String state = new String();
		int i = 1;
		for(CacheItem item : objs) {
			state = state + "<br>" + i + " - " + item.getId();
			i++;
		}
		return state;
	}

	public String getCacheDir() {
		return cacheDir.getPath();
	}

	public void setCacheDir(String cacheDir) {
		this.cacheDir = new File(cacheDir);
	}

}
