/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jcg155
 */
package edu.harvard.hul.ois.pds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.mail.*;
import javax.mail.internet.*;
import javax.sql.DataSource;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.configuration.XMLConfiguration;
import org.xml.sax.SAXException;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

//import edu.harvard.hul.ois.drs2.callservice.FileMetadata;
//import edu.harvard.hul.ois.drs2.callservice.ServiceResponse;
import edu.harvard.hul.ois.drs2.callservice.ServiceWrapper;
import edu.harvard.hul.ois.drs2.services.dto.ext.DRSFileDTOExt;
import edu.harvard.hul.ois.pdx.util.InternalMets;

public class PdfAsynchGenerator {
    
        //private HttpServletRequest req;
        private PdfAsynchTicket ticket;
	private InternalMets mets;
	private String dropbox;
	private Integer id;
	private String idsUrl;
	private DataSource dataSource;
	
	private XMLConfiguration conf;
	private ServiceWrapper drs2Service;
        
	/*public PdfAsynchGenerator(Integer id, XMLConfiguration conf, InternalMets mets, BasicDataSource dataSource) throws IOException, SAXException{
		this.req = req;
		this.mets = mets;
	    dropbox = conf.getString("dropbox");
	    idsUrl = conf.getString("ids");
	    this.dataSource = dataSource;
	    this.id = id;
	    this.conf = conf;


		drs2Service = new ServiceWrapper(conf.getString("drs2ServiceURL"),conf.getString("drs2AppKey"));
	}
	
	public static File generate(HttpServletRequest req, Integer id,XMLConfiguration conf, InternalMets mets, BasicDataSource dataSource) throws Exception {
		PdfAsynchGenerator pdfGen = new PdfAsynchGenerator(id,conf,mets,dataSource);
		return pdfGen.generate();
	}*/
        
        
        public PdfAsynchGenerator(PdfAsynchTicket t, XMLConfiguration conf, DataSource dataSource) throws IOException, SAXException{

            this.mets = t.getMets();
            this.ticket = t;    
	    dropbox = conf.getString("dropbox");
	    idsUrl = conf.getString("ids");
	    this.dataSource = dataSource;
	    this.id = t.getId();
	    this.conf = conf;


		drs2Service = new ServiceWrapper(conf.getString("drs2ServiceURL"),
		        conf.getString("drs2AppKey"),
		        1);
	}
        
