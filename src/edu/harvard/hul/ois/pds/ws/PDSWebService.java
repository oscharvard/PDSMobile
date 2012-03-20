/**********************************************************************
 * Page Delivery Service Web Service (PDS-WS) servlet
 * Copyright 2011 by the President and Fellows of Harvard College
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

package edu.harvard.hul.ois.pds.ws;

import edu.harvard.hul.ois.xml.WebAppLogMessage;
import edu.harvard.hul.ois.xml.XmlLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Collections;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.DailyRollingFileAppender;
//import org.apache.commons.dbcp.BasicDataSource;

//import edu.harvard.hul.ois.drs2.callservice.FileMetadata;
import edu.harvard.hul.ois.drs2.callservice.ServiceException;
//import edu.harvard.hul.ois.drs2.callservice.ServiceResponse;
import edu.harvard.hul.ois.drs2.callservice.ServiceWrapper;
import edu.harvard.hul.ois.drs2.services.dto.ext.DRSFileDTOExt;
import edu.harvard.hul.ois.pds.cache.CacheController;
import edu.harvard.hul.ois.pds.cache.CacheItem;
import edu.harvard.hul.ois.pds.exceptions.TooManyPDFConversions;
import edu.harvard.hul.ois.pds.ids_util.Coordinate;
import edu.harvard.hul.ois.pds.ids_util.CoordinatePair;
import edu.harvard.hul.ois.pds.ids_util.Jpeg2000;
import edu.harvard.hul.ois.pds.ids_util.UIUtil;
import edu.harvard.hul.ois.pds.user.PdsUserState;
import edu.harvard.hul.ois.pdx.util.CitationDiv;
import edu.harvard.hul.ois.pdx.util.Div;
import edu.harvard.hul.ois.pdx.util.IntermediateDiv;
import edu.harvard.hul.ois.pdx.util.InternalMets;
import edu.harvard.hul.ois.pdx.util.PageDiv;
import edu.harvard.hul.ois.pdx.util.UnicodeUtils;

import java.net.URLEncoder;
import edu.harvard.hul.ois.pds.*;



/**
 *
 * @author jcg155
 */
