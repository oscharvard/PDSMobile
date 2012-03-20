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
import edu.harvard.hul.ois.xml.XmlLayout;
import java.io.File;
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
//import java.util.Iterator;

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
import edu.harvard.hul.ois.pdx.util.CitationDiv;
//import edu.harvard.hul.ois.pdx.util.Div;
//import edu.harvard.hul.ois.pdx.util.IntermediateDiv;
import edu.harvard.hul.ois.pdx.util.InternalMets;
//import edu.harvard.hul.ois.pdx.util.PageDiv;
import edu.harvard.hul.ois.pdx.util.UnicodeUtils;

import java.util.Properties;
import java.util.Date;
import javax.mail.*;
import javax.mail.internet.*;
import javax.sql.DataSource;

public class PdfAsynchThread extends Thread 
{
    private PdfAsynchTicket t;
    private XMLConfiguration conf;
    private DataSource dataSource;
    private List queue;
    volatile boolean keepRunning = true;
    
    public PdfAsynchThread(PdfAsynchTicket ticket, XMLConfiguration c, DataSource ds )
    {
        this.t = ticket;
        this.conf = c;
        this.dataSource = ds;
    }
    
    public void run()
    {

        System.out.println("async thread - processing id: " + Integer.toString(t.getId()) );

                try
                {
                    File pdf = PdfAsynchGenerator.generate(t, conf, dataSource);
                
                    //email file
                
                    String host = conf.getString("emailHost");
                    String from = conf.getString("emailFrom");
                    String email = t.getEmail();
                    String pds = conf.getString("pds");
                    String emailTemplate = conf.getString("emailBody");
                    String dropboxURL = pds + conf.getString("dropboxURL") + pdf.getName();
                    InternalMets mets = t.getMets();
                    CitationDiv cite = mets.getCitationDiv();
                    String desc = UnicodeUtils.getBidiDivedElement(cite.getDisplayLabel());
                   
                    
                    Properties props = new Properties();
                    props.put("mail.smtp.host", host);
                    props.put("mail.from", from);
                    Session session = Session.getInstance(props, null);

                    try 
                    {
                        MimeMessage msg = new MimeMessage(session);
                        msg.setFrom();
                        msg.setRecipients(Message.RecipientType.TO, email);
                        msg.setSubject("Your requested PDS document - id: " + Integer.toString(t.getId()) );
                        msg.setSentDate(new Date());
                        String emailText = emailTemplate + "\n\n" + dropboxURL;
                        msg.setText(emailText);
                      Transport.send(msg);
                      System.out.println("async thread - emailed link " + dropboxURL + " to " + email);
                    } catch (MessagingException mex) 
                    {
                        WebAppLogMessage message = new WebAppLogMessage();
                        message.setContext("pdfemail");
                        message.setMessage("Error sending pdf: " + mex);
                    }
                    
                    
                }
                catch (Exception e)
                {
                    WebAppLogMessage message = new WebAppLogMessage();
                    message.setContext("pdf");
                    message.setMessage("Error processing async PDF");
                    System.out.println("async thread - email error " + e.toString() );
                
                }
            
            
            
  
    }
}
