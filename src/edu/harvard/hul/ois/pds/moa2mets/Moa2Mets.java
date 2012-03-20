/**********************************************************************
 * MOA2-to-METS converter
 * Copyright 2004 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 * Contact information
 *
 * Office for Information Systems
 * Harvard University Library
 * Harvard University
 * Cambridge, MA  02138
 * (617)495-3724
 * hulois@hulmail.harvard.edu
 **********************************************************************/

package edu.harvard.hul.ois.pds.moa2mets;

import edu.harvard.hul.ois.app.*;
import edu.harvard.hul.ois.mets.*;
import edu.harvard.hul.ois.mets.helper.*;
import edu.harvard.hul.ois.xml.*;
import java.io.*;
import java.sql.*;
import javax.xml.parsers.*;
import java.util.List;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import java.util.Hashtable;

/**
 * Convert MOA2 object trees to METS files.
 *
 * Usage:
 * <pre>
 *   java Moa2Mets [-hv] [-c <em>config</em> <em>id</em>
 *   -h          Display help information
 *   -v          Display version information
 *   -c <em>config</em>   Configuration file
 *   -o <em>output</em>   Output file (defaults to standard output)
 *   -m <em>mode</em>     Set mode.  DRS to lookup moa2's in the DRS or local for local file system.
 *   <em>id/filename</em> MOA2 citation node ID, or the local citaion moa2 filename
 *   <em>path</em>        If the mode is local, the path to the filename needs to be specified.
 * </pre>
 *
 * @author: Stephen Abrams/Spencer McEwen
 * @version: 1.1 2005-01-07
 * @version: 1.0 2004-09-01
 *
 * 2005-01-07 SM:  Added better support for dates and the option
 *                 for local file conversions
 */
