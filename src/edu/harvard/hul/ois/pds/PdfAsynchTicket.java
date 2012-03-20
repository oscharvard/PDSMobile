/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jcg155
 */
package edu.harvard.hul.ois.pds;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;

//import javax.servlet.http.HttpServletRequest;
//import org.apache.commons.configuration.XMLConfiguration;
//import org.apache.commons.dbcp.BasicDataSource;
//import org.xml.sax.SAXException;
//import com.lowagie.text.Document;
//import com.lowagie.text.Font;
//import com.lowagie.text.HeaderFooter;
//import com.lowagie.text.Image;
//import com.lowagie.text.PageSize;
//import com.lowagie.text.Paragraph;
//import com.lowagie.text.Phrase;
//import com.lowagie.text.Rectangle;
//import com.lowagie.text.pdf.PdfWriter;

/*import edu.harvard.hul.ois.drs2.callservice.FileMetadata;
import edu.harvard.hul.ois.drs2.callservice.ServiceResponse;
import edu.harvard.hul.ois.drs2.callservice.ServiceWrapper;*/
import edu.harvard.hul.ois.pdx.util.InternalMets;

public class PdfAsynchTicket {

    private String start;
    private String end;
    private Integer id;
    private String email;
    private String printOpt;
    private String n;
    private InternalMets mets;
    private Integer maxJP2res;
    
    
    public String getEmail()
    {
        return email;
    }
    
    public String getStart()
    {
        return start;
    }
    
    public String getEnd()
    {
        return end;
    }
    
    public String getPrintOpt()
    {
        return printOpt;
    }
    
    public String getN()
    {
        return n;
    }
    
    public Integer getId()
    {
        return id;
    }
    
    public InternalMets getMets()
    {
        return mets;
    }
    
    public Integer getMaxJP2Res()
    {
        return maxJP2res;
    }
    
    //setter
    public void setEmail(String addr)
    {
        this.email = addr;
    }
    
    public void setStart(String s)
    {
        this.start = s;
    }
    
    public void setEnd(String e)
    {
        this.end = e;
    }
    
    public void setPrintOpt(String opt)
    {
        this.printOpt = opt;
    }
     
    public void setN(String x)
    {
        this.n = x;
    }
    
    public void setId(Integer i)
    {
        this.id = i;
    }
    
    public void setMets(InternalMets m)
    {
        this.mets = m;
    }
    
    public void setMaxJP2Res(Integer m)
    {
        this.maxJP2res = m;
    }
    
    public PdfAsynchTicket(String s, String e, Integer drs_id, String mail, String opt, String num, InternalMets mts, Integer max)
    {
        this.start = s;
        this.end = e;
        this.id = drs_id;
        this.email = mail;
        this.printOpt = opt;
        this.n = num;
        this.mets = mts;
        this.maxJP2res = max;
    }
    
    
    
}
