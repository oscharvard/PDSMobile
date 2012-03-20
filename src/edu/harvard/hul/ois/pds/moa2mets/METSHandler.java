/**********************************************************************
 * Copyright (c) 2002 by the President and Fellows of Harvard College
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

//import edu.harvard.hul.ois.drs.db.*;
//import edu.harvard.hul.ois.drs.loader.*;
import edu.harvard.hul.ois.xml.XmlWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * METS pre-processor xml handler.
 *
 * @author Julian Marinus
 */

public class METSHandler
    implements ContentHandler, ErrorHandler
{
    /**
     * List of ID's for wrapped metadata <mdWrap> element.
     */
    private ArrayList mdWrapIDList = new ArrayList();
    /**
     * List of ID's for <file> elements with <FContent> elements.
     */
    private ArrayList fContentIDList = new ArrayList();

    /**
     * List of xlink:href's for <FLocat> elements.
     */
    private ArrayList fLocatXlinkHrefList = new ArrayList();

    /** mets type */
    private String type;
    /** current file id */
    private String curFileID;
    /** buffer */
    private StringBuffer collector;
    /** name of the current file */
    private String curfile;

    /**
     * Constructor for METS parser.
     */

    public METSHandler() {
    }

    /** Implements org.xml.sax.ContentHandler method. */
    public void characters(char ch[],
			   int start,
			   int length)
    {
	if (collector == null) {
	    collector = new StringBuffer();
	}
	collector.append(ch,start,length);
    }

    /** Implements org.xml.saxContentHandler method. */
    public void endDocument() { }

    /** Implements org.xml.sax.ContentHandler method. */
    public void endElement(String namespaceURI,
			   String localName,
			   String qName)
	throws SAXException
    {
	collector = null;
        
        if (localName.equals("file")) {
            curFileID = null;
        }

    }

    /** Implements org.xml.sax.ContentHandler method. */
    public void endPrefixMapping(String prefix) { }

    /** Implements org.xml.sax.ErrorHandler method. */
    public void error(SAXParseException exception)
	throws SAXException
    {
	throw exception;
    }

    /** Implements org.xml.sax.ErrorHandler method. */
    public void fatalError(SAXParseException exception)
	throws SAXException
    {
	throw exception;
    }

    /**
     * Returns the node type of this METS if parse() has been called.
     *
     * @return type of the METS, null if it is not specified in
     *         the xml
     */

    public String getType()
    {
	return type;
    }

    /**
     * Returns a list of the IDs for &lt;mdWrap&gt; elements found
     * in the METS document.
     *
     * @return list of mdWrap IDs as Strings, null if none exist
     */

    public ArrayList getMdWrapIDList()
    {
        return (mdWrapIDList.size() < 1 ? null : mdWrapIDList);
    }

    /**
     * Returns a list of the IDs for &lt;file&gt; elements found
     * in the METS document, if they contain &lt;FContent&gt;
     * elements.
     *
     * @return list of FContent IDs as Strings, null if none exist
     */

    public ArrayList getFContentIDList()
    {
        return (fContentIDList.size() < 1 ? null : fContentIDList);
    }

    /**
     * Returns a list of the xlink:href's for &lt;FLocat&gt; elements found
     * in the METS document.
     * elements.
     *
     * @return list of xlink:href values as Strings, null if none exist
     */

    public ArrayList getFLocatXlinkHrefList()
    {
        return (fLocatXlinkHrefList.size() < 1 ? null : fLocatXlinkHrefList);
    }

    /**
     * Replaces directory separator used in DRS METS with standard
     * file separator:
     *
     * <pre>dir1-_-file1 to dir1/file1</pre>
     *
     * and space escape sequence with a space:
     *
     * <pre>a-__-b to a b</pre>
     *
     * @param path file path to decode
     * @return decoded file path
     */

    public static String decodePath(String path)
    {
        return XmlWriter.decodeID(path);
    }

    /**
     * Calls decodePath() on every element in the list.
     */

    public static ArrayList decodePaths(ArrayList paths)
    {
        if (paths == null || paths.size() < 1) {
            return paths;
        }

        ArrayList newPaths = new ArrayList();
        for (int i=0;i<paths.size();i++) {
            String path = (String)paths.get(i);
            newPaths.add(decodePath(path));
        }
        return newPaths;
    }

    /**
     * Replaces directory separator used in DRS METS with standard
     * file separator:
     *
     * <pre>dir1/file1 to dir1-_-file1</pre>
     *
     * and space escape sequence with a space:
     *
     * <pre>a b to a-__-b</pre>
     *
     * @param path file path to encode
     * @return encoded file path
     */

    public static String encodePath(String path)
    {
        return XmlWriter.encodeID(path);
    }

    /**
     * Calls encodePath() on every element in the list.
     */

    public static ArrayList encodePaths(ArrayList paths)
    {
        if (paths == null || paths.size() < 1) {
            return paths;
        }

        ArrayList newPaths = new ArrayList();
        for (int i=0;i<paths.size();i++) {
            String path = (String)paths.get(i);
            newPaths.add(encodePath(path));
        }
        return newPaths;
    }

    /** Implements org.xml.sax.ContentHandler method. */
    public void ignorableWhitespace(char[] ch, int start, int length) { }
    /** Implements org.xml.sax.ContentHandler method. */
    public void processingInstruction(String target, String data) { }


    /** Implements org.xml.sax.ContentHandler method. */
    public void setDocumentLocator(Locator locator) { }
    /** Implements org.xml.sax.ContentHandler method. */
    public void skippedEntity(String name) { }
    /** Implements org.xml.sax.ContentHandler method. */
    public void startDocument() { }

    /**
     * Start of xml element.
     *
     * @param uri tag name
     * @param localName tag name
     * @param qName tag name
     * @param attributes tag attributes
     *
     * @throws SAXException if attributes of current tag are
     *         invalid
     */

    public void startElement(String uri,
			     String localName,
			     String qName,
			     Attributes attributes)
	throws SAXException
    {
	if (localName.equalsIgnoreCase("mets")) {
	    // determine if this is a citation, intermediate or
	    // leaf node
	    this.type = attributes.getValue("TYPE");
	    if (this.type == null) {
		throw new SAXException("Attributes for tag " +
				       localName + " contains no " +
				       "TYPE in METSHandler");
	    }
	}
        else if (localName.equals("file")) {
            String id = attributes.getValue("ID");
            curFileID = id;
        }
        else if (localName.equals("FLocat")) {
            String xlinkHref = attributes.getValue("xlink:href");
            if (xlinkHref != null) {
                fLocatXlinkHrefList.add(xlinkHref);
            }
        }
        else if (localName.equals("FContent")) {
            if (curFileID != null) {
                fContentIDList.add(curFileID);
            }
        }
        else if (localName.equals("mdWrap")) {
            String id = attributes.getValue("ID");
            if (id != null) {
                mdWrapIDList.add(id);
            }
        }
    }

    /** Implements org.xml.sax.ContentHandler method. */
    public void startPrefixMapping(String prefix, String uri) { }

    /** Implements org.xml.sax.ErrorHandler method. */
    public void warning(SAXParseException exception)
	throws SAXException
    {
	throw exception;
    }
}
