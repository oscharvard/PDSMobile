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
import edu.harvard.hul.ois.mets.helper.parser.*;
import edu.harvard.hul.ois.xml.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.net.URISyntaxException;

public class Moa2Handler
    extends DefaultHandler
{
    /******************************************************************
	 * PRIVATE CLASS FIELDS.
	 ******************************************************************/
	
    private static final String _select =
		"SELECT filepath FROM drs_objects WHERE object_id=?";
    private static int _sequence = 0;
	private static int dmdid = 0;
	private static String beginDate;
	private static String endDate;
	private static List dmdList  = new ArrayList();
	private static List jpegList = new ArrayList();
	private static List tiffList = new ArrayList();
	private static List textList = new ArrayList();
	private static int maxSequence = 0;
    
	/******************************************************************
	 * PRIVATE INSTANCE METHODS.
	 ******************************************************************/
	private Hashtable tempHash   = new Hashtable();
    private StringBuffer _characters;
    private boolean _citation;
    private boolean _citationLeaf;
    private Connection _conn;
    private String _creator;
    private edu.harvard.hul.ois.mets.Div _div;
    private SAXParserFactory _factory;
    private String _feature;
    private MetsElement _fileParent;
    private boolean _intermediate;
    private boolean _isCreator;
    private String _label;
    private boolean _leaf;
    private List _localID = new ArrayList();
	private List _localType = new ArrayList();
    private Mets _mets;
    private String _n;
    private String _pg;
    private MetsElement _structParent;
    private String _title;
    private String _type;
	private String _mode;
	private String _citationId;
	private String _rootPath;
	private String _fileOutputPath;
	private String _citationEndDate;
	private String _citationBeginDate;
	private FileOutputStream _fout;
    private PrintStream _p;
	private boolean _twoCitationDmdsec;
	private boolean _jp2Mode;
	public static boolean printSequence = false;
	public static String  currLineToPrint = "";
	
	//private static String _prevDivOrderLabel;
	//private static String _currDivOrderLabel;
	private static String _prevDivPG;
	private static String _currDivPG;
	private static String _prevDivLabel;
	private static String _currDivLabel;
	
    /******************************************************************
	 * CLASS CONSTRUCTOR.
	 ******************************************************************/
	
    public Moa2Handler ()
    {
		super ();
		_citation     = false;
		_citationLeaf = false;
		_intermediate = false;
		_leaf         = false;
		_characters   = new StringBuffer ();
		_twoCitationDmdsec = false;
    }
	
    /******************************************************************
	 * PUBLIC INSTANCE METHODS.
	 ******************************************************************/
	/*	public void setCurrDivOrderLabel(String label)
	{
		_currDivOrderLabel = label;
	 }*/
	public void setCurrDivPG(String pg)
	{
		_currDivPG = pg;
	}
	public void setCurrDivLabel(String label)
	{
		_currDivLabel = label;
	}
	/*public String getCurrDivOrderLabel()
	{
		return _currDivOrderLabel;
	 }*/
	public String getCurrDivPG()
	{
		return _currDivPG;
	}
	public String getCurrDivLabel()
	{
		return _currDivLabel;
	}
	
	public int lastCitationIndex()
	{
		if(_twoCitationDmdsec)
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}
	public void setFileoutputPath(String path)
	{
		_fileOutputPath = path;
	}
    public List getDmdList ()
    {
		return dmdList;
    }
	public List getJpegList ()
    {
		return jpegList;
    }
	public List getTiffList ()
    {
		return tiffList;
    }
	public List getTextList ()
    {
		return textList;
    }
	public void setMode (String mode)
    {
		_mode = mode;
    }
	public void setMode (String mode, String citationId)
    {
		_mode = mode;
		_citationId = citationId;
    }
	public String getMode ()
    {
		return _mode;
    }
	public void setRootPath (String rootPath)
    {
		_rootPath = rootPath;
    }
    public void setConnection (Connection conn)
    {
		_conn = conn;
    }
	
    public void setFactory (SAXParserFactory factory)
    {
		_factory = factory;
    }
	
    public void setFileParent (MetsElement fileParent)
    {
		_fileParent = fileParent;
    }
	
    public void setMets (Mets mets)
    {
		_mets = mets;
    }
	
    public void setStructParent (MetsElement structParent)
    {
		_structParent = structParent;
    }
	public void setJp2Mode (boolean jp2)
    {
		_jp2Mode = jp2;
    }
	
    public void characters (char [] ch, int start, int length)
    {
		_characters.append (ch, start, length);
    }
	
    public void endElement (String uri, String localName, String qName)
    {
		if (_citation || _citationLeaf)
		{
			if (localName.equals ("DescMD"))
			{
				DmdSec dmdSec = new DmdSec ();
				dmdSec.setID ("C0");
				dmdSec.setGROUPID("CITATION");
				
				MdWrap mdWrap = new MdWrap ();
				mdWrap.setMDTYPE (Mdtype.MODS);
				
				XmlData xmlData = new XmlData ();
				
				edu.harvard.hul.ois.mets.helper.parser.Attributes attrs =
					new edu.harvard.hul.ois.mets.helper.parser.Attributes ();
				attrs.add (new Attribute ("xmlns:mods",
										  "http://www.loc.gov/mods/v3", '"'));
				attrs.add (new Attribute ("xsi:schemaLocation",
										  "http://www.loc.gov/mods/v3 " +
											  "http://www.loc.gov/standards/" +
											  "mods/v3/mods-3-0.xsd", '"'));
				attrs.add (new Attribute ("version", "3.0", '"'));
				Any mods = new Any ("mods:mods", attrs);
				List content = mods.getContent ();
				if(_creator!=null) {
					Any name = new Any ("mods:name");
					Any displayForm = new Any ("mods:displayForm", _creator);
					name.getContent ().add (displayForm);
					content.add (name);
				}
				if(_title != null) {
					Any titleInfo = new Any ("mods:titleInfo");
					Any title = new Any ("mods:title", _title);
					titleInfo.getContent ().add (title);
					content.add (titleInfo);
				}
				
				if (_localID != null)
				{
					for(int i=0;i<_localID.size();i++) {
						String id =((String)_localID.get(i));
						String type = (String)_localType.get(i);
						attrs =	new edu.harvard.hul.ois.mets.helper.parser.Attributes ();
						//Remove (MH) from type
						if(((String)_localType.get(i)).charAt(0) == '(') {
							type = type.substring(type.lastIndexOf(")")+1, type.length());
						}
						
						if(type.compareToIgnoreCase("aleph") == 0 ||
						   type.compareToIgnoreCase("hollis") == 0) {
							type = "hollis";
							//Check for alpha characters in type
							//If it contains characters, make type=local
							boolean local = false;
							for(int j=0;j<id.length();j++) {
								if(!Character.isDigit(id.charAt(j))) {
									type="local";
									local = true;
									break;
								}
							}
							if(local || Integer.parseInt(id)!=0) {
								attrs.add (new Attribute ("type", type, '"'));
								Any identifier = new Any ("mods:identifier", attrs,id);
								content.add (identifier);
							}
						}
						else if(type.compareToIgnoreCase("oasis") == 0 ) {
							if(id!=null && id.length()>3) {
								type = "uri";
								attrs.add(new Attribute("displayLabel", "OASIS finding aid", '"'));
								//Lookup and construct oasis URN
								String locCode = id.substring(0,3);
								String authorityPath = (String)Moa2Mets.oasisHash.get(locCode);
								String urnString = "urn-3:" + authorityPath + ":" + id;
								id = "http://nrs.harvard.edu/"+urnString;
																
								attrs.add (new Attribute ("type", type, '"'));
								Any identifier = new Any ("mods:identifier", attrs,id);
								content.add (identifier);
							}
						}
						else {
							attrs.add (new Attribute ("type", "local", '"'));
							attrs.add (new Attribute("displayLabel", type, '"'));
							Any identifier = new Any ("mods:identifier", attrs,id);
							content.add (identifier);
						}
					}
				}
				 
				xmlData.getContent ().add (mods);
				mdWrap.getContent ().add (xmlData);
				dmdSec.getContent ().add (mdWrap);
				
				if(_citationBeginDate!=null || _citationEndDate!=null)
				{
					_twoCitationDmdsec = true;
					DmdSec dmdSec2 = new DmdSec();
					dmdSec2.setID("C1");
					dmdSec2.setGROUPID("CITATION");
					Any pds     = new Any ("pds:pds");
					Any pdsdate = new Any("pds:date");
					if(_citationBeginDate!=null)
					{
						Any pdsfrom = new Any("pds:fromDate", _citationBeginDate);
						pdsdate.getContent().add(pdsfrom);
					}
					if(_citationEndDate!=null)
					{
						Any pdsto   = new Any("pds:toDate", _citationEndDate);
						pdsdate.getContent().add(pdsto);
					}
					MdWrap pdsmdWrap = new MdWrap ();
					pdsmdWrap.setMDTYPE (Mdtype.OTHER);
					pdsmdWrap.setOTHERMDTYPE("HULPDS");
					XmlData pdsxmlData = new XmlData ();
					pds.getContent().add(pdsdate);
					pdsxmlData.getContent().add(pds);
					pdsmdWrap.getContent ().add (pdsxmlData);
					dmdSec2.getContent().add(pdsmdWrap);
					_mets.getContent ().add (0,dmdSec2);
				}
				_mets.getContent ().add (0,dmdSec);
				
			}
			else if (localName.equals ("Creator"))
			{
				if (_isCreator)
				{
					_creator = _characters.toString ();
				}
			}
			else if (localName.equals ("LocalID"))
			{
				_localID.add(_characters.toString ());
				//_localID = _characters.toString ();
			}
			else if (localName.equals ("Title"))
			{
				_title = _characters.toString ();
			}
			if (localName.equals ("StructMap"))
			{
				List content = _mets.getContent ();
				StructMap structMap = new StructMap ();
				structMap.getContent ().add (_structParent);
				content.add (structMap);
			}/*
			if(localName.equals("coreDate") && _otherDateFormat)
			{
				_citationBeginDate = _characters.toString();
				_citationEndDate = _characters.toString();
				_otherDateFormat = false;
			 }*/
		}
		if (localName.equals ("div"))
		{
			if (_div != null)
			{
				_structParent.getContent ().add (_div);
				_div = null;
			}
		}
		
		_characters = new StringBuffer ();
    }
	
	/**
	 * Method startElement
	 *
	 * @param    uri                 a  String
	 * @param    localName           a  String
	 * @param    qName               a  String
	 * @param    attrs               an Attributes
	 *
	 * @exception   SAXException
	 *
	 */
    public void startElement (String uri, String localName, String qName,
							  org.xml.sax.Attributes attrs)
		throws SAXException
    {
		if (localName.equals ("ArchObj"))
		{
			String value = attrs.getValue ("", "NODETYPE");
			if (value.equals ("citation"))
			{
				_citation = true;
			}
			else if (value.equals ("citationleaf"))
			{
				_citationLeaf = true;
			}
			else if (value.equals ("intermediate"))
			{
				_intermediate = true;
			}
			else if (value.equals ("leaf"))
			{
				_leaf = true;
			}
			
			if (_citation || _citationLeaf)
			{
				_mets.setLABEL (attrs.getValue ("", "LABEL"));
			}
		}
		else if (localName.equals("LocalID")) {
			_localType.add(attrs.getValue("LocalIDType"));
			//_localType = attrs.getValue("LocalIDType");
		}
		else if (localName.equals ("div"))
		{
			//_prevDivOrderLabel = this.getCurrDivOrderLabel();
			_prevDivPG = this.getCurrDivPG();
			_prevDivLabel = this.getCurrDivLabel();
			
			_feature = attrs.getValue ("", "FEATURE");
			_label   = attrs.getValue ("", "LABEL");
			_pg      = attrs.getValue ("", "PG");
			_type    = attrs.getValue ("", "TYPE");
			
			/*if(_label != null)
			{
				this.setCurrDivOrderLabel(_label);
			 }*/
			
			if (_type != null && _type.toLowerCase ().equals ("page"))
			{
				//if(_pg!=null)
				//{
					this.setCurrDivPG(_pg);
				//}
				//if(_label!=null)
				//{
					this.setCurrDivLabel(_label);
				//}
				_div = new edu.harvard.hul.ois.mets.Div();
				++_sequence;
				maxSequence = _sequence;
				_div.setORDER (_sequence);
				_div.setTYPE ("PAGE");
				if (_label != null && !_label.equals(""))
				{
					if(_label.startsWith(", ")) {
						_label = _label.substring (2);
					}
					else if (_label.startsWith(" ")) {
						_label = _label.substring (1);
					}
					this.setCurrDivLabel(_label);
					if(!_label.equals("unnumbered page") && !_label.equals("")) {
						_div.setLABEL(_label);
					}
				}
				if (_pg != null && _pg.length () > 0)
				{
					_div.setORDERLABEL (_pg);
				}
			}
		}
		else if (localName.equals ("fptr"))
		{
			String mime = attrs.getValue ("", "HARVMIME");
			String value;
			String id;
			if(_mode.equals("drs"))
			{
				value = attrs.getValue ("", "FILEID").substring (1);
			}
			else
			{
				//in local mode.
				value = attrs.getValue ("", "FILEID");
			}
			id = "F" + value;
			id = XmlWriter.encodeID(id);
			try
			{
				value = XmlWriter.encodeRelativeURI(value);
			}
			catch (URISyntaxException e) {}
			edu.harvard.hul.ois.mets.File file =
				new edu.harvard.hul.ois.mets.File ();
			file.setID (id);
			if (mime.equals ("bitonal-archival") ||
				mime.equals ("grayscale-archival") ||
				mime.equals ("color-archival"))
			{
				file.setMIMETYPE ("image/tiff");
			}
			else if (mime.equals ("dirty-ocr"))
			{
				file.setMIMETYPE ("text/plain");
			}
			else if (mime.equals ("color-deliverable") ||
					 mime.equals ("grayscale-deliverable"))
			{
				if(_jp2Mode) {
					file.setMIMETYPE ("image/jp2");
				}
				else {
					file.setMIMETYPE ("image/jpeg");
				}
			}
			
			FLocat fLocat = new FLocat ();
			fLocat.setLOCTYPE (Loctype.OTHER);
			if(_mode.equals("drs"))
			{
				//fLocat.setOTHERLOCTYPE ("DRS");
				fLocat.setOTHERLOCTYPE ("OracleID");
			}
			else
			{
				fLocat.setOTHERLOCTYPE ("FILE");
			}
			fLocat.setXlinkHref (value);
			file.getContent ().add (fLocat);
			
			if(tempHash.get(file.getID()) == null)
			{
				tempHash.put(file.getID(), file);
				if(file.getMIMETYPE().equals("image/jpeg") || file.getMIMETYPE().equals("image/jpg")
				  || file.getMIMETYPE().equals("image/jp2"))
				{
					jpegList.add(file);
				}
				if(file.getMIMETYPE().equals("image/tiff"))
				{
					tiffList.add(file);
				}
				if(file.getMIMETYPE().equals("text/plain"))
				{
					textList.add(file);
				}
				if(printSequence)
				{
					if(_fileOutputPath != null)
					{
						try { _fout = new FileOutputStream(_fileOutputPath + "/"+_citationId+".txt", true);}
						catch (FileNotFoundException e) {}
						_p = new PrintStream (_fout);
						_p.println(currLineToPrint+":"+_sequence);
						_p.close();
						printSequence = false;
						currLineToPrint = "";
					}
					else
					{
						System.out.print(currLineToPrint+":"+_sequence+"\n");
						printSequence = false;
						currLineToPrint = "";
					}
				}
			}
			else
			{
				//Adjust page number and labels to reflect correct numbers
				//Do not let sequence go to more than 1 less than the current
				//maxSequence value
				if(_sequence>maxSequence-1)
				{
					_sequence = _sequence-1;
					_div.setORDER (_sequence);
					_div.setTYPE ("PAGE");
					if(_prevDivLabel!=null && !_prevDivLabel.equals("") && !_prevDivLabel.equals("unnumbered page")) {
						_div.setLABEL(_prevDivLabel);
					}
					else {
						_div.setLABEL(null);
					}
					if(_prevDivPG!=null && !_prevDivPG.equals("")) {
						_div.setORDERLABEL(_prevDivPG);
					}
					else {
						_div.setORDERLABEL(null);
					}
				}


				if(printSequence)
				{
					if(_fileOutputPath != null)
					{
						try{_fout = new FileOutputStream(_fileOutputPath + "/"+_citationId+".txt", true);}
						catch (FileNotFoundException e) {}
						_p = new PrintStream (_fout);
						_p.println(currLineToPrint+":"+_sequence);
						_p.close();
						printSequence = false;
						currLineToPrint = "";
					}
					else
					{
						System.out.print(currLineToPrint+":"+_sequence+"\n");
						printSequence = false;
						currLineToPrint = "";
					}
				}
			}
			Fptr fptr = new Fptr ();
			fptr.setFILEID (id);
			_div.getContent ().add (fptr);
			
		}
		else if (localName.equals("coreDate") && _intermediate)
		{
			if(attrs.getValue("beginDateNorm")!= null && !attrs.getValue("beginDateNorm").equals(""))
			{
				beginDate = attrs.getValue("beginDateNorm");
			}
			else
			{
				beginDate = null;
			}
			if(attrs.getValue("endDateNorm")!= null && !attrs.getValue("endDateNorm").equals(""))
			{
				endDate   = attrs.getValue("endDateNorm");
			}
			else
			{
				endDate = null;
			}
			String [] dates = {beginDate, endDate};
			dmdList.add(dates);
		}
		else if (localName.equals("coreDate") && (_citation || _citationLeaf))
		{
			if(attrs.getValue("beginDateNorm")!= null && !attrs.getValue("beginDateNorm").equals(""))
			{
				_citationBeginDate = attrs.getValue("beginDateNorm");
			}
			else
			{
				//Log bad date range
				if(_fileOutputPath != null)
				{
					try{_fout = new FileOutputStream(_fileOutputPath + "/nonStandardDates.txt", true);}
					catch (FileNotFoundException e) {}
					_p = new PrintStream (_fout);
					_p.println(_citationId);
					_p.close();
				}
				else {System.out.println("Non Standard Date: "+ _citationId);}
			}
			if(attrs.getValue("endDateNorm")!= null && !attrs.getValue("endDateNorm").equals(""))
			{
				_citationEndDate   = attrs.getValue("endDateNorm");
			}
		}
		else if (localName.equals ("mptr"))
		{
			String value = attrs.getValue ("xlink:href");
			edu.harvard.hul.ois.mets.Div div = new edu.harvard.hul.ois.mets.Div ();
			div.setLABEL (_label);
			div.setTYPE ("INTERMEDIATE");
			
			if(_mode.equals("drs"))
			{
				printSequence = true;
				currLineToPrint = currLineToPrint + value + " ";
			}
			
			try
			{
				long moa2Id = 0;
				if(_mode.equals("drs"))
				{
					moa2Id = Long.parseLong (value);
				}
				
				String filepath = null;
				if(_mode.equals("drs"))
				{
					PreparedStatement st = _conn.prepareStatement (_select);
					st.clearParameters ();
					st.setLong (1, moa2Id);
					ResultSet rs = st.executeQuery ();
					if (rs.next ())
					{
						filepath = rs.getString ("filepath");
					}
					rs.close ();
					st.close ();
				}
				Moa2Handler handler = new Moa2Handler ();
				if(_mode.equals("drs"))
				{
					handler.setMode("drs", _citationId);
					handler.setConnection (_conn);
					handler.setFileoutputPath(_fileOutputPath);
				}
				else
				{
					//must be in local mode
					handler.setMode("local");
					handler.setRootPath(_rootPath);
					filepath = _rootPath + "/" + value;
				}
				handler.setFactory (_factory);
				handler.setJp2Mode(_jp2Mode);
				handler.setFileParent (_fileParent);
				handler.setStructParent (div);
				handler.tempHash = tempHash;
				//handler.setCurrDivOrderLabel(this.getCurrDivOrderLabel());
				handler.setCurrDivPG(this.getCurrDivPG());
				handler.setCurrDivLabel(this.getCurrDivLabel());
				SAXParser parser = _factory.newSAXParser ();
				parser.parse (filepath, handler);
				
				if(_citation && (beginDate!=null || endDate!=null))
				{
					div.setDMDID(String.valueOf("I"+dmdid));
					dmdid++;
					beginDate = null;
					endDate   = null;
				}
				
				_structParent.getContent ().add(div);
				
			}
			catch (Exception e)
			{
				throw new SAXException (e);
			}
		}
		else if (_citation || _citationLeaf)
		{
			if (localName.equals ("Creator"))
			{
				_isCreator = true;
				String value = attrs.getValue ("", "Role");
				if (value != null && !value.equals ("author"))
				{
					_isCreator = false;
				}
			}
			else if (localName.equals ("StructMap"))
			{
				_fileParent   = new FileGrp ();
				_structParent = new edu.harvard.hul.ois.mets.Div ();
				((edu.harvard.hul.ois.mets.Div) _structParent).setTYPE ("CITATION");
				if(_twoCitationDmdsec)
				{
					((edu.harvard.hul.ois.mets.Div) _structParent).setDMDID ("C0 C1");
				}
				else
				{
					((edu.harvard.hul.ois.mets.Div) _structParent).setDMDID ("C0");
				}
			}
		}
    }
}
