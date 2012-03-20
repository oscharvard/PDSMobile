/**********************************************************************
 * Page Delivery Service (PDS) servlet
 * Copyright 2004 by the President and Fellows of Harvard College
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, butdate
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
package edu.harvard.hul.ois.pds.index;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class MetsTitleParser extends DefaultHandler {
	
	private StringBuffer _buffer;
	private String label;
	private String name;
	private String title;

	public void characters(char[] buf, int offset, int len) {
		if (_buffer != null) {
			_buffer.append(buf, offset, len);
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("mods:displayForm")) {
			name = _buffer.toString();
			_buffer = null;
		} else if (qName.equals("mods:title")) {
			title = _buffer.toString();
			_buffer = null;
		} 
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) {
		if (qName.equals("mets")) {
			label = atts.getValue("LABEL");
		}
		if (qName.equals("mods:displayForm")) {
			_buffer = new StringBuffer();
		} else if (qName.equals("mods:title")) {
			_buffer = new StringBuffer();
		} 
	}
	
	public String getDisplayLabel() {
		if (label == null || label.equals("")) {
			if ((name != null && !name.equals("")) && (title != null) && !title.equals("")) {
				if (name.endsWith(".")) {
					name = name + " " + title;
				} else {
					name = name + ". " + title;
				}
			} else if (title != null && !title.equals("")) {
				name = title;
			} else if (name != null && !name.equals("")) {
				//Do nothing, name is already set
			} else {
				name = "No Label";
			}
			return name;
		} else {
			return label;
		}
	}


}
