/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jcg155
 */

package edu.harvard.hul.ois.pds;

import edu.harvard.hul.ois.xml.WebAppLogMessage;
//import edu.harvard.hul.ois.xml.XmlLayout;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Hashtable;
import java.util.List;
//import java.util.Collections;
import java.util.Iterator;

//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.servlet.RequestDispatcher;
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletOutputStream;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import org.apache.commons.configuration.XMLConfiguration;
//import org.apache.log4j.Appender;
//import org.apache.log4j.Logger;
//import org.apache.log4j.DailyRollingFileAppender;

//import edu.harvard.hul.ois.drs2.callservice.FileMetadata;
//import edu.harvard.hul.ois.drs2.callservice.ServiceException;
//import edu.harvard.hul.ois.drs2.callservice.ServiceResponse;
//import edu.harvard.hul.ois.drs2.callservice.ServiceWrapper;
//import edu.harvard.hul.ois.pds.cache.CacheController;
//import edu.harvard.hul.ois.pds.cache.CacheItem;
//import edu.harvard.hul.ois.pds.exceptions.TooManyPDFConversions;
//import edu.harvard.hul.ois.pds.ids_util.Coordinate;
//import edu.harvard.hul.ois.pds.ids_util.CoordinatePair;
//import edu.harvard.hul.ois.pds.ids_util.Jpeg2000;
//import edu.harvard.hul.ois.pds.ids_util.UIUtil;
//import edu.harvard.hul.ois.pds.user.PdsUserState;
//import edu.harvard.hul.ois.pdx.util.CitationDiv;
//import edu.harvard.hul.ois.pdx.util.Div;
//import edu.harvard.hul.ois.pdx.util.IntermediateDiv;
//import edu.harvard.hul.ois.pdx.util.InternalMets;
//import edu.harvard.hul.ois.pdx.util.PageDiv;
//import edu.harvard.hul.ois.pdx.util.UnicodeUtils;

//import java.util.Properties;
//import java.util.Date;
//import javax.mail.*;
//import javax.mail.internet.*;
import javax.sql.DataSource;

public class PdfMainAsynchThread extends Thread {

    private XMLConfiguration conf;
    private DataSource dataSource;
    private List queue;
    volatile boolean keepRunning = true;
    
    public PdfMainAsynchThread(XMLConfiguration c, DataSource ds )
    {
        this.conf = c;
        this.dataSource = ds;
    }
    
    public void stopRequest() { keepRunning = false; }

    public void run()
    {
        queue = PdsServlet.getPdfAsyncQueue();
        int seconds = Integer.parseInt(conf.getString("queueWait"));
        System.out.println("main async thread - running");
        while (keepRunning)
        {
            synchronized (queue)
            { 
                Iterator i = queue.iterator();
                while (i.hasNext())
                {
                    PdfAsynchTicket ticket = ((PdfAsynchTicket)i.next());
                    PdfAsynchThread pt = new PdfAsynchThread(ticket, conf, dataSource);
                    pt.start();
                    //remove from queue
                    i.remove();
                }
            
            }      
            try
            {
                System.out.println("async main thread - sleeping for " + Integer.toString(seconds) + " seconds");
                Thread.sleep(seconds);
            }
            catch (Exception e )
            {
                WebAppLogMessage message = new WebAppLogMessage();
                message.setContext("async");
                message.setMessage("Error with main async pdf thread");
                System.out.println("main async thread - error " + e.toString() );
                return;      
            }
        }
    
    }    
    
    

}
