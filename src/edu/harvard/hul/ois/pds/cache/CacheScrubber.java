package edu.harvard.hul.ois.pds.cache;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration.XMLConfiguration;

public class CacheScrubber implements Runnable {

	private int delay;
	private int maxage;
	private ConcurrentHashMap<String, CacheItem> cache;
	private boolean debug;

	public CacheScrubber(XMLConfiguration conf, ConcurrentHashMap<String, CacheItem> cache) {
		delay = conf.getInt("ScrubDelay",300);
		delay = delay *1000;//convert seconds to milliseconds
		maxage = conf.getInt("CacheMaxAge",60) * 1000; //convert seconds to milliseconds
		debug = conf.getBoolean("CacheDebug",false);	
		this.cache = cache;
	}
	
	public void run() {
		
		while(true) {
			try {
				Thread.sleep(delay);
				Collection<CacheItem> objs = cache.values();
				for(CacheItem item : objs) {
					Date currTime = new Date();
					if(currTime.getTime() - item.getLastAccessed().getTime() > maxage) {
						objs.remove(item);
						if(debug) {
							Date now = new Date();
							System.out.println(now.toString() + " - Ejected "+item.getId() + 
								". Age was: "+(currTime.getTime() - item.getLastAccessed().getTime()));
						}
	
					}
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
