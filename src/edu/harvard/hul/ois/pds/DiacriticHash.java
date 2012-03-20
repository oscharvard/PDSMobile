/**********************************************************************
 * Page Delivery Service (PDS) servlet
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

package edu.harvard.hul.ois.pds;

import java.util.Hashtable;

public class DiacriticHash
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/
    private Hashtable<Character, Character> _hash;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/
    public DiacriticHash ()
    {
        _hash = new Hashtable<Character, Character>();
		_hash.put(new Character((char)0x00c0), new Character((char)0x0041));
	    _hash.put(new Character((char)0x00c1), new Character((char)0x0041));
	    _hash.put(new Character((char)0x00c2), new Character((char)0x0041));
	    _hash.put(new Character((char)0x00c3), new Character((char)0x0041));
	    _hash.put(new Character((char)0x00c4), new Character((char)0x0041));
	    _hash.put(new Character((char)0x00c5), new Character((char)0x0041));
	    _hash.put(new Character((char)0x00c7), new Character((char)0x0043));
	    _hash.put(new Character((char)0x00c8), new Character((char)0x0045));
	    _hash.put(new Character((char)0x00c9), new Character((char)0x0045));
	    _hash.put(new Character((char)0x00ca), new Character((char)0x0045));
	    _hash.put(new Character((char)0x00cb), new Character((char)0x0045));
	    _hash.put(new Character((char)0x00cc), new Character((char)0x0049));
	    _hash.put(new Character((char)0x00cd), new Character((char)0x0049));
	    _hash.put(new Character((char)0x00ce), new Character((char)0x0049));
	    _hash.put(new Character((char)0x00cf), new Character((char)0x0049));
	    _hash.put(new Character((char)0x00d1), new Character((char)0x004e));
	    _hash.put(new Character((char)0x00d2), new Character((char)0x004f));
	    _hash.put(new Character((char)0x00d3), new Character((char)0x004f));
	    _hash.put(new Character((char)0x00d4), new Character((char)0x004f));
	    _hash.put(new Character((char)0x00d5), new Character((char)0x004f));
	    _hash.put(new Character((char)0x00d6), new Character((char)0x004f));
	    _hash.put(new Character((char)0x00d9), new Character((char)0x0055));
	    _hash.put(new Character((char)0x00da), new Character((char)0x0055));
	    _hash.put(new Character((char)0x00db), new Character((char)0x0055));
	    _hash.put(new Character((char)0x00dc), new Character((char)0x0055));
	    _hash.put(new Character((char)0x00dd), new Character((char)0x0059));
	    _hash.put(new Character((char)0x00e0), new Character((char)0x0061));
	    _hash.put(new Character((char)0x00e1), new Character((char)0x0061));
	    _hash.put(new Character((char)0x00e2), new Character((char)0x0061));
	    _hash.put(new Character((char)0x00e3), new Character((char)0x0061));
	    _hash.put(new Character((char)0x00e4), new Character((char)0x0061));
	    _hash.put(new Character((char)0x00e5), new Character((char)0x0061));
	    _hash.put(new Character((char)0x00e7), new Character((char)0x0063));
	    _hash.put(new Character((char)0x00e8), new Character((char)0x0065));
	    _hash.put(new Character((char)0x00e9), new Character((char)0x0065));
	    _hash.put(new Character((char)0x00ea), new Character((char)0x0065));
	    _hash.put(new Character((char)0x00eb), new Character((char)0x0065));
	    _hash.put(new Character((char)0x00ec), new Character((char)0x0069));
	    _hash.put(new Character((char)0x00ed), new Character((char)0x0069));
	    _hash.put(new Character((char)0x00ee), new Character((char)0x0069));
	    _hash.put(new Character((char)0x00ef), new Character((char)0x0069));
	    _hash.put(new Character((char)0x00f1), new Character((char)0x006e));
	    _hash.put(new Character((char)0x00f2), new Character((char)0x006f));
	    _hash.put(new Character((char)0x00f3), new Character((char)0x006f));
	    _hash.put(new Character((char)0x00f4), new Character((char)0x006f));
	    _hash.put(new Character((char)0x00f5), new Character((char)0x006f));
	    _hash.put(new Character((char)0x00f6), new Character((char)0x006f));
	    _hash.put(new Character((char)0x00f9), new Character((char)0x0075));
	    _hash.put(new Character((char)0x00fa), new Character((char)0x0075));
	    _hash.put(new Character((char)0x00fb), new Character((char)0x0075));
	    _hash.put(new Character((char)0x00fc), new Character((char)0x0075));
	    _hash.put(new Character((char)0x00fd), new Character((char)0x0079));
	    _hash.put(new Character((char)0x00ff), new Character((char)0x0079));
	    _hash.put(new Character((char)0x0100), new Character((char)0x0041));
	    _hash.put(new Character((char)0x0101), new Character((char)0x0061));
	    _hash.put(new Character((char)0x0102), new Character((char)0x0041));
	    _hash.put(new Character((char)0x0103), new Character((char)0x0061));
	    _hash.put(new Character((char)0x0104), new Character((char)0x0041));
	    _hash.put(new Character((char)0x0105), new Character((char)0x0061));
	    _hash.put(new Character((char)0x0106), new Character((char)0x0043));
	    _hash.put(new Character((char)0x0107), new Character((char)0x0063));
	    _hash.put(new Character((char)0x0108), new Character((char)0x0043));
	    _hash.put(new Character((char)0x0109), new Character((char)0x0063));
	    _hash.put(new Character((char)0x010a), new Character((char)0x0043));
	    _hash.put(new Character((char)0x010b), new Character((char)0x0063));
	    _hash.put(new Character((char)0x010c), new Character((char)0x0043));
	    _hash.put(new Character((char)0x010d), new Character((char)0x0063));
	    _hash.put(new Character((char)0x010e), new Character((char)0x0044));
	    _hash.put(new Character((char)0x010f), new Character((char)0x0064));
	    _hash.put(new Character((char)0x0112), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0113), new Character((char)0x0065));
	    _hash.put(new Character((char)0x0114), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0115), new Character((char)0x0065));
	    _hash.put(new Character((char)0x0116), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0117), new Character((char)0x0065));
	    _hash.put(new Character((char)0x0118), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0119), new Character((char)0x0065));
	    _hash.put(new Character((char)0x011a), new Character((char)0x0045));
	    _hash.put(new Character((char)0x011b), new Character((char)0x0065));
	    _hash.put(new Character((char)0x011c), new Character((char)0x0047));
	    _hash.put(new Character((char)0x011d), new Character((char)0x0067));
	    _hash.put(new Character((char)0x011e), new Character((char)0x0047));
	    _hash.put(new Character((char)0x011f), new Character((char)0x0067));
	    _hash.put(new Character((char)0x0120), new Character((char)0x0047));
	    _hash.put(new Character((char)0x0121), new Character((char)0x0067));
	    _hash.put(new Character((char)0x0122), new Character((char)0x0047));
	    _hash.put(new Character((char)0x0123), new Character((char)0x0067));
	    _hash.put(new Character((char)0x0124), new Character((char)0x0048));
	    _hash.put(new Character((char)0x0125), new Character((char)0x0068));
	    _hash.put(new Character((char)0x0128), new Character((char)0x0049));
	    _hash.put(new Character((char)0x0129), new Character((char)0x0069));
	    _hash.put(new Character((char)0x012a), new Character((char)0x0049));
	    _hash.put(new Character((char)0x012b), new Character((char)0x0069));
	    _hash.put(new Character((char)0x012c), new Character((char)0x0049));
	    _hash.put(new Character((char)0x012d), new Character((char)0x0069));
	    _hash.put(new Character((char)0x012e), new Character((char)0x0049));
	    _hash.put(new Character((char)0x012f), new Character((char)0x0069));
	    _hash.put(new Character((char)0x0130), new Character((char)0x0049));
	    _hash.put(new Character((char)0x0134), new Character((char)0x004a));
	    _hash.put(new Character((char)0x0135), new Character((char)0x006a));
	    _hash.put(new Character((char)0x0136), new Character((char)0x004b));
	    _hash.put(new Character((char)0x0137), new Character((char)0x006b));
	    _hash.put(new Character((char)0x0139), new Character((char)0x004c));
	    _hash.put(new Character((char)0x013a), new Character((char)0x006c));
	    _hash.put(new Character((char)0x013b), new Character((char)0x004c));
	    _hash.put(new Character((char)0x013c), new Character((char)0x006c));
	    _hash.put(new Character((char)0x013d), new Character((char)0x004c));
	    _hash.put(new Character((char)0x013e), new Character((char)0x006c));
	    _hash.put(new Character((char)0x0143), new Character((char)0x004e));
	    _hash.put(new Character((char)0x0144), new Character((char)0x006e));
	    _hash.put(new Character((char)0x0145), new Character((char)0x004e));
	    _hash.put(new Character((char)0x0146), new Character((char)0x006e));
	    _hash.put(new Character((char)0x0147), new Character((char)0x004e));
	    _hash.put(new Character((char)0x0148), new Character((char)0x006e));
	    _hash.put(new Character((char)0x014c), new Character((char)0x004f));
	    _hash.put(new Character((char)0x014d), new Character((char)0x006f));
	    _hash.put(new Character((char)0x014e), new Character((char)0x004f));
	    _hash.put(new Character((char)0x014f), new Character((char)0x006f));
	    _hash.put(new Character((char)0x0150), new Character((char)0x004f));
	    _hash.put(new Character((char)0x0151), new Character((char)0x006f));
	    _hash.put(new Character((char)0x0154), new Character((char)0x0052));
	    _hash.put(new Character((char)0x0155), new Character((char)0x0072));
	    _hash.put(new Character((char)0x0156), new Character((char)0x0052));
	    _hash.put(new Character((char)0x0157), new Character((char)0x0072));
	    _hash.put(new Character((char)0x0158), new Character((char)0x0052));
	    _hash.put(new Character((char)0x0159), new Character((char)0x0072));
	    _hash.put(new Character((char)0x015a), new Character((char)0x0053));
	    _hash.put(new Character((char)0x015b), new Character((char)0x0073));
	    _hash.put(new Character((char)0x015c), new Character((char)0x0053));
	    _hash.put(new Character((char)0x015d), new Character((char)0x0073));
	    _hash.put(new Character((char)0x015e), new Character((char)0x0053));
	    _hash.put(new Character((char)0x015f), new Character((char)0x0073));
	    _hash.put(new Character((char)0x0160), new Character((char)0x0053));
	    _hash.put(new Character((char)0x0161), new Character((char)0x0073));
	    _hash.put(new Character((char)0x0162), new Character((char)0x0054));
	    _hash.put(new Character((char)0x0163), new Character((char)0x0074));
	    _hash.put(new Character((char)0x0164), new Character((char)0x0054));
	    _hash.put(new Character((char)0x0165), new Character((char)0x0074));
	    _hash.put(new Character((char)0x0168), new Character((char)0x0055));
	    _hash.put(new Character((char)0x0169), new Character((char)0x0075));
	    _hash.put(new Character((char)0x016a), new Character((char)0x0055));
	    _hash.put(new Character((char)0x016b), new Character((char)0x0075));
	    _hash.put(new Character((char)0x016c), new Character((char)0x0055));
	    _hash.put(new Character((char)0x016d), new Character((char)0x0075));
	    _hash.put(new Character((char)0x016e), new Character((char)0x0055));
	    _hash.put(new Character((char)0x016f), new Character((char)0x0075));
	    _hash.put(new Character((char)0x0170), new Character((char)0x0055));
	    _hash.put(new Character((char)0x0171), new Character((char)0x0075));
	    _hash.put(new Character((char)0x0172), new Character((char)0x0055));
	    _hash.put(new Character((char)0x0173), new Character((char)0x0075));
	    _hash.put(new Character((char)0x0174), new Character((char)0x0057));
	    _hash.put(new Character((char)0x0175), new Character((char)0x0077));
	    _hash.put(new Character((char)0x0176), new Character((char)0x0059));
	    _hash.put(new Character((char)0x0177), new Character((char)0x0079));
	    _hash.put(new Character((char)0x0178), new Character((char)0x0059));
	    _hash.put(new Character((char)0x0179), new Character((char)0x005a));
	    _hash.put(new Character((char)0x017a), new Character((char)0x007a));
	    _hash.put(new Character((char)0x017b), new Character((char)0x005a));
	    _hash.put(new Character((char)0x017c), new Character((char)0x007a));
	    _hash.put(new Character((char)0x017d), new Character((char)0x005a));
	    _hash.put(new Character((char)0x017e), new Character((char)0x007a));
	    _hash.put(new Character((char)0x01a0), new Character((char)0x004f));
	    _hash.put(new Character((char)0x01a1), new Character((char)0x006f));
	    _hash.put(new Character((char)0x01af), new Character((char)0x0055));
	    _hash.put(new Character((char)0x01b0), new Character((char)0x0075));
	    _hash.put(new Character((char)0x01cd), new Character((char)0x0041));
	    _hash.put(new Character((char)0x01ce), new Character((char)0x0061));
	    _hash.put(new Character((char)0x01cf), new Character((char)0x0049));
	    _hash.put(new Character((char)0x01d0), new Character((char)0x0069));
	    _hash.put(new Character((char)0x01d1), new Character((char)0x004f));
	    _hash.put(new Character((char)0x01d2), new Character((char)0x006f));
	    _hash.put(new Character((char)0x01d3), new Character((char)0x0055));
	    _hash.put(new Character((char)0x01d4), new Character((char)0x0075));
	    _hash.put(new Character((char)0x01e6), new Character((char)0x0047));
	    _hash.put(new Character((char)0x01e7), new Character((char)0x0067));
	    _hash.put(new Character((char)0x01e8), new Character((char)0x004b));
	    _hash.put(new Character((char)0x01e9), new Character((char)0x006b));
	    _hash.put(new Character((char)0x01ea), new Character((char)0x004f));
	    _hash.put(new Character((char)0x01eb), new Character((char)0x006f));
	    _hash.put(new Character((char)0x01f0), new Character((char)0x006a));
	    _hash.put(new Character((char)0x01f4), new Character((char)0x0047));
	    _hash.put(new Character((char)0x01f5), new Character((char)0x0067));
	    _hash.put(new Character((char)0x01f8), new Character((char)0x004e));
	    _hash.put(new Character((char)0x01f9), new Character((char)0x006e));
	    _hash.put(new Character((char)0x0200), new Character((char)0x0041));
	    _hash.put(new Character((char)0x0201), new Character((char)0x0061));
	    _hash.put(new Character((char)0x0202), new Character((char)0x0041));
	    _hash.put(new Character((char)0x0203), new Character((char)0x0061));
	    _hash.put(new Character((char)0x0204), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0205), new Character((char)0x0065));
	    _hash.put(new Character((char)0x0206), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0207), new Character((char)0x0065));
	    _hash.put(new Character((char)0x0208), new Character((char)0x0049));
	    _hash.put(new Character((char)0x0209), new Character((char)0x0069));
	    _hash.put(new Character((char)0x020a), new Character((char)0x0049));
	    _hash.put(new Character((char)0x020b), new Character((char)0x0069));
	    _hash.put(new Character((char)0x020c), new Character((char)0x004f));
	    _hash.put(new Character((char)0x020d), new Character((char)0x006f));
	    _hash.put(new Character((char)0x020e), new Character((char)0x004f));
	    _hash.put(new Character((char)0x020f), new Character((char)0x006f));
	    _hash.put(new Character((char)0x0210), new Character((char)0x0052));
	    _hash.put(new Character((char)0x0211), new Character((char)0x0072));
	    _hash.put(new Character((char)0x0212), new Character((char)0x0052));
	    _hash.put(new Character((char)0x0213), new Character((char)0x0072));
	    _hash.put(new Character((char)0x0214), new Character((char)0x0055));
	    _hash.put(new Character((char)0x0215), new Character((char)0x0075));
	    _hash.put(new Character((char)0x0216), new Character((char)0x0055));
	    _hash.put(new Character((char)0x0217), new Character((char)0x0075));
	    _hash.put(new Character((char)0x0218), new Character((char)0x0053));
	    _hash.put(new Character((char)0x0219), new Character((char)0x0073));
	    _hash.put(new Character((char)0x021a), new Character((char)0x0054));
	    _hash.put(new Character((char)0x021b), new Character((char)0x0074));
	    _hash.put(new Character((char)0x021e), new Character((char)0x0048));
	    _hash.put(new Character((char)0x021f), new Character((char)0x0068));
	    _hash.put(new Character((char)0x0226), new Character((char)0x0041));
	    _hash.put(new Character((char)0x0227), new Character((char)0x0061));
	    _hash.put(new Character((char)0x0228), new Character((char)0x0045));
	    _hash.put(new Character((char)0x0229), new Character((char)0x0065));
	    _hash.put(new Character((char)0x022e), new Character((char)0x004f));
	    _hash.put(new Character((char)0x022f), new Character((char)0x006f));
	    _hash.put(new Character((char)0x0232), new Character((char)0x0059));
	    _hash.put(new Character((char)0x0233), new Character((char)0x0079));
	    _hash.put(new Character((char)0x037e), new Character((char)0x003b));
	    _hash.put(new Character((char)0x1e00), new Character((char)0x0041));
	    _hash.put(new Character((char)0x1e01), new Character((char)0x0061));
	    _hash.put(new Character((char)0x1e02), new Character((char)0x0042));
	    _hash.put(new Character((char)0x1e03), new Character((char)0x0062));
	    _hash.put(new Character((char)0x1e04), new Character((char)0x0042));
	    _hash.put(new Character((char)0x1e05), new Character((char)0x0062));
	    _hash.put(new Character((char)0x1e06), new Character((char)0x0042));
	    _hash.put(new Character((char)0x1e07), new Character((char)0x0062));
	    _hash.put(new Character((char)0x1e0a), new Character((char)0x0044));
	    _hash.put(new Character((char)0x1e0b), new Character((char)0x0064));
	    _hash.put(new Character((char)0x1e0c), new Character((char)0x0044));
	    _hash.put(new Character((char)0x1e0d), new Character((char)0x0064));
	    _hash.put(new Character((char)0x1e0e), new Character((char)0x0044));
	    _hash.put(new Character((char)0x1e0f), new Character((char)0x0064));
	    _hash.put(new Character((char)0x1e10), new Character((char)0x0044));
	    _hash.put(new Character((char)0x1e11), new Character((char)0x0064));
	    _hash.put(new Character((char)0x1e12), new Character((char)0x0044));
	    _hash.put(new Character((char)0x1e13), new Character((char)0x0064));
	    _hash.put(new Character((char)0x1e18), new Character((char)0x0045));
	    _hash.put(new Character((char)0x1e19), new Character((char)0x0065));
	    _hash.put(new Character((char)0x1e1a), new Character((char)0x0045));
	    _hash.put(new Character((char)0x1e1b), new Character((char)0x0065));
	    _hash.put(new Character((char)0x1e1e), new Character((char)0x0046));
	    _hash.put(new Character((char)0x1e1f), new Character((char)0x0066));
	    _hash.put(new Character((char)0x1e20), new Character((char)0x0047));
	    _hash.put(new Character((char)0x1e21), new Character((char)0x0067));
	    _hash.put(new Character((char)0x1e22), new Character((char)0x0048));
	    _hash.put(new Character((char)0x1e23), new Character((char)0x0068));
	    _hash.put(new Character((char)0x1e24), new Character((char)0x0048));
	    _hash.put(new Character((char)0x1e25), new Character((char)0x0068));
	    _hash.put(new Character((char)0x1e26), new Character((char)0x0048));
	    _hash.put(new Character((char)0x1e27), new Character((char)0x0068));
	    _hash.put(new Character((char)0x1e28), new Character((char)0x0048));
	    _hash.put(new Character((char)0x1e29), new Character((char)0x0068));
	    _hash.put(new Character((char)0x1e2a), new Character((char)0x0048));
	    _hash.put(new Character((char)0x1e2b), new Character((char)0x0068));
	    _hash.put(new Character((char)0x1e2c), new Character((char)0x0049));
	    _hash.put(new Character((char)0x1e2d), new Character((char)0x0069));
	    _hash.put(new Character((char)0x1e30), new Character((char)0x004b));
	    _hash.put(new Character((char)0x1e31), new Character((char)0x006b));
	    _hash.put(new Character((char)0x1e32), new Character((char)0x004b));
	    _hash.put(new Character((char)0x1e33), new Character((char)0x006b));
	    _hash.put(new Character((char)0x1e34), new Character((char)0x004b));
	    _hash.put(new Character((char)0x1e35), new Character((char)0x006b));
	    _hash.put(new Character((char)0x1e36), new Character((char)0x004c));
	    _hash.put(new Character((char)0x1e37), new Character((char)0x006c));
	    _hash.put(new Character((char)0x1e3a), new Character((char)0x004c));
	    _hash.put(new Character((char)0x1e3b), new Character((char)0x006c));
	    _hash.put(new Character((char)0x1e3c), new Character((char)0x004c));
	    _hash.put(new Character((char)0x1e3d), new Character((char)0x006c));
	    _hash.put(new Character((char)0x1e3e), new Character((char)0x004d));
	    _hash.put(new Character((char)0x1e3f), new Character((char)0x006d));
	    _hash.put(new Character((char)0x1e40), new Character((char)0x004d));
	    _hash.put(new Character((char)0x1e41), new Character((char)0x006d));
	    _hash.put(new Character((char)0x1e42), new Character((char)0x004d));
	    _hash.put(new Character((char)0x1e43), new Character((char)0x006d));
	    _hash.put(new Character((char)0x1e44), new Character((char)0x004e));
	    _hash.put(new Character((char)0x1e45), new Character((char)0x006e));
	    _hash.put(new Character((char)0x1e46), new Character((char)0x004e));
	    _hash.put(new Character((char)0x1e47), new Character((char)0x006e));
	    _hash.put(new Character((char)0x1e48), new Character((char)0x004e));
	    _hash.put(new Character((char)0x1e49), new Character((char)0x006e));
	    _hash.put(new Character((char)0x1e4a), new Character((char)0x004e));
	    _hash.put(new Character((char)0x1e4b), new Character((char)0x006e));
	    _hash.put(new Character((char)0x1e54), new Character((char)0x0050));
	    _hash.put(new Character((char)0x1e55), new Character((char)0x0070));
	    _hash.put(new Character((char)0x1e56), new Character((char)0x0050));
	    _hash.put(new Character((char)0x1e57), new Character((char)0x0070));
	    _hash.put(new Character((char)0x1e58), new Character((char)0x0052));
	    _hash.put(new Character((char)0x1e59), new Character((char)0x0072));
	    _hash.put(new Character((char)0x1e5a), new Character((char)0x0052));
	    _hash.put(new Character((char)0x1e5b), new Character((char)0x0072));
	    _hash.put(new Character((char)0x1e5e), new Character((char)0x0052));
	    _hash.put(new Character((char)0x1e5f), new Character((char)0x0072));
	    _hash.put(new Character((char)0x1e60), new Character((char)0x0053));
	    _hash.put(new Character((char)0x1e61), new Character((char)0x0073));
	    _hash.put(new Character((char)0x1e62), new Character((char)0x0053));
	    _hash.put(new Character((char)0x1e63), new Character((char)0x0073));
	    _hash.put(new Character((char)0x1e6a), new Character((char)0x0054));
	    _hash.put(new Character((char)0x1e6b), new Character((char)0x0074));
	    _hash.put(new Character((char)0x1e6c), new Character((char)0x0054));
	    _hash.put(new Character((char)0x1e6d), new Character((char)0x0074));
	    _hash.put(new Character((char)0x1e6e), new Character((char)0x0054));
	    _hash.put(new Character((char)0x1e6f), new Character((char)0x0074));
	    _hash.put(new Character((char)0x1e70), new Character((char)0x0054));
	    _hash.put(new Character((char)0x1e71), new Character((char)0x0074));
	    _hash.put(new Character((char)0x1e72), new Character((char)0x0055));
	    _hash.put(new Character((char)0x1e73), new Character((char)0x0075));
	    _hash.put(new Character((char)0x1e74), new Character((char)0x0055));
	    _hash.put(new Character((char)0x1e75), new Character((char)0x0075));
	    _hash.put(new Character((char)0x1e76), new Character((char)0x0055));
	    _hash.put(new Character((char)0x1e77), new Character((char)0x0075));
	    _hash.put(new Character((char)0x1e7c), new Character((char)0x0056));
	    _hash.put(new Character((char)0x1e7d), new Character((char)0x0076));
	    _hash.put(new Character((char)0x1e7e), new Character((char)0x0056));
	    _hash.put(new Character((char)0x1e7f), new Character((char)0x0076));
	    _hash.put(new Character((char)0x1e80), new Character((char)0x0057));
	    _hash.put(new Character((char)0x1e81), new Character((char)0x0077));
	    _hash.put(new Character((char)0x1e82), new Character((char)0x0057));
	    _hash.put(new Character((char)0x1e83), new Character((char)0x0077));
	    _hash.put(new Character((char)0x1e84), new Character((char)0x0057));
	    _hash.put(new Character((char)0x1e85), new Character((char)0x0077));
	    _hash.put(new Character((char)0x1e86), new Character((char)0x0057));
	    _hash.put(new Character((char)0x1e87), new Character((char)0x0077));
	    _hash.put(new Character((char)0x1e88), new Character((char)0x0057));
	    _hash.put(new Character((char)0x1e89), new Character((char)0x0077));
	    _hash.put(new Character((char)0x1e8a), new Character((char)0x0058));
	    _hash.put(new Character((char)0x1e8b), new Character((char)0x0078));
	    _hash.put(new Character((char)0x1e8c), new Character((char)0x0058));
	    _hash.put(new Character((char)0x1e8d), new Character((char)0x0078));
	    _hash.put(new Character((char)0x1e8e), new Character((char)0x0059));
	    _hash.put(new Character((char)0x1e8f), new Character((char)0x0079));
	    _hash.put(new Character((char)0x1e90), new Character((char)0x005a));
	    _hash.put(new Character((char)0x1e91), new Character((char)0x007a));
	    _hash.put(new Character((char)0x1e92), new Character((char)0x005a));
	    _hash.put(new Character((char)0x1e93), new Character((char)0x007a));
	    _hash.put(new Character((char)0x1e94), new Character((char)0x005a));
	    _hash.put(new Character((char)0x1e95), new Character((char)0x007a));
	    _hash.put(new Character((char)0x1e96), new Character((char)0x0068));
	    _hash.put(new Character((char)0x1e97), new Character((char)0x0074));
	    _hash.put(new Character((char)0x1e98), new Character((char)0x0077));
	    _hash.put(new Character((char)0x1e99), new Character((char)0x0079));
	    _hash.put(new Character((char)0x1ea0), new Character((char)0x0041));
	    _hash.put(new Character((char)0x1ea1), new Character((char)0x0061));
	    _hash.put(new Character((char)0x1ea2), new Character((char)0x0041));
	    _hash.put(new Character((char)0x1ea3), new Character((char)0x0061));
	    _hash.put(new Character((char)0x1eb8), new Character((char)0x0045));
	    _hash.put(new Character((char)0x1eb9), new Character((char)0x0065));
	    _hash.put(new Character((char)0x1eba), new Character((char)0x0045));
	    _hash.put(new Character((char)0x1ebb), new Character((char)0x0065));
	    _hash.put(new Character((char)0x1ebc), new Character((char)0x0045));
	    _hash.put(new Character((char)0x1ebd), new Character((char)0x0065));
	    _hash.put(new Character((char)0x1ec8), new Character((char)0x0049));
	    _hash.put(new Character((char)0x1ec9), new Character((char)0x0069));
	    _hash.put(new Character((char)0x1eca), new Character((char)0x0049));
	    _hash.put(new Character((char)0x1ecb), new Character((char)0x0069));
	    _hash.put(new Character((char)0x1ecc), new Character((char)0x004f));
	    _hash.put(new Character((char)0x1ecd), new Character((char)0x006f));
	    _hash.put(new Character((char)0x1ece), new Character((char)0x004f));
	    _hash.put(new Character((char)0x1ecf), new Character((char)0x006f));
	    _hash.put(new Character((char)0x1ee4), new Character((char)0x0055));
	    _hash.put(new Character((char)0x1ee5), new Character((char)0x0075));
	    _hash.put(new Character((char)0x1ee6), new Character((char)0x0055));
	    _hash.put(new Character((char)0x1ee7), new Character((char)0x0075));
	    _hash.put(new Character((char)0x1ef2), new Character((char)0x0059));
	    _hash.put(new Character((char)0x1ef3), new Character((char)0x0079));
	    _hash.put(new Character((char)0x1ef4), new Character((char)0x0059));
	    _hash.put(new Character((char)0x1ef5), new Character((char)0x0079));
	    _hash.put(new Character((char)0x1ef6), new Character((char)0x0059));
	    _hash.put(new Character((char)0x1ef7), new Character((char)0x0079));
	    _hash.put(new Character((char)0x1ef8), new Character((char)0x0059));
	    _hash.put(new Character((char)0x1ef9), new Character((char)0x0079));
	    _hash.put(new Character((char)0x1fef), new Character((char)0x0060));
	    _hash.put(new Character((char)0x212a), new Character((char)0x004b));
	    _hash.put(new Character((char)0x2260), new Character((char)0x003d));
	    _hash.put(new Character((char)0x226e), new Character((char)0x003c));
	    _hash.put(new Character((char)0x226f), new Character((char)0x003e));
	    _hash.put(new Character((char)0x0110), new Character((char)0x0044));
	    _hash.put(new Character((char)0x0111), new Character((char)0x0064));
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/
    public Character getChar (char c)
    {
		return (Character)_hash.get(new Character(c));
    }

}
