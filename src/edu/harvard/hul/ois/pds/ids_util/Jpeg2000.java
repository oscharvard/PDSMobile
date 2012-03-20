/**********************************************************************
 * Copyright (c) 2003 by the President and Fellows of Harvard College
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

package edu.harvard.hul.ois.pds.ids_util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import jj2000.j2k.io.BEBufferedRandomAccessFile;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.decoder.Decoder;
import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.codestream.HeaderInfo;
import jj2000.j2k.codestream.reader.HeaderDecoder;
import jj2000.j2k.codestream.reader.BitstreamReaderAgent;
import jj2000.j2k.fileformat.reader.FileFormatReader;

/**
 * This class represents a Jpeg2000 file.  It uses JJ2000, an open
 * source JPEG2000 implementation.
 */

public class Jpeg2000
{
    private File file;
    private String accessFlag;
    private int transformLevels = -1;
    private int rows = -1;
    private int columns = -1;
    
    public Jpeg2000(File file)
        throws IOException
    {
        this(file, null);
    }

    public Jpeg2000(File file, String accessFlag)
        throws IOException
    {
        this.file = file;
        this.accessFlag = accessFlag;
        getFileInfo();
    }

    public static String key(String id) { return("lidsId-" + id); }

    /* Note that transform levels are not exactly resolution levels that
     * an application would use.  If an image has 6 transform levels,
     * it actually has 7 resolution levels.
     */

    public int getTransformLevels() {
        return transformLevels;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public File getFile() {
        return file;
    }

    public String getAccessFlag() {
        return accessFlag;
    }

    public ArrayList findResolutionDimensions()
    {
        ArrayList<Dimensions> list = new ArrayList<Dimensions>();
        for (int i=1;i<=(getTransformLevels() + 1);i++) {
            Dimensions dimensions = findResolutionDimensions(i);
            list.add(dimensions);
        }

        return (list);
    }

    public Dimensions findResolutionDimensions(int level)
    {
        if (level < 1) { return null; }

        int currentLevel = 1;
        int totalLevels = getTransformLevels() + 1;
        int currentWidth = getColumns();
        int currentHeight = getRows();
        
        while (currentLevel <= totalLevels) {

            if (currentLevel == level) {
                break;
            }

            currentLevel++;
            
            currentWidth = (int)Math.ceil((double)currentWidth / (double)2);
            currentHeight = (int)Math.ceil((double)currentHeight / (double)2);
        }
        
        Dimensions d = new Dimensions();
        d.setHeight(currentHeight);
        d.setWidth(currentWidth);

        return (currentLevel <= totalLevels ? d : null);
    }

    // finds the smallest resolution level that contains the request
    // width and height

    public int findResolutionLevelCanContain(int requestWidth,
                                             int requestHeight)
    {
        int currentLevel = 1;
        Dimensions dimensions = findResolutionDimensions(currentLevel);

        while (dimensions != null) {
           
            if (dimensions.getWidth() <= requestWidth ||
                dimensions.getHeight() <= requestHeight) {
                break;
            }

            currentLevel++;
            dimensions = findResolutionDimensions(currentLevel);
        }

        return (dimensions != null ? currentLevel : -1);
    }

    // finds the largest resolution level that is contained within
    // the request width and height

    public int findResolutionLevelContainedBy(int requestWidth,
                                              int requestHeight)
    {
        int currentLevel = 1;
        Dimensions dimensions = findResolutionDimensions(currentLevel);

        while (dimensions != null) {
           
            if (dimensions.getWidth() <= requestWidth &&
                dimensions.getHeight() <= requestHeight) {
                break;
            }

            currentLevel++;
            dimensions = findResolutionDimensions(currentLevel);
        }

        return (dimensions != null ? currentLevel : -1);
    }

    public static String estimateJpegSize(int width, int height, int magnitude)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        
        // 24 bit (RGB) jpeg - estimate 10:1 compression
        int estimate = (((height * width * 24) / 8) / 10) / magnitude;

        int factor;
        for (factor = 1; estimate > factor; factor *= 10) { }

        if (factor > 10) {
            factor = factor / 10;
            estimate = ((int)Math.floor(estimate / factor)) * factor;
        }

        return nf.format(estimate);
    }

    private void getFileInfo()
        throws IOException
    {

        // Much of this code for initializing the JJ2000 code was taken
        // from the OpenImage.java file in the GUI src that you can
        // download.  This was changed from the Aware API, which seemed
        // to have a horrible memory leak.

        try {

            String param[][] = Decoder.getAllParameters();
            ParameterList defpl = new ParameterList();
            
            for (int i=param.length-1;i>=0;i--) {
                if (param[i][3] != null) {
                    defpl.put(param[i][0],param[i][3]);
                }
            }
            
            ParameterList pl = new ParameterList(defpl);
            RandomAccessIO in = new BEBufferedRandomAccessFile(file, "r");
            
            FileFormatReader ff = new FileFormatReader(in);
            ff.readFileFormat();
            if (ff.JP2FFUsed) {
                in.seek(ff.getFirstCodeStreamPos());
            }
            
            HeaderInfo hi = new HeaderInfo();
            HeaderDecoder hd = new HeaderDecoder(in,pl,hi);
            
            DecoderSpecs decSpec = hd.getDecoderSpecs();
            BitstreamReaderAgent breader = BitstreamReaderAgent.
                createInstance(in,hd,pl,decSpec,
                               false,
                               hi);

            transformLevels = breader.getImgRes();
            rows = hd.getImgHeight();
            columns = hd.getImgWidth();

            in.close();
        }
        catch (Exception e) {
            throw new IOException("Error retrieving JPEG2000 info: " +
                                  getStackTrace(e));
                                  
        }
    }

   
    private String getStackTrace(Exception e)
    {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	e.printStackTrace(pw);
	pw.flush();
	String ret = sw.toString();
	pw.close();
	try {
	    sw.close();
	}
	catch (Exception ex) { }
	return ret;
    }


}