        public static File generate(PdfAsynchTicket ticket, XMLConfiguration conf, DataSource dataSource) throws Exception {
		PdfAsynchGenerator pdfGen = new PdfAsynchGenerator(ticket,conf,dataSource);
		return pdfGen.generate();
	}
        
	
	public File generate() throws Exception {	
		String start=null;
		String end=null;
		//String printOpt = req.getParameter("printOpt");
		//String n = req.getParameter("n");
                String printOpt = ticket.getPrintOpt();
		String n = ticket.getN();
		
		if(printOpt==null || printOpt.length()<1) {
			throw new Exception("Invalid Print Option");
		}
		
		try	{
			if(printOpt.equals("all")) {
				start="1";
				end=String.valueOf(mets.getCitationDiv().getLastPageNumber());
			}
			else if(printOpt.equals("range")) {
				//start = req.getParameter("start");
				//end = req.getParameter("end");
                                start = ticket.getStart();
				end = ticket.getEnd();
			}
			//printOpt must be "current"
			else {
				start=n;
				end=n;
			}
			
			/* If the end is less than the start, or if the end is greater than the largest possible end value */
			if(Integer.parseInt(end) < Integer.parseInt(start) ||
			   Integer.parseInt(end) > mets.getCitationDiv().getLastPageNumber()) {
				throw new Exception("Invalid Page Range");
			}
		}
		catch(NumberFormatException e)	{
			throw new Exception("Invalid Page Range",e);
		}
		
		// Set the pdf output file name
		String pdf = dropbox + File.separator + id.toString() + "-" + start + "-" +
					 end + ".pdf";
		

		// If the pdf file does not exist - preview requests do not
		// 	get cached.  They get recreated each time.
		 
		File pdffile = new File (pdf);
		if (!pdffile.exists ()) {
			
			String guid = UUID.randomUUID().toString();
			String guidPdf = dropbox + File.separator + id.toString() +
							"-" + start + "-" +	end + "."+ guid +".pdf";
			
			List printData = mets.getPrintData(mets.getCitationDiv(),Integer.parseInt(start),Integer.parseInt(end), new ArrayList(), false);
			String footerText = (String)printData.get(0);
			footerText = UnescapeHtml.unescape(footerText,0);
			ArrayList<String> files = new ArrayList<String>();
			/* Loop through all ID's, look up filepath and add image to filename string */
			String pdfImage = null;
			Connection conn = dataSource.getConnection();
			Statement stmt = null;
			String owner = "";
			try	{
				for(int i = 1; i<printData.size(); i++)	{					
					stmt = conn.createStatement();
					ResultSet rset = stmt.executeQuery("SELECT filepath FROM drs_objects WHERE object_id="+((Integer)printData.get(i)).intValue());
					if (!conf.getBoolean("useOnlyDRS2") && rset.next()) {
						pdfImage = rset.getString ("filepath");
						files.add(pdfImage);
					}
					else if(conf.getBoolean("useDRS2")) {
					    DRSFileDTOExt fmd = drs2Service.getFileMetadataById(String.valueOf(printData.get(i)),false);
						files.add(fmd.getFilePath());
					}
				}		
				if(mets.getCitationDiv().getpdfheader()==null) {
					stmt = conn.createStatement();
					ResultSet rset = stmt.executeQuery(
						"select FULL_NAME from DRS_OBJECTS, OWNERS"+
						" WHERE object_id="+id+" AND"+
						" DRS_OBJECTS.OWNER_CODE=OWNERS.OWNER_CODE");
					if (!conf.getBoolean("useOnlyDRS2") && rset.next()) {
						owner = rset.getString("FULL_NAME");
					}
					else if(conf.getBoolean("useDRS2")){
						//TODO set owner using drs2Service
					}
					
				}
				else {
					owner = mets.getCitationDiv().getpdfheader();
				}
			}
			catch (SQLException e) {
				throw new Exception(e);
			}
			finally {
				try{
					if(stmt != null) {
						stmt.close();
					}
				} 
				catch(SQLException e) {
					throw new Exception(e);
				}
				// The connection is returned to the Broker
				if (conn != null) {try { conn.close(); } catch (Exception e) { }}
			}

			DiacriticHash hash = new DiacriticHash();
			/* Configure String Characters for output */
			StringBuffer b = new StringBuffer();
			int j;
			for(j=0; j < footerText.length(); j++) {
				char c = footerText.charAt(j);
				if( c < 0x7f) {
					b.append(c);
				}
				else {
					Character ch = hash.getChar(c);
					if(ch != null) {
						b.append(ch.charValue());
					}
					else {
						b.append('?');
					}
				}
			}
			footerText = "Harvard University - "+owner+" / "+b.toString();
		
		    try {		    	
		    	String tmpPdf = guidPdf+".tmp";		    	
		    	Document tmpDocument = new Document(new Rectangle(100,100),0,0,0,0);
		    	Document document = new Document(new Rectangle(100,100),5,5,0,5);	    	 
		    	PdfWriter.getInstance(tmpDocument, new FileOutputStream(tmpPdf));    	        
		        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(guidPdf));
		        writer.setViewerPreferences(PdfWriter.PageModeUseThumbs);
		        
		    	for(int i=0;i<files.size();i++) {
		    		String image = files.get(i);
		    		
		    		String filename = image.substring(image.lastIndexOf('/')+1,image.length());
		    		String[] filenameparts = filename.split("\\.");
		    		String id = filenameparts[0];
		    		String ext  = filenameparts[1];
			    	
			    	if(!ext.equalsIgnoreCase("txt")) {
			    		Image img = null;
			    		
			    		//if image is a jp2
			    		if(ext.equalsIgnoreCase("jp2")) {
			    			//xcap parameter of a single space is set to suppress the IDS default caption.
			    			//img = Image.getInstance(idsUrl+"/"+id+"?height=2400&width=2400&xcap=mx%2BH1zMK5j7hx82zCIFrFkzkAxhZwK5bitNuD10fe4k%3D");
			    			//img = Image.getInstance(idsUrl+"/"+id+"?height=2400&width=2400&usecap=no");
                                            
			    			//judaica fix- jp2s used to be set at 1200px res, but limit it down if maxres set lower.
                                                int maxJP2Res = ticket.getMaxJP2Res();
                                                if ( maxJP2Res < 1200 )
                                                {
                                                    img = Image.getInstance(idsUrl+"/"+id+"?height=" + Integer.toString(maxJP2Res) + "&width=" + Integer.toString(maxJP2Res) + "&usecap=no");
                                                }
                                                else
                                                {
                                                    img = Image.getInstance(idsUrl+"/"+id+"?height=1200&width=1200&usecap=no");
                                                }
			    		}
			    		else {
			    			img = Image.getInstance(image);
			    		}
			    		
			    		//Calculate font size based on image size
			    		float fontSize = 12;		        
				        if(img.height() < 750 && img.width() < 750) {
				        	fontSize = 8;
				        }
				        else if (img.height()>img.width()) {
				        	fontSize = img.height()/110;
				        }
				        else {
				        	fontSize = img.width()/110;
				        }
				        
			    		Font font = new Font(Font.HELVETICA, fontSize, Font.NORMAL);
				    	HeaderFooter footer = new HeaderFooter(new Phrase(footerText,font), false);
				    	footer.setBorderWidthTop(0);
				    	
				    	//Calculate what the footer height will be on the page.
					    tmpDocument.setFooter(footer);
				    	tmpDocument.setPageSize(new Rectangle(img.width(),img.height()));
				    	//This will cause the footer height to be calculated.
				        tmpDocument.open();
				        float footerHeight = footer.height();
				        
				        //Create the real page with room for the footer beneath the image
				        document.setPageSize(new Rectangle(img.width(),img.height()+footerHeight));
				        document.setFooter(footer);		
				        document.open();		                       
				        img.setAbsolutePosition(0,footerHeight);
				        document.add(img);	
				        document.newPage();
			    	}
			    	else {
			    		//Create footer
			    		Font font = new Font(Font.HELVETICA, 8, Font.NORMAL);
				    	HeaderFooter footer = new HeaderFooter(new Phrase(footerText,font), false);
				    	footer.setBorderWidthTop(0);
			    		
			    		//Calculate what the footer height will be on the page.
					    tmpDocument.setFooter(footer);
					    Rectangle pagesize = PageSize.LETTER;
				    	tmpDocument.setPageSize(pagesize);
				    	//This will cause the footer height to be calculated.
				        tmpDocument.open();
				        float footerHeight = footer.height();
				        
				        //Create the real page with room for the footer beneath the image
				        document.setPageSize(new Rectangle(pagesize.width(),pagesize.height()+footerHeight));
				        document.setFooter(footer);		
				        document.open();		                       
				        //add contents of text file to pdf
				        try {
				        	String fileContents = getContents(new File(image));
					        document.add(new Paragraph(fileContents));
					        document.newPage();
				        }
				        catch(Exception e) {
					        document.add(new Paragraph("Unable to read text file contents"));
					        document.newPage();	
				        }
				        
			    	}
		    	}
		    	tmpDocument = null;
		    	File tmpPdfFile = new File(tmpPdf);
		    	tmpPdfFile.delete();
			    document.close();
		    	writer.close();

			    
			    File guidPdfFile = new File(guidPdf);
			    guidPdfFile.renameTo(pdffile);
			    
		    }
		    catch(Exception e) {
		    	pdffile.delete();
		    	throw e;
		    }
		}
		return new File(pdf);                
	}
	/**
	  * Fetch the entire contents of a text file, and return it in a String.
	  * This style of implementation does not throw Exceptions to the caller.
	  *
	  * @param aFile is a file which already exists and can be read.
	 * @throws Exception 
	  */
	  static public String getContents(File aFile) throws Exception {
	    StringBuffer contents = new StringBuffer();
	    BufferedReader input = null;
	    try {
	      input = new BufferedReader( new FileReader(aFile) );
	      String line = null; //not declared within while loop
	      while (( line = input.readLine()) != null){
	        contents.append(line);
	        contents.append(System.getProperty("line.separator"));
	      }
	    }
	    catch (Exception e) {
	      throw e;
	    }
	    finally {
	      try {
	        if (input!= null) {
	          input.close();
	        }
	      }
	      catch (IOException ex) {}
	    }
	    return contents.toString();
	  }	

}