public class Moa2Mets
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    private static String _select =
	"SELECT filepath FROM drs_objects WHERE object_id=?";
	private static String localFileName = null;
	private static String rootPath      = null;
	private static String mode          = null;
	private static boolean jp2 			= false;

    /******************************************************************
     * PUBLIC CLASS Variables.
     ******************************************************************/
	public static Hashtable oasisHash = new Hashtable();
	
	  /**
     * Main entry point.
     * @parm args Command line arguments
     */
    public static void main (String [] args)
    {
	/**************************************************************
	 * Initialize the application.
	 **************************************************************/
	Application app = new Application ("Moa2Mets");
	app.setBuildDate  ("2005-01-07");
	app.setConfigFile ("moa2mets.conf");
	app.setUsage      ("java Moa2Mets [-hv] [-c config] [-j (jp2/jpeg)] [-o output] [-m mode(drs/local)] [-g yes] id/filename [path]");
	app.setVersion    ("1.1");
		
	String password   = "******";
	String uri        = "jdbc:oracle:thin:@cool.hul.harvard.edu:1521:DEV";
	String username   = "repository";

	String saxParser  = "org.apache.xerces.parsers.SAXParser";
	String jdbcDriver = "oracle.jdbc.OracleDriver";
	long   moa2Id     = 0;
	String filepath   = null;
	Connection conn   = null;

	try {
	    /**********************************************************
	     * Parse the command line and the configuration file.
	     **********************************************************/
	
	    app.parseCommandLine (args, "m:o:c:g:j:");
	    app.parseConfigFile ();
		boolean validate = false;
		if(app.getOption('g') != null && app.getOption('g').equals("yes")) {
			validate = true;
		}
		
		//Get the jp2 flag
		if(app.getOption('j') != null && app.getOption('j').equals("jp2")) {
			jp2 = true;
		}
		else if (app.getOption('j') != null && app.getOption('j').equals("jpeg")) {
			jp2=false;
		}
		
		//Get the Oasis Hashtable for URN Authority Path
		oasisHash = app.getConfigHash("oasisLocCode");

		/**********************************************************
	    * Set local or drs mode.
	    **********************************************************/
		mode = app.getOption ('m');
	    if (mode==null || !(mode.equals("drs") || mode.equals("local"))) {
		throw new ApplicationException ("no mode set. Specify drs or local",
						"command line",
						Application.ESYNTAX, true);
	    }
			
			
	    /**********************************************************
	     * The MOA2 ID is a required argument.
	     **********************************************************/

		if(mode.equals("drs")) {
			String moa2 = app.getArgument (1);
			
			if (moa2 == null) {
			throw new ApplicationException ("no MOA2 ID",
							"command line",
							Application.ESYNTAX, true);
			}
			try {
			moa2Id = Long.parseLong (moa2);
			}
			catch (NumberFormatException e) {
			throw new ApplicationException ("MOA2 ID not numeric",
							moa2, Application.ESYNTAX,
							true);
			}
		}
		else {
			//mode is local
			localFileName = app.getArgument (1);
			rootPath = app.getArgument(2);
			if(rootPath==null) {
				throw new ApplicationException ("When in 'local' mode the local file path must be set",
							rootPath + "/" + localFileName, Application.ESYNTAX,
							true);
			}
			java.io.File localFile = new java.io.File(rootPath + "/" + localFileName);
			if(!localFile.exists()) {
				throw new ApplicationException ("File," + localFileName +", does not exist",
							rootPath + "/" + localFileName, Application.ESYNTAX,
							true);
			}
		}
		
	    /**********************************************************
	     * Open the output stream.
	     **********************************************************/

	    OutputStream out = null;
	    String output = app.getOption ('o');
	    if (output != null) {
		out = new FileOutputStream (output);
	    }
	    if (out == null) {
		out = System.out;
	    }
		
	    /**********************************************************
	     * Get a SAX2 parser.
	     **********************************************************/

	    SAXParserFactory factory = SAXParserFactory.newInstance ();
	    factory.setNamespaceAware (true);
	    factory.setValidating (true);
	    SAXParser parser = factory.newSAXParser ();

	    /**********************************************************
	     * Load the JDBC driver.
	     **********************************************************/
		if(mode.equals("drs")) {
			jdbcDriver = app.getConfig ("jdbcDriver", jdbcDriver);
			try {
				Class.forName (jdbcDriver).newInstance ();
			}
			catch (Exception e) {
				throw new ApplicationException ("can't load JDBC driver",
							jdbcDriver,
							Application.EDRIVER);
			}
	
			/**********************************************************
			 * Open JDBC connections to the Indexing schemas.
			 **********************************************************/
	
			uri      = app.getConfig ("uri",      uri);
			username = app.getConfig ("username", username);
			password = app.getConfig ("password", password);
			try {
				conn = DriverManager.getConnection (uri, username, password);
				PreparedStatement st = conn.prepareStatement (_select);
		
				st.clearParameters ();
				st.setLong (1, moa2Id);
				ResultSet rs = st.executeQuery ();
		
				if (rs.next ()) {
					filepath = rs.getString ("filepath");
				}
				else {
					throw new ApplicationException ("ID not found",
									Long.toString (moa2Id),
									Application.EACCESS);
				}
				rs.close ();
				st.close ();
			}
			catch (SQLException e) {
				throw new ApplicationException (e.getSQLState () + "[" +
							e.getErrorCode () + "]: " +
							e.getMessage (),
							username + "/" + password +
							"@" + uri,
							Application.ECONNECT,
							"Moa2Fts.main()");
			}
			}
			try {
				Mets mets = new Mets ();
				mets.setTYPE ("PAGEDOBJECT");
				mets.setPROFILE ("HUL");
				mets.setSchema("pds","http://hul.harvard.edu/ois/xml/ns/pds","http://hul.harvard.edu/ois/xml/xsd/pds/pds.xsd");
		
				Moa2Handler handler = new Moa2Handler ();
				handler.setFactory (factory);
				handler.setJp2Mode(jp2);
				handler.setMets (mets);
				if(mode.equals("drs")) {
					handler.setConnection (conn);
					handler.setMode("drs", String.valueOf(moa2Id));
					if(output != null) {
						handler.setFileoutputPath(output.substring(0,output.lastIndexOf("/")));
					}
					else {
						handler.setFileoutputPath(null);
					}
				}
				else {
					//Mode must be local
					filepath = rootPath + "/" + localFileName;
					handler.setMode("local");
					handler.setRootPath(rootPath);
				}

				parser.parse (filepath, handler);

				//Add the <fileSec> and <fileGrp> sections
				List jpegList = handler.getJpegList();
				List tiffList = handler.getTiffList();
				List textList = handler.getTextList();
				FileSec fileSec = new FileSec();
		
				if(jpegList.size()>0) {
					FileGrp jpegGrp = new FileGrp();
					if(jp2) {
						jpegGrp.setID("image-jp2");
					}
					else {
						jpegGrp.setID("image-jpeg");
					}
					for(int i=0;i<jpegList.size();i++) {
						jpegGrp.getContent().add(jpegList.get(i));
					}
					fileSec.getContent().add(jpegGrp);
				}
				if(textList.size()>0) {
					FileGrp textGrp = new FileGrp();
					textGrp.setID("text-plain");
					for(int i=0;i<textList.size();i++) {
						textGrp.getContent().add(textList.get(i));
					}
					fileSec.getContent().add(textGrp);
				}
				if(tiffList.size()>0) {
					FileGrp tiffGrp = new FileGrp();
					tiffGrp.setID("image-tiff");
					for(int i=0;i<tiffList.size();i++) {
						tiffGrp.getContent().add(tiffList.get(i));
					}
					fileSec.getContent().add(tiffGrp);
				}
				mets.getContent().add(handler.lastCitationIndex(),fileSec);
				
				//Add the <DMDSEC> sections
				List dmdList = handler.getDmdList();
				for(int l=0; l<dmdList.size(); l++) {
					DmdSec dmdsec = new DmdSec();
					String [] dates = (String[])dmdList.get(l);
					dmdsec.setID(String.valueOf("I"+l));
					MdWrap mdWrap = new MdWrap ();
					mdWrap.setMDTYPE (Mdtype.OTHER);
					mdWrap.setOTHERMDTYPE("HULPDS");
					XmlData xmlData = new XmlData ();
					
					Any pds     = new Any ("pds:pds");
					Any pdsdate = new Any("pds:date");
					
					if(dates[0]!=null) {
						Any pdsfrom = new Any("pds:fromDate", dates[0]);
						pdsdate.getContent().add(pdsfrom);
					}
					if(dates[1]!=null) {
						Any pdsto   = new Any("pds:toDate", dates[1]);
						pdsdate.getContent().add(pdsto);
					}
					pds.getContent().add(pdsdate);
					xmlData.getContent().add(pds);
					mdWrap.getContent ().add (xmlData);
					dmdsec.getContent ().add (mdWrap);
					mets.getContent().add(l+handler.lastCitationIndex(),dmdsec);
				}
				
							
METSHandler mh = new METSHandler();
XMLReader metsparser = (XMLReader)Class.forName(saxParser).newInstance();
metsparser.setContentHandler(mh);
metsparser.setErrorHandler(mh);
// this is a validating parser
metsparser.setFeature("http://xml.org/sax/features/validation",true);
metsparser.setFeature("http://apache.org/xml/features/validation/schema",true);

				 
				mets.write (new MetsWriter (out));
				if(output!=null && validate) {
					metsparser.parse(output);
				}
			}
			catch (Exception e) {
				System.out.println("SAX Error");
				e.printStackTrace (System.err);
				app.setExitCode (Application.ERROR);
				app.setExitMessage (e.getMessage ());
				throw new ApplicationException (
							e.getMessage (),
							username + "/" + password +
							"@" + uri,
							Application.ECONNECT,
				 "Moa2Fts.main()");
				
			}
			
			finally {
				 /******************************************************
				 * Close the open JDBC connections
				 ******************************************************/
				try {
					if(mode.equals("drs")) {
						conn.close ();
					}
				}
				catch (SQLException e) {}
				}
	}

	/**************************************************************
	 * Catch any application-specific exceptions.
	 **************************************************************/

	catch (ApplicationException e) {
	    app.setExitCode (e.getCode ());
	    app.setExitMessage (e.getMessage ());
	    app.setExitUsage (e.getUsage ());
	}

	/**************************************************************
	 * Catch any unspecified exceptions.
	 **************************************************************/

	catch (Exception e) {
	    e.printStackTrace (System.err);
	    app.setExitCode (Application.ERROR);
	    app.setExitMessage (e.getMessage ());
	}

	/**************************************************************
	 * Terminate the application, returning an exit code, and,
	 * optionally, a written exit message and/or a usage syntax.
	 **************************************************************/

	finally {
	    app.exit ();
	}
    }
}