public class PDSWebService extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static final String ACTION_VIEW   = "view";
    private static final String ACTION_VIEW_TEXT   = "viewtext";
    private static final String ACTION_PRINT  = "print";
    private static final String ACTION_PRINTOPS  = "printoptions";
    private static final String ACTION_SEARCH = "search";
    private static final String ACTION_LINKS  = "links";
    private static final String ACTION_CITE_INFO = "fullcitation";

    //API verbs
    private static final String API_VIEW   = "get";
    private static final String API_VIEW_TEXT   = "getocr";
    private static final String API_PRINT  = "print";
    private static final String API_PRINTOPS  = "printoptions";
    private static final String API_SEARCH = "find";
    private static final String API_LINKS  = "related";
    private static final String API_CITE_INFO = "cite";
    private static final String API_DOC_TREE = "toc";
    private static final String API_MAX_SECTIONS = "getmaxsections";
    private static final String API_MAX_PAGES = "getmaxpages";
    private static final String API_MAX_CHAPTERS = "getmaxchapters";

    /** Citation frame operation. */
    private static final char OP_CITATION   = 'c';
    /** Content frame operation. */
    private static final char OP_CONTENT    = 't';
    /** Navigation frame operation. */
    private static final char OP_NAVIGATION = 'n';

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

	/** Cache directory. */
    private String cache;

    /** Base URI of IDS. */
    private String idsUrl;

    /** Base URI of FTS. */
    private String ftsUrl;

    /** TIFF-to-GIF utility pathname. */
    private String giffy;

    /** URL for temp images/pdf location */
    private String cacheUrl;

	/**Logger for access, warn and error messages*/
	private Logger logger;

	/** Url of PDS */
	private String pdsUrl;

	/** Url of NRS, abstracted from ftsUrl **/
	private String nrsUrl;

        /** disable thumbnails for docs over a certain size */
        private String maxThumbnails;

        /** Database data source */
        private DataSource ds;

        private XMLConfiguration conf = null;
	private CacheController memcache;

	private Hashtable<String,ArrayList<Integer>> pdfConversions;

	private ServiceWrapper drs2Service;



    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Initialize the servlet.
     */

    public void init (ServletConfig config)	throws ServletException 
    {
        super.init (config);

		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			PdsConf pdsConf = (PdsConf) envContext.lookup("bean/PdsConf");
			conf = pdsConf.getConf();
			memcache = (CacheController) envContext.lookup("bean/CacheController");
			ds = (DataSource) envContext.lookup("jdbc/DrsDB");
		} catch (NamingException e) {
			e.printStackTrace();
		}

		cache             = conf.getString("cache");
		idsUrl            = conf.getString("ids");
		ftsUrl            = conf.getString("fts");
		giffy             = conf.getString("t2gif");
		cacheUrl          = conf.getString("cacheUrl");
		pdsUrl 	  		  = conf.getString("pds");
		nrsUrl 	  		  = conf.getString("nrsUrl");
		String logFile    = conf.getString("logFile");

                maxThumbnails = conf.getString("maxThumbnails");

		//Configure the logger
		logger = Logger.getLogger("edu.harvard.hul.ois.pds.ws");
		try {
			XmlLayout myLayout = new XmlLayout();
			//an appender for the access log
			Appender myAppender = new DailyRollingFileAppender(myLayout, logFile, conf.getString("logRollover"));
			logger.addAppender(myAppender);
		}
	    catch (Exception e) {
			WebAppLogMessage message = new WebAppLogMessage();
            message.setContext("init logger");
            message.setMessage("Error initializing logger");
            logger.error(message,e);
	    	throw new ServletException(e.getMessage());
		}
	    //reset the logger for this class
        logger = Logger.getLogger(PDSWebService.class);

		System.setProperty("org.xml.sax.driver","org.apache.xerces.parsers.SAXParser");

		//init pdf conversions hash
		pdfConversions = new Hashtable<String,ArrayList<Integer>>();

		//Log successful servlet init
		WebAppLogMessage message = new WebAppLogMessage();
		message.setMessage("Servlet init()");
		logger.info(message);

		drs2Service = new ServiceWrapper(conf.getString("drs2ServiceURL"),
		        conf.getString("drs2AppKey"),
		        1);


    }


    /**
     * Process HTTP POST request.
     */
    public void doPost (HttpServletRequest req, HttpServletResponse res) throws IOException {
    	doGet (req, res);
    }

    /**
     * Process HTTP GET request.
    */
    public void doGet (HttpServletRequest req, HttpServletResponse res)
    						throws IOException, FileNotFoundException {

    	HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(req);


		//Get parameters from the URL
		String sOp = req.getParameter ("op");
		char op = 'f';
		if (sOp != null) {
		    op = sOp.charAt (0);
		}

		String uri = req.getRequestURI();
		uri = uri.substring(1);
        String[] uriElements = uri.split("/");
        Integer id = null;
        String action = null;
        if(uriElements.length > 2) {
                action = uriElements[1];
                if ( action.equalsIgnoreCase(API_SEARCH) )  //go straight to fts
                {
                    String docParam;
                    if ( (uriElements[2]).equalsIgnoreCase("global") )
                    {
                        docParam = "";
                    }
                    else
                    {
                        try {id = new Integer(uriElements[2]);}
                        catch (Exception e)
                        {
                            printError(req,res,"Invalid DRS ID",null);
                            return; 
                        }
                        docParam = "G=" + id + "&";
                    }
                    String format = "";
                    format = req.getParameter("F");
                    if (format != null)
                    {
                        format = "&F=" + format;
                    }
                    else
                    {
                        format = "&F=M";
                    }
                    String range = "";
                    String advparams = "";
                    range = req.getParameter("B");
                    if (range != null)
                    {
                        range = "&B=" + range;
                    }
                    else
                    {
                        range = "";
                    }
                    String dataQualification = "";
                    dataQualification = req.getParameter("D");
                    if (dataQualification != null)
                    {
                        dataQualification = "&D=" + dataQualification;
                    }
                    else
                    {
                        dataQualification = "";
                    }
                    String contextScope = "";
                    contextScope = req.getParameter("C");
                    if (contextScope != null)
                    {
                        contextScope = "&C=" + contextScope;
                    }
                    else
                    {
                        contextScope = "";
                    }
                    String jumplistRefs = "";
                    jumplistRefs = req.getParameter("J");
                    if (jumplistRefs != null)
                    {
                        jumplistRefs = "&J=" + jumplistRefs;
                    }
                    else
                    {
                        jumplistRefs = "";
                    }
                    String lowerDate = "";
                    lowerDate = req.getParameter("L");
                    if (lowerDate != null)
                    {
                        lowerDate = "&L=" + lowerDate;
                    }
                    else
                    {
                        lowerDate = "";
                    }
                    String upperDate = "";
                    upperDate = req.getParameter("U");
                    if (upperDate != null)
                    {
                        upperDate = "&U=" + upperDate;
                    }
                    else
                    {
                        upperDate = "";
                    }
                    String resultsize = "";
                    resultsize = req.getParameter("P");
                    if (resultsize != null)
                    {
                        resultsize = "&P=" + resultsize;
                    }
                    else
                    {
                        resultsize = "";
                    }
                    advparams = resultsize + range + dataQualification + contextScope
                            + jumplistRefs +lowerDate + upperDate;

                    //Log the search page request
                            WebAppLogMessage message = new WebAppLogMessage(req, true);
                            message.setContext("search");
                            message.setMessage("Search Request: " + id);
                            logger.info(message);
                            String Query = req.getParameter("Q");
                            if (Query == null)
                            {
                                Query = "";
                            }
                            try
                            {
                                String queryString = new String("?" + docParam + "Q=" + Query + format + advparams );
                                wrapper.setAttribute("searchurl",ftsUrl + queryString);
                                RequestDispatcher rd = req.getRequestDispatcher("/api-search.jsp?");
                                rd.forward(req,res);
                                //res.sendRedirect(ftsUrl+queryString);
                                return;
                            }
                            catch(Exception e)
                            {
                                message = new WebAppLogMessage(req, true);
                                message.setContext("main");
                                Throwable root = e;
                                if (e instanceof ServletException) {
                                    ServletException se = (ServletException)e;
                                    Throwable t = se.getRootCause();
                                    if (t != null) {
                                        logger.error(message,t);
                                        root = t;
                                    }
                                    else { logger.error(message, se); }
                                }
                                else {
                                    logger.error(message,e);
                                }
                                printError(req,res,"PDS-WS Error",root);
                            }

                }
        	try {id = new Integer(uriElements[2]);}
            catch (Exception e) { }
        }
        if (id==null) {
			printError(req,res,"Invalid DRS ID",null);
			return;
        }
        
		String n = req.getParameter ("n");
		if (n == null) {
		    n = "1";
		}
		else if(n.equals("")) {
			printError(req,res,"Page not found",null);
			return;
		}
		//Set scaling factors. s overrides imagesize.  Image size select
		//boxes are based on S.
		String scale = req.getParameter ("s");
		String imagesize = req.getParameter("imagesize");
		if (scale == null || !(scale.equals("2") || scale.equals("4") ||
				       scale.equals("6") || scale.equals("8"))) {
		    if(imagesize!=null && imagesize.equals("300")) {
		    	scale = "8";
		    }
		    else if(imagesize!=null && imagesize.equals("600")) {
		    	scale = "6";
		    }
		    else if(imagesize!=null && imagesize.equals("1200")) {
		    	scale = "4";
		    }
		    else if(imagesize!=null && imagesize.equals("2400")) {
		    	scale = "2";
		    }
		    else {
		    	scale = "4";
		    }
		}
		if(imagesize==null || !(imagesize.equals("300") || imagesize.equals("600")
					|| imagesize.equals("1200") || imagesize.equals("2400"))) {
		    if(scale.equals("2")) {
		    	imagesize="2400";
		    }
		    else if(scale.equals("4")) {
		    	imagesize="1200";
		    }
		    else if(scale.equals("6")) {
		    	imagesize="600";
		    }
		    else if(scale.equals("8")) {
		    	imagesize="300";
		    }
		}
		String jp2Rotate = req.getParameter("rotation");
		if(jp2Rotate==null || jp2Rotate.equals("360") || jp2Rotate.equals("-360")) {
		    jp2Rotate="0";
		}
		else if(jp2Rotate.equals("-90")) {
		    jp2Rotate="270";
		}
		String jp2x = req.getParameter("jp2x");
		if(jp2x==null) {
		    jp2x="0";
		}
		String jp2y = req.getParameter("jp2y");
		if(jp2y==null) {
		    jp2y="0";
		}
		String jp2Res = req.getParameter("jp2Res");

		String bbx1 = req.getParameter("bbx1");
		if(bbx1==null) {
			bbx1="0";
		}
		String bby1 = req.getParameter("bby1");
		if(bby1==null) {
			bby1="0";
		}
		String bbx2 = req.getParameter("bbx2");
		if(bbx2==null) {
			bbx2="0";
		}
		String bby2 = req.getParameter("bby2");
		if(bby2==null) {
			bby2="0";
		}
                String printThumbnails = req.getParameter("printThumbnails");
                if (printThumbnails==null) {
                    printThumbnails = "no";
                }
                wrapper.setAttribute("printThumbnails", printThumbnails);

                //cg debug
                System.out.println("debug1- imagesize: " + imagesize + " jp2Res: " + jp2Res + " scale: " + scale );

                String clickX = req.getParameter("thumbnail.x");
                if (clickX != null)
                {
                    wrapper.setAttribute("clickX", clickX);
                }
                else
                {
                    clickX = (String)wrapper.getAttribute("clickX");
                }
		String clickY = req.getParameter("thumbnail.y");
                if (clickX != null)
                {
                    wrapper.setAttribute("clickY", clickY);
                }
                else
                {
                    clickY = (String)wrapper.getAttribute("clickY");
                }
                //cg debug
               System.out.println("debug1- thumbnail.x: " + clickX );
               System.out.println("debug1- X: " + req.getParameter("x") );
               System.out.println("debug1- thumbnail.y: " + clickY );
               System.out.println("debug1- Y: " + req.getParameter("y") );

		/**********************************************************
		 * Create new, or use existing Session
		 **********************************************************/
		HttpSession session = req.getSession(true);

		PdsUserState pdsUser = (PdsUserState)session.getAttribute("PdsUser");
		CacheItem item = memcache.getObject(pdsUser.getMeta());
		InternalMets mets = item.getMets();

		//compare request header if-modified-since with METS DRS last modified
		//TODO This is temporarily disabled to allow crawlers access to updated
		// content that they may have already indexed
		/*
		Date metsLastModified = item.getLastModifiedDate();
		try {
			long header = req.getDateHeader("If-Modified-Since");
			if (header > 0) {
				Date headerDate = new Date(header);
				if (metsLastModified.before(headerDate)) {
					res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// we just ignore this
		}
		*/

		//Set the last modified response value
		//TODO Warning - this causes browsers to have problems refreshing the
		// navigation tree html.  Possibly this can be set for the content and
		// citation frames?
		//res.setDateHeader("Last-Modified", metsLastModified.getTime());

		/******************************************************************************
		 *  Get the flags for if doing a page number (p) or sequence number(s) page find
		 *  If p is null, look up sequence, if it is not null, look up with page number.
		 * if getContentPage returns null, the page does not exist or an invalid number was
		 * entered.
		 ******************************************************************************/
		PageDiv pdiv = null;
		try	{
			String pageMode = req.getParameter("P");
			if(pageMode==null || !pageMode.equals("p"))	{
				pageMode = "s";
			}
			//if a page number search trim n
			if(pageMode.equals("p")) {
				n = n.replace(']',' ');
				n = n.replace('[',' ');
				n = n.trim();
			}
			pdiv = mets.getContentPage(n, pageMode);
			//if pdiv is a jpeg2000 and res is null then calculate default res value
			//SET DEFAULT SIZE IF RES NOT SET
			if(jp2Res==null && pdiv.getDefaultImageMimeType().equals("image/jp2")) {

                            //judaica fix
                            int maxJP2sz = getMaxJP2DisplaySize(pdiv.getDefaultImageID());
                            if ( (maxJP2sz > 300) && (maxJP2sz < 600 ) )
                            {
                                    maxJP2sz = 300;
                                    scale = "8";
                            }
                            else if ( (maxJP2sz > 600 ) && (maxJP2sz < 1200) )
                            {
                                    maxJP2sz = 600;
                                    scale = "6";
                            }
                            else if ( (maxJP2sz > 1200 ) && (maxJP2sz < 2400) )
                            {
                                  maxJP2sz = 1200;
                                  scale = "4";
                            }
                            else if (maxJP2sz > 2400)
                            {
                                 maxJP2sz = 2400;
                                 scale = "2";
                            }
                            String origImagesize = imagesize;
                            if (Integer.parseInt(imagesize) > maxJP2sz)
                            {
                                imagesize = Integer.toString(maxJP2sz);
                            }

			    String filepath = getFilePath(pdiv.getDefaultImageID());
        		    Jpeg2000 jp2 = new Jpeg2000(new File(filepath));
			    int newRes = jp2.findResolutionLevelContainedBy(
									    Integer.parseInt(origImagesize),
									    Integer.parseInt(origImagesize));


			    //convert Res to a scaling decimal
			    if(newRes == 1)
			    	jp2Res = "1";
			    else if(newRes == 2)
			    	jp2Res = ".5";
			    else if(newRes == 3)
			    	jp2Res = ".25";
			    else if(newRes == 4)
			    	jp2Res = ".125";
			    else if(newRes == 5)
			    	jp2Res = ".0625";
			    else if(newRes > 5) {
			    	jp2Res = "0.625";
			    }
                            //recalculate newRes if judaica maxres has changed
                            //the actual imagesize
                            if ( !imagesize.equals(origImagesize))
                            {
                                int oldImgsize = Integer.parseInt(origImagesize);
                                int newImgsize = Integer.parseInt(imagesize);
                                float factor = (oldImgsize / newImgsize) / 2;
                                System.out.println("new factor: " + Double.toString(factor) );
                                jp2Res = Double.toString( Double.parseDouble(jp2Res) / factor);
                            }

                            //cg debug
                            //System.out.println("debug2- newRes: " + newRes + " jp2Res is now: " + jp2Res + " scale: " + scale );
			}
			if(pdiv!=null && pageMode.equals("p")) {
				n=String.valueOf(pdiv.getOrder());
				//to keep n in the address bar current, redirect to new URL with equivalent n value of page number
				res.sendRedirect(pdsUrl+"/view/"+id+"?n="+n+"&s="+scale+
						(imagesize!=null?"&imagesize="+imagesize:"")+
						(jp2Res!=null?"&jp2Res="+jp2Res:"")+
						(jp2Rotate!=null?"&rotation="+jp2Rotate:""));
				return;
			}
		}
		catch(Exception e) {
			WebAppLogMessage message = new WebAppLogMessage();
		    message.setContext("find page");
		    message.setMessage("invalid page number");
		    message.processSessionRequest(req);
		    logger.error(message,e);
		}
		if(pdiv==null)	{
			printError(req,res,"Page not found",null);
			return;
		}
		/**********************************************************
		 *  Process appropriately based on the op variable.
		 **********************************************************/
		try	{
			/*************************
			 * If image is a JP2 and this is not a frame request, figure
			 * out x,y to create bounding box on thumbnail
			 ***********************/
			if (pdiv.getDefaultImageMimeType().equals("image/jp2") &&
			   (op != OP_CITATION && op != OP_CONTENT && op != OP_NAVIGATION)) {

                                //judaica fix
                                int maxJP2size = getMaxJP2DisplaySize(pdiv.getDefaultImageID());
                                if ( (maxJP2size > 300) && (maxJP2size < 600 ) )
                                {
                                    maxJP2size = 300;
                                    scale = "8";
                                }
                                else if ( (maxJP2size > 600 ) && (maxJP2size < 1200) )
                                {
                                    maxJP2size = 600;
                                    scale = "6";
                                }
                                else if ( (maxJP2size > 1200 ) && (maxJP2size < 2400) )
                                {
                                    maxJP2size = 1200;
                                    scale = "4";
                                }
                                else if (maxJP2size > 2400)
                                {
                                    maxJP2size = 2400;
                                    scale = "2";
                                }
                                if (Integer.parseInt(imagesize) > maxJP2size)
                                {
                                    imagesize = Integer.toString(maxJP2size);
                                }
				String jp2Action = req.getParameter("action");
				if(jp2Action==null) {
					jp2Action = "noaction";
				}
				int vHeight = Integer.parseInt(imagesize);
				int vWidth = Integer.parseInt(imagesize);
				double imgres = Double.parseDouble(jp2Res);
				//int tWidth = Integer.parseInt(req.getParameter("thumbwidth"));
				//int tHeight = Integer.parseInt(req.getParameter("thumbheight"));
				String filepath = getFilePath(pdiv.getDefaultImageID());
                	        Coordinate dimension = new Coordinate();
				int x = Integer.parseInt(jp2x);
				int y = Integer.parseInt(jp2y);
				Jpeg2000 jp2 = new Jpeg2000(new File(filepath));

				//String clickX = req.getParameter("thumbnail.x");
				//String clickY = req.getParameter("thumbnail.y");
				int thumbX = -1;
				int thumbY = -1;
				if(clickX != null && clickY != null) {
					thumbX = Integer.parseInt(clickX);
					thumbY = Integer.parseInt(clickY);
				}
				if(jp2Action.equals("noaction")) {
					thumbX = 0;
					thumbY = 0;
				}
				else if(jp2Action.equals("jp2pan")) {
					x = thumbX;
					y = thumbY;
					//panThumbnail is passed thumbnail scale coordinates. It returns
					//thumbnail scale coordinates so the new X and Y do not need to be
					//extracted from the method return object
					dimension = UIUtil.panThumbnail(vHeight,vWidth,x,y,imgres,
										  jp2,jp2Rotate);
				}
				else if(jp2Action.equals("jp2zoomin")) {
					dimension = UIUtil.zoom(vHeight,vWidth,x,y,imgres,
											jp2,true,jp2Rotate);
					Coordinate c = UIUtil.convertFullSizeCoordate(jp2,dimension.getX(),dimension.getY(),vWidth,vHeight,imgres,jp2Rotate);
					thumbX = c.getX();
					thumbY = c.getY();
				}
				else if(jp2Action.equals("jp2zoomout")) {
					dimension = UIUtil.zoom(vHeight,vWidth,x,y,imgres,
											jp2,false,jp2Rotate);
					Coordinate c = UIUtil.convertFullSizeCoordate(jp2, dimension.getX(),dimension.getY(),vWidth,vHeight,imgres,jp2Rotate);
					thumbX = c.getX();
					thumbY = c.getY();
				}
				else if(jp2Action.equals("jp2resize")) {
				    String pres = req.getParameter("pres");
				    double pvRes = Double.valueOf(pres);
					String previousWidth = req.getParameter("pvWidth");
					String previousHeight = req.getParameter("pvHeight");
					int pvWidth = Integer.parseInt(previousWidth);
					int pvHeight = Integer.parseInt(previousHeight);
					dimension = UIUtil.centerOnResize(vHeight,vWidth,pvHeight,
									  pvWidth,pvRes,x,y,imgres,
									  jp2,jp2Rotate);
					Coordinate c = UIUtil.convertFullSizeCoordate(jp2, dimension.getX(),dimension.getY(),vWidth,vHeight,imgres,jp2Rotate);
					thumbX = c.getX();
					thumbY = c.getY();
				}
				else if(jp2Action.equals("jp2rotate")) {
					dimension = UIUtil.panThumbnail(vHeight,vWidth,0,0,imgres,
							  jp2,jp2Rotate);
					thumbX = 0;
					thumbY = 0;
				}

				jp2x=String.valueOf(dimension.getX());
				jp2y=String.valueOf(dimension.getY());
				req.setAttribute("jp2x",jp2x);
				req.setAttribute("jp2y",jp2y);

				//set up coordinates to draw bounding box on thumbnail
				CoordinatePair bbCoors = UIUtil.getBoundingBoxDimensions(jp2,imgres,
																vWidth,vHeight,thumbX,thumbY,jp2Rotate);
				bbx1 = Integer.toString(bbCoors.getPoint1().getX());
				bby1 = Integer.toString(bbCoors.getPoint1().getY());
				bbx2 = Integer.toString(bbCoors.getPoint2().getX());
				bby2 = Integer.toString(bbCoors.getPoint2().getY());
			}

	        wrapper.setAttribute("cite",mets.getCitationDiv());
	        wrapper.setAttribute("pdiv",pdiv);
			/*if(action.equalsIgnoreCase(ACTION_VIEW) || action.equalsIgnoreCase(ACTION_VIEW_TEXT)) {
				int imageId = pdiv.getDefaultImageID();
		        wrapper.setAttribute("lastPage",String.valueOf(mets.getCitationDiv().getLastPageNumber()));
		        wrapper.setAttribute("imageId",String.valueOf(imageId));
		        wrapper.setAttribute("id",id);
		        wrapper.setAttribute("n",n);
		        wrapper.setAttribute("s",scale);
		        wrapper.setAttribute("mime",pdiv.getDefaultImageMimeType());
		        wrapper.setAttribute("filepath",getFilePath(imageId));
		        wrapper.setAttribute("idsUrl",idsUrl);
		        wrapper.setAttribute("cache",cache);
		        wrapper.setAttribute("cacheUrl",pdsUrl+cacheUrl);
		        wrapper.setAttribute("jp2Rotate",jp2Rotate);
		        wrapper.setAttribute("jp2Res",jp2Res);
		        wrapper.setAttribute("jp2x",jp2x);
		        wrapper.setAttribute("jp2y",jp2y);
		        wrapper.setAttribute("imagesize",imagesize);
		        wrapper.setAttribute("bbx1",bbx1);
		        wrapper.setAttribute("bby1",bby1);
		        wrapper.setAttribute("bbx2",bbx2);
		        wrapper.setAttribute("bby2",bby2);
		        wrapper.setAttribute("pdsUrl",pdsUrl);
		        wrapper.setAttribute("ids",idsUrl);
		        wrapper.setAttribute("action",action);

				if (op == OP_CITATION)	{
                                        if( pdiv.getDefaultImageMimeType().equals("image/jp2") ) {
                                            //get max res for biling code
                                            wrapper.setAttribute("maxjp2res", Integer.toString(getMaxJP2DisplaySize(pdiv.getDefaultImageID())) );
                                        }
					RequestDispatcher rd = req.getRequestDispatcher("/citation.jsp?");
					rd.forward(req,res);
				}
				else if (op == OP_CONTENT)	{
			        //get paths of the ocr files
			        ArrayList<String> ocrPaths = new ArrayList<String>();
			        for(int i=0;i<pdiv.getOcrID().size();i++) {
			        	Integer ocr = (Integer) pdiv.getOcrID().get(i);
			        	ocrPaths.add(getFilePath(ocr.intValue()));
			        }
			        wrapper.setAttribute("ocrList",ocrPaths);

			        //if image is a tiff, convert to gif
			        if(pdiv.getDefaultImageMimeType().equals("image/tiff")) {
			        	String delv = cache + "/" + imageId + "-" + scale + ".gif";
						File file = new File (delv);
						if (!file.exists ()) {
							Runtime rt = Runtime.getRuntime();
							String tiffpath = getFilePath(imageId);
							String exec = giffy + " id=" + imageId + " path=" +
								tiffpath.substring(0,tiffpath.lastIndexOf("/")) + " scale=" + scale +
								" cache=" + cache;
							Process child = rt.exec (exec);
							child.waitFor();
							child.destroy();
						}
			        }
			        wrapper.setAttribute("caption",item.getImageCaption());
					RequestDispatcher rd = req.getRequestDispatcher("/content.jsp?");
					rd.forward(req,res);

				}
				else if (op == OP_NAVIGATION) {
					String treeIndex = req.getParameter("index");
					String treeAction = req.getParameter("treeaction");

					if (treeAction != null) {
						if (treeAction.equalsIgnoreCase("expand")) {
							pdsUser.setExpandedNodes(id,item.getAllNodesIndices());
						}
						else if (treeAction.equalsIgnoreCase("collapse")) {
							pdsUser.setExpandedNodes(id,new ArrayList<String>());
						}
					}
					if(treeIndex != null) {
						pdsUser.toggleNode(id,treeIndex);
					}
					wrapper.setAttribute("pdsUser",pdsUser);
                                        wrapper.setAttribute("maxThumbnails", maxThumbnails);
					//wrapper.setAttribute("toggleIndex",toggleIndex);
					//wrapper.setAttribute("treeaction",treeAction);
					RequestDispatcher rd = req.getRequestDispatcher("/navigation.jsp?");
					rd.forward(req,res);
				}
				else {
					//Log the frameset request
                    WebAppLogMessage message = new WebAppLogMessage(req, true);
                    message.setContext("frameset");
                    message.setMessage("Frameset Request: " + id);
                    logger.info(message);
					RequestDispatcher rd = req.getRequestDispatcher("/frameset.jsp?");
					rd.forward(req,res);
				}
			}
			else if (action.equalsIgnoreCase(ACTION_CITE_INFO)) {
	               WebAppLogMessage message = new WebAppLogMessage(req, true);
	                message.setContext("fullcitation");
	                message.setMessage("Full Citation: " + id + " Seq: " + n);
	                logger.info(message);
			        wrapper.setAttribute("cite",mets.getCitationDiv());
			        wrapper.setAttribute("pdsUrl",pdsUrl);
			        wrapper.setAttribute("id",id);
			        wrapper.setAttribute("nStr", n+"");

			        String repos = pdsUser.getMeta().getOwner();
			        if (repos == null || repos.equals(""))
			        	repos = "Harvard University Library";
			        String mainUrn = 	pdsUser.getMeta().getUrn();
			        String pageUrn = pdsUser.getMeta().getUrnFromList("?n=" +n);
	                SimpleDateFormat sdf =
	                   new SimpleDateFormat("dd MMMM yyyy");
	                String accDate =  sdf.format(new Date());
	                wrapper.setAttribute("accDate", accDate);
			        wrapper.setAttribute("repos", repos);
			        wrapper.setAttribute("mainUrn", nrsUrl + "/" +mainUrn);
			        wrapper.setAttribute("pageUrn", nrsUrl + "/" + pageUrn);
			        StringBuffer sb = new StringBuffer("");
			        getAllSectionLabels(mets.getCitationDiv(), Integer.parseInt(n), sb);
			        sb.delete(0,2);
			        wrapper.setAttribute("sectLabels", sb.toString());
			        RequestDispatcher rd = req.getRequestDispatcher("/fullcitation.jsp?");
					rd.forward(req,res);
			}
			else if (action.equalsIgnoreCase(ACTION_SEARCH)) {
				//Log the search page request
                WebAppLogMessage message = new WebAppLogMessage(req, true);
                message.setContext("search");
                message.setMessage("Search Request: " + id);
                logger.info(message);
		        wrapper.setAttribute("cite",mets.getCitationDiv());
		        wrapper.setAttribute("ftsUrl",ftsUrl);
		        wrapper.setAttribute("pdsUrl",pdsUrl);
		        wrapper.setAttribute("id",id);
                RequestDispatcher rd = req.getRequestDispatcher("/search.jsp?");
				rd.forward(req,res);
			}
			else if (action.equalsIgnoreCase(ACTION_PRINTOPS)) {
                WebAppLogMessage message = new WebAppLogMessage(req, true);
                message.setContext("printops");
                message.setMessage("print options: " + id);
                logger.info(message);
		        wrapper.setAttribute("pdsUrl",pdsUrl);
		        wrapper.setAttribute("id",id);
		        wrapper.setAttribute("n",n);
                RequestDispatcher rd = req.getRequestDispatcher("/printoptions.jsp?");
				rd.forward(req,res);
			}
			else if (action.equalsIgnoreCase(ACTION_PRINT)) {
				
			}
			else if (action.equalsIgnoreCase(ACTION_LINKS)) {
                WebAppLogMessage message = new WebAppLogMessage(req, true);
                message.setContext("links");
                message.setMessage("display links: " + id);
                logger.info(message);
		        wrapper.setAttribute("cite",mets.getCitationDiv());
		        wrapper.setAttribute("id",id);
		        wrapper.setAttribute("pdsUrl",pdsUrl);
                RequestDispatcher rd = req.getRequestDispatcher("/links.jsp?");
				rd.forward(req,res);
			}*/  //START API CALLS
                        if ( action.equalsIgnoreCase(API_VIEW) || action.equalsIgnoreCase(API_VIEW_TEXT) )
                        {  //main api function. shows ocr'd pages

                            int imageId = pdiv.getDefaultImageID();
                            wrapper.setAttribute("lastPage",String.valueOf(mets.getCitationDiv().getLastPageNumber()));
                            wrapper.setAttribute("imageId",String.valueOf(imageId));
                            wrapper.setAttribute("id",id);
                            wrapper.setAttribute("n",n);
                            wrapper.setAttribute("s",scale);
                            wrapper.setAttribute("mime",pdiv.getDefaultImageMimeType());
                            wrapper.setAttribute("filepath",getFilePath(imageId));
                            wrapper.setAttribute("idsUrl",idsUrl);
                            wrapper.setAttribute("cache",cache);
                            wrapper.setAttribute("cacheUrl",pdsUrl+cacheUrl);
                            wrapper.setAttribute("jp2Rotate",jp2Rotate);
                            wrapper.setAttribute("jp2Res",jp2Res);
                            wrapper.setAttribute("jp2x",jp2x);
                            wrapper.setAttribute("jp2y",jp2y);
                            wrapper.setAttribute("imagesize",imagesize);
                            wrapper.setAttribute("bbx1",bbx1);
                            wrapper.setAttribute("bby1",bby1);
                            wrapper.setAttribute("bbx2",bbx2);
                            wrapper.setAttribute("bby2",bby2);
                            wrapper.setAttribute("pdsUrl",pdsUrl);
                            wrapper.setAttribute("ids",idsUrl);
                            wrapper.setAttribute("action",action);

                            //get paths of the ocr files
			        ArrayList<String> ocrPaths = new ArrayList<String>();
			        for(int i=0;i<pdiv.getOcrID().size();i++) {
			        	Integer ocr = (Integer) pdiv.getOcrID().get(i);
			        	ocrPaths.add(getFilePath(ocr.intValue()));
			        }
			        wrapper.setAttribute("ocrList",ocrPaths);

			        //if image is a tiff, convert to gif
			        if(pdiv.getDefaultImageMimeType().equals("image/tiff")) {
			        	String delv = cache + "/" + imageId + "-" + scale + ".gif";
						File file = new File (delv);
						if (!file.exists ()) {
							Runtime rt = Runtime.getRuntime();
							String tiffpath = getFilePath(imageId);
							String exec = giffy + " id=" + imageId + " path=" +
								tiffpath.substring(0,tiffpath.lastIndexOf("/")) + " scale=" + scale +
								" cache=" + cache;
							Process child = rt.exec (exec);
							child.waitFor();
							child.destroy();
						}
			        }
			        wrapper.setAttribute("caption",item.getImageCaption());
					RequestDispatcher rd = req.getRequestDispatcher("/api-content.jsp?");
					rd.forward(req,res);



                        }
                        else if ( action.equalsIgnoreCase(API_LINKS) )
                        {
                                 WebAppLogMessage message = new WebAppLogMessage(req, true);
                                message.setContext("links");
                                message.setMessage("display links: " + id);
                                logger.info(message);
                                wrapper.setAttribute("cite",mets.getCitationDiv());
                                wrapper.setAttribute("id",id);
                                wrapper.setAttribute("pdsUrl",pdsUrl);
                                wrapper.setAttribute("n",n);
                                RequestDispatcher rd = req.getRequestDispatcher("/api-links.jsp?");
				rd.forward(req,res);
                        }
                        else if ( action.equalsIgnoreCase(API_CITE_INFO) )
                        {
                                WebAppLogMessage message = new WebAppLogMessage(req, true);
                                message.setContext("fullcitation");
                                message.setMessage("Full Citation: " + id + " Seq: " + n);
                                logger.info(message);
			        wrapper.setAttribute("cite",mets.getCitationDiv());
			        wrapper.setAttribute("pdsUrl",pdsUrl);
			        wrapper.setAttribute("id",id);
			        wrapper.setAttribute("nStr", n+"");

			        String repos = pdsUser.getMeta().getOwner();
			        if (repos == null || repos.equals(""))
			        	repos = "Harvard University Library";
			        String mainUrn = 	pdsUser.getMeta().getUrn();
			        String pageUrn = pdsUser.getMeta().getUrnFromList("?n=" +n);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                                String accDate =  sdf.format(new Date());
                                wrapper.setAttribute("accDate", accDate);
			        wrapper.setAttribute("repos", repos);
			        wrapper.setAttribute("mainUrn", nrsUrl + "/" +mainUrn);
			        wrapper.setAttribute("pageUrn", nrsUrl + "/" + pageUrn);
			        StringBuffer sb = new StringBuffer("");
			        getAllSectionLabels(mets.getCitationDiv(), Integer.parseInt(n), sb);
			        sb.delete(0,2);
			        wrapper.setAttribute("sectLabels", sb.toString());
			        RequestDispatcher rd = req.getRequestDispatcher("/api-fullcitation.jsp?");
					rd.forward(req,res);
                        }
                        else if ( action.equalsIgnoreCase(API_DOC_TREE) )
                        {
                            String treeIndex = req.getParameter("index");
                            String treeAction = req.getParameter("treeaction");

                            /*if (treeAction != null)
                            {
                                if (treeAction.equalsIgnoreCase("expand")) {
                                    pdsUser.setExpandedNodes(id,item.getAllNodesIndices());
				}
                                else if (treeAction.equalsIgnoreCase("collapse")) {
					pdsUser.setExpandedNodes(id,new ArrayList<String>());
						}
				}
				if(treeIndex != null) {
                                    pdsUser.toggleNode(id,treeIndex);
				}*/
                                //expand the tree
                                pdsUser.setExpandedNodes(id,item.getAllNodesIndices());
                                wrapper.setAttribute("pdsUser",pdsUser);
                                wrapper.setAttribute("pdsUrl",pdsUrl);
                                wrapper.setAttribute("cacheUrl",pdsUrl+cacheUrl);
                                wrapper.setAttribute("cache",cache);
			        wrapper.setAttribute("id",id);
                                wrapper.setAttribute("n",n);
                                wrapper.setAttribute("s",scale);
                                wrapper.setAttribute("action","get");
                                wrapper.setAttribute("idsUrl",idsUrl);
                                wrapper.setAttribute("maxThumbnails", maxThumbnails);
				wrapper.setAttribute("jp2Rotate",jp2Rotate);
                            	wrapper.setAttribute("jp2x",jp2x);
                            	wrapper.setAttribute("jp2y",jp2y);
				wrapper.setAttribute("caption",item.getImageCaption());
					//wrapper.setAttribute("toggleIndex",toggleIndex);
					//wrapper.setAttribute("treeaction",treeAction);
                                RequestDispatcher rd = req.getRequestDispatcher("/api-navigation.jsp?");
				rd.forward(req,res);
                        } //todo?
                        else if ( action.equalsIgnoreCase(API_MAX_SECTIONS) )
                        {

                        }
                        else if ( action.equalsIgnoreCase(API_MAX_PAGES) )
                        {

                        }
                        else if ( action.equalsIgnoreCase(API_MAX_CHAPTERS) )
                        {

                        }
                        


		} //END MAIN
		catch (Exception e) {
            WebAppLogMessage message = new WebAppLogMessage(req, true);
            message.setContext("main");
            Throwable root = e;
            if (e instanceof ServletException) {
                ServletException se = (ServletException)e;
                Throwable t = se.getRootCause();
                if (t != null) {
                    logger.error(message,t);
                    root = t;
                }
                else { logger.error(message, se); }
            }
            else {
                logger.error(message,e);
            }
            printError(req,res,"PDS-WS Error",root);
		}
	}

    //TODO update with DRS2 WS call
	public String getFilePath(int id) {
		Connection conn = null;
		Statement stmt = null;
		String filepath = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT filepath FROM drs_objects WHERE object_id="+id);

			if (!conf.getBoolean("useOnlyDRS2") && rset.next()) { //If object is found in DRS1 and not migrated  //TODO change OBJ_QUERY to include migrated flag
				filepath = rset.getString ("filepath");
			}
			else if(conf.getBoolean("useDRS2")){
				DRSFileDTOExt fmd = drs2Service.getFileMetadataById(String.valueOf(id),false);
				filepath = fmd.getFilePath();
			}
		}
		catch (SQLException e1) {
			System.out.println("<i><b>Error code:</b> " + e1 + "</i>");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try{if(stmt != null) {stmt.close();}} catch(SQLException e1){}
			if (conn != null) {
	            // return the connection to the pool
	            try { conn.close(); } catch (Exception e) { }
	        }
		}
		return filepath;
	}

	public static void printError(HttpServletRequest req, HttpServletResponse res,
			   				String message, Throwable e) {
		HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(req);
	    wrapper.setAttribute("message",message);
	    wrapper.setAttribute("pdsUrl",req.getContextPath());
	    wrapper.setAttribute("exception",e);
	    wrapper.setAttribute("req",req);
	    RequestDispatcher rd = req.getRequestDispatcher("/api-error.jsp?");
		try {
			rd.forward(req,res);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 *
	 * A yuckily iterative process to find the successive section names
	 * @param div
	 * @param num
	 * @param sb
	 */
	public void getAllSectionLabels(Div div, int num, StringBuffer sb) {
		if (sb == null ) sb = new StringBuffer();
		if (div instanceof PageDiv) {
			return;
		}
		else if (div instanceof CitationDiv) {
			CitationDiv cite = (CitationDiv)div;
			List<Div> children = cite.getChildren();
			for (Div child: children) {
				if (child instanceof IntermediateDiv) {
					IntermediateDiv intDiv = (IntermediateDiv) child;
					if (num>=intDiv.getFirstOrder()&&num<=intDiv.getLastOrder()) {
						getAllSectionLabels(intDiv, num, sb);
						return;
					}
				}
			}
		}
		else if (div instanceof IntermediateDiv) {
			IntermediateDiv intDiv = (IntermediateDiv)div;
			sb.append(", " + UnicodeUtils.getBidiDivedElement(intDiv.getLabel()) );
			List<Div> children = intDiv.getChildren();
			for (Div child: children) {
				if (child instanceof IntermediateDiv) {
					IntermediateDiv id = (IntermediateDiv) child;
					if (num>=id.getFirstOrder()&&num<=id.getLastOrder()) {
						getAllSectionLabels(id, num, sb);
						return;
					}
				}
			}
		}


	}



    //get the max jp2 max viewport size
    //TODO update with DRS2 WS call
	public int getMaxJP2DisplaySize(int id) {
		Connection conn = null;
		Statement stmt = null;
		int maxJP2DisplaySize = 3000; //max default
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT b.MAX_JP2_DISPLAY_SIZE from billing b, drs_objects d where d.object_id=" + id + " and d.billing_code = b.billing_code");

			if (!conf.getBoolean("useOnlyDRS2") && rset.next()) { //If object is found in DRS1 and not migrated  //TODO change OBJ_QUERY to include migrated flag
                                if ( rset.getInt ("MAX_JP2_DISPLAY_SIZE") > 0 )
                                {
                                    maxJP2DisplaySize = rset.getInt ("MAX_JP2_DISPLAY_SIZE");
                                }
                                System.out.println("MAX JP2 RES SET TO: " + Integer.toString(maxJP2DisplaySize ) );
			}
			/*IGNORE DRS 2 Objects for now 9/14/10 - cg
                         else if(conf.getBoolean("useDRS2")){
				ServiceResponse drs2resp = drs2Service.getFileMetadata(String.valueOf(id),false);
				FileMetadata fmd = drs2resp.getFileMetadata();
				maxDisplaySize = fmd.getMaxJP2DisplaySize();
			}*/
		}
		catch (SQLException e1) {
			System.out.println("<i><b>Error code:</b> " + e1 + "</i>");
		}
                /*catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		finally {
			try{if(stmt != null) {stmt.close();}} catch(SQLException e1){}
			if (conn != null) {
	            // return the connection to the pool
	            try { conn.close(); } catch (Exception e) { }
	        }
		}
		return maxJP2DisplaySize;
	}
    





}
