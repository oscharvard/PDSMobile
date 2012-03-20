package edu.harvard.hul.ois.pds.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import javax.naming.NamingException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import edu.harvard.hul.ois.pds.Utils;

public class IndexGenerator {
	

	private static final String OBJ_QUERY = 
		"select o.object_id, o.filepath "+
		"from drs_objects o, text_metadata t "+
		"where t.object_id = o.object_id "+
		"and o.access_flag='P' "+
		"and t.descriptor_type = 'PAGEDOBJECT'";
	private static final String USAGE = "IndexGenerator -c conf";
	
	public static void main(String[] args) throws SAXException, IOException, NamingException, ConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {		
		String confFile = null;
        for (int i=0; i<args.length; i++) {
		    if (args[i].equals ("-c")) {
		    	confFile = args[++i];
		    }
        }
        
        //check for valid parameter states
        if(confFile == null) {
        	System.err.println("\nInvalid command line parameters\n");
        	System.err.println("Usage:\n"+ USAGE);
	    	System.exit (-1);
        }
        
        XMLConfiguration conf = new XMLConfiguration(confFile);

        String DB_CONN = conf.getString("dbconnect");
	    String DB_DRIVER = conf.getString("dbdriver");
	    String USER = conf.getString("dbuser");
	    String PASS = conf.getString("dbpass");
	    int LINKS_PER_PAGE = conf.getInt("linksperpage");
	    String INDEX_DIR = conf.getString("indexDir");
	    String PDS_URL = conf.getString("pds");
	    String HTML_TEMPLATE = conf.getString("htmlTemplate");
	    
	    Connection conn = null;
        Class.forName(DB_DRIVER).newInstance();   	
        ArrayList<IndexItem> ptos = new ArrayList<IndexItem> ();
               
		PreparedStatement stmt = null; 
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(DB_CONN, USER, PASS);
			stmt = conn.prepareStatement(OBJ_QUERY);

			rs = stmt.executeQuery();
			while (rs.next()) {
				String filepath = rs.getString("filepath");
				String objectId = rs.getString("object_id");
				IndexItem item = new IndexItem(objectId,filepath);
				item.setTitle(objectId);
				ptos.add(item);
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			conn.close(); // Return to connection pool
			conn = null; // Make sure we don't close it twice
		} catch (SQLException e) {
			//TODO handle error
			e.printStackTrace();
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {;}
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {;}
				stmt = null;
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {;}
				conn = null;
			}

		}
		
		//TODO add all objects from DRS2
		
		System.setProperty("org.xml.sax.driver","org.apache.xerces.parsers.SAXParser");		
		XMLReader parser = null;
		parser = XMLReaderFactory.createXMLReader ();
		MetsTitleParser titleParser = new MetsTitleParser ();
		parser.setContentHandler (titleParser);
		
		for(IndexItem item : ptos) {
			String filepath = item.getFilepath();
			try {
				parser.parse (filepath);
			}
			catch(Exception e) {
				System.out.println("ERROR PARSING "+filepath);
				continue;
			}
			String title = titleParser.getDisplayLabel();
			if(title != null && !title.equals("")) {
				item.setTitle(Utils.escapeHtml(title.trim()));
			}		
		}
		
		Collections.<IndexItem>sort(ptos);
		
		//Queue<IndexItem> ptoStack = new LinkedList<IndexItem>();
		//ptoStack.addAll(ptos);
		
		int fileCnt = 1;
		while(!ptos.isEmpty()) {	
			//create new HTML Page
			File file;
			if(fileCnt == 1) {
				file = new File(INDEX_DIR+"/index.html");
			}
			else {
				file = new File(INDEX_DIR+"/index"+fileCnt+".html");
			}
			
			if(file.exists()) {
				file.delete();
			}
	        Writer out = new BufferedWriter(new FileWriter(file));
	        out.write(Utils.readFileAsString(HTML_TEMPLATE));
			for(int i=0;i<LINKS_PER_PAGE;i++) {
				if(ptos.isEmpty()) {
					fileCnt = -1;
					break;
				}
				//output links to HTML Page		
				IndexItem item = ptos.remove(0);
				
				//System.out.println(item.getObjId() + "=" + item.getFilepath() + " -- " + item.getTitle());
				out.write("<li><a href=\""+PDS_URL+"/view/"+item.getObjId()+"\">"+item.getTitle()+"</a></li>\n");
			}
			//close HTML file
			out.write("</ul>");
			getClosingHTML(out,fileCnt);
			out.close();	
			fileCnt++;
		}
		System.out.println("All done");
		

		
	}
	private static void getClosingHTML(Writer out, int fileNum) throws IOException  {
		if(fileNum > 0) {
			out.write("<br/><a href=\"index"+(fileNum+1)+".html\">Index Page "+(fileNum+1)+"</a>");
		}
		out.write("</body>\n</html>");
	}
		 
}
