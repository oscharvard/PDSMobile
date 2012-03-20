/**********************************************************************
 * Copyright 2006 by the President and Fellows of Harvard College
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

public class UIUtil
{
    private static final double THUMB_LONG_EDGE = 130;
    
	/***********************************************************************
	 * Calculates the new X and Y coordinates when a jp2 thumbnail is
	 * clicked.  The coordinates returned are adjusted so that the click
	 * point becomes the center of the new bounding box.
	 **********************************************************************/
	public static Coordinate panThumbnail(int vHeight, int vWidth, int clickX, int clickY,
							double res, Jpeg2000 jp2,
						  	String rotation) {
		Dimensions d = getThumbnailSize(jp2, rotation);
		Double tHeight = (double)d.getHeight();
		Double tWidth = (double)d.getWidth();
		
		int fullWidth = jp2.getColumns();
		int fullHeight = jp2.getRows();
		
		double resWidth = fullWidth * res;
		double resHeight = fullHeight * res;
		
		if(rotation.equals("90") || rotation.equals("270")) {
			double temp = fullHeight;
			fullHeight = fullWidth;
			fullWidth = (int)temp;
			temp = resHeight;
			resHeight = resWidth;
			resWidth = temp;
		}
		
		double boxWidth = getBoxDimension(tWidth,resWidth,vWidth);
		double boxHeight = getBoxDimension(tHeight,resHeight,vHeight);
		
		//convert clicked center point to the upper left coordinates
		boxWidth = Math.rint(boxWidth);
		boxHeight = Math.rint(boxHeight);
		clickX = clickX - (int)boxWidth/2;
		clickY = clickY - (int)boxHeight/2;
		
		Coordinate newPoint = fixBoxPosition(boxWidth,boxHeight,tWidth,tHeight,clickX,clickY);

		return convertThumbCoordinate(resWidth,resHeight,
									  tWidth,tHeight,
				  					  newPoint.getX(),newPoint.getY());
	}
	/*************************************************************************
	 * Calculates the new X and Y coordinates when the jp2 is zoomed.
	 * The coordinates returned are adjusted so that the center of the
	 * pre-zoomed images remains the center of the new bounding box.
	 ************************************************************************/
	public static Coordinate zoom(int vHeight, int vWidth, int jp2x, int jp2y,
							  double res, Jpeg2000 jp2,
							  boolean in, String rotation) {
		
		Dimensions d = getThumbnailSize(jp2, rotation);
		Double tHeight = (double)d.getHeight();
		Double tWidth = (double)d.getWidth();
		
		//Full sized image
		int fullWidth = jp2.getColumns();
		int fullHeight = jp2.getRows();
		
		//original (before zoom) box height
		double resWidth = fullWidth * (in?res/2:res*2);
		double resHeight = fullHeight * (in?res/2:res*2);
		
		//If the image is rotated, swap height and widths
		if(rotation.equals("90") || rotation.equals("270")) {
			double temp = fullHeight;
			fullHeight = fullWidth;
			fullWidth = (int)temp;
			temp = resHeight;
			resHeight = resWidth;
			resWidth = temp;
		}
		double boxWidth  = getBoxDimension(tWidth,resWidth,vWidth);
		double boxHeight = getBoxDimension(tHeight,resHeight,vHeight);
		
		//convert x and y to thumbnail scale coordinates
		Coordinate thumbDimension = convertFullSizeCoordate(resWidth,resHeight,
												  tWidth,tHeight,jp2x,jp2y);
		jp2x = thumbDimension.getX();
		jp2y = thumbDimension.getY();
		
		//Calculate center point of original blue box
		int centerX=0;
		int centerY=0;
		if(boxWidth<tWidth) {
			centerX = jp2x + (int)boxWidth/2;
		}
		else {
			//use thumbnail center
			centerX = (int)(tWidth/2);
		}
		if(boxHeight<tHeight) {
			centerY = jp2y + (int)boxHeight/2;
		}
		else {
			//use thumbnail center
			centerY = (int)(tHeight/2);
		}

		//new blue box dimensions
		resWidth = fullWidth * res;
		resHeight = fullHeight * res;

		boxWidth  = getBoxDimension(tWidth,resWidth,vWidth);
		boxHeight = getBoxDimension(tHeight,resHeight,vHeight);
		if(boxHeight>tHeight) {
			boxHeight=tHeight;
		}
		if(boxWidth>tWidth) {
			boxWidth=tWidth;
		}
		
		jp2x = centerX - (int)boxWidth/2;
		jp2y = centerY - (int)boxHeight/2;
		
		Coordinate newPoint = fixBoxPosition(boxWidth,boxHeight,tWidth,tHeight,jp2x,jp2y);
		
		return convertThumbCoordinate(resWidth,resHeight,
									  tWidth,tHeight,
				  					  newPoint.getX(),newPoint.getY());
	}
    /*************************************************************************
     * Keeps the display centered around the center of the previous X,Y
     * coordinate when the image size (viewheight,vieweidth) is changed
     ************************************************************************/
    public static Coordinate centerOnResize(int vHeight, int vWidth, int pvHeight, 
					    int pvWidth,double pvRes, int jp2x, int jp2y,
					    double res,Jpeg2000 jp2, String rotation) {
	Dimensions d = getThumbnailSize(jp2, rotation);
	Double tHeight = (double)d.getHeight();
	Double tWidth = (double)d.getWidth();
		
	int fullWidth = jp2.getColumns();
	int fullHeight = jp2.getRows();

	double resWidth = fullWidth * pvRes;
	double resHeight = fullHeight * pvRes;
	
	if(rotation.equals("90") || rotation.equals("270")) {
	    double temp = fullHeight;
	    fullHeight = fullWidth;
	    fullWidth = (int)temp;
	    temp = resHeight;
	    resHeight = resWidth;
	    resWidth = temp;
	}
	
	double boxWidth = getBoxDimension(tWidth,resWidth,pvWidth);
	double boxHeight = getBoxDimension(tHeight,resHeight,pvHeight);
	boxWidth = Math.rint(boxWidth);
	boxHeight = Math.rint(boxHeight);
	Coordinate c = convertFullSizeCoordate(resWidth,resHeight,tWidth,
					       tHeight, jp2x,jp2y);
	
	//Calculate center point of original blue box
	int centerX=0;
	int centerY=0;
	if(boxWidth<tWidth) {
	    centerX = c.getX() + (int)boxWidth/2;
	}
	else {
	    //use thumbnail center
	    centerX = (int)(tWidth/2);
	}
	if(boxHeight<tHeight) {
	    centerY = c.getY() + (int)boxHeight/2;
	}
	else {
	    //use thumbnail center
	    centerY = (int)(tHeight/2);
	}
	
	//pan to the previous center point with the new vHeight and vWidth
	return panThumbnail(vHeight, vWidth, centerX, centerY,
			    res, jp2,
			    rotation); 
    }

	/********************************************************************
	 * Converts a thumbnail coordinate to the equivalent full size image
	 * coordinate
	 *******************************************************************/
	private static Coordinate convertThumbCoordinate(double resWidth,
											 double resHeight,double tWidth,
											 double tHeight,double thumbX,
											 double thumbY) {
		double newX = (resWidth/tWidth)*thumbX;
		double newY = (resHeight/tHeight)*thumbY;
		newX = Math.rint(newX);
		newY = Math.rint(newY);
		return new Coordinate((int)newX,(int)newY);
	}
	/********************************************************************
	 * Converts a full sized image coordinate to the equivalent thumbnail
	 * image size coordinate
	 *******************************************************************/
	public static Coordinate convertFullSizeCoordate(double resWidth,
										double resHeight,double tWidth,
										double tHeight,double fullSizeX,
										double fullSizeY) {
		double newX = (tWidth/resWidth)*fullSizeX;
		double newY = (tHeight/resHeight)*fullSizeY;
		newX = Math.rint(newX);
		newY = Math.rint(newY);
		return new Coordinate((int)newX,(int)newY);
	}
	/********************************************************************
	 * Converts a full sized image coordinate to the equivalent thumbnail
	 * image size coordinate
	 *******************************************************************/
	public static Coordinate convertFullSizeCoordate(Jpeg2000 jp2,double fullSizeX,double fullSizeY,
										double vWidth, double vHeight,double res,
										String rotation) {
		Dimensions d = getThumbnailSize(jp2, rotation);
		Double tHeight = (double)d.getHeight();
		Double tWidth = (double)d.getWidth();
		
		int fullWidth = jp2.getColumns();
		int fullHeight = jp2.getRows();
		
		double resWidth = fullWidth * res;
		double resHeight = fullHeight * res;
		
		if(rotation.equals("90") || rotation.equals("270")) {
			double temp = resHeight;
			resHeight = resWidth;
			resWidth = temp;
		}
				
		Coordinate c = convertFullSizeCoordate(resWidth, resHeight, tWidth, tHeight, fullSizeX,fullSizeY);
		double newX = Math.rint(c.getX());
		double newY = Math.rint(c.getY());
				
		double boxWidth  = getBoxDimension(tWidth,resWidth,vWidth);
		double boxHeight = getBoxDimension(tHeight,resHeight,vHeight);
		
		return new Coordinate((int)(newX+boxWidth/2),(int)(newY+boxHeight/2));
	}
	
	/********************************************************************
	 * Corrects (if needed) the positioning of the blue bounding box
	 * if it extends beyond the edge of the thumbnail.
	 *******************************************************************/
	public static Coordinate fixBoxPosition(double vHeight,double vWidth,double x,
								double y,double res,double tWidth,
								double tHeight,Jpeg2000 jp2, String rotation) {
		int fullWidth = jp2.getColumns();
		int fullHeight = jp2.getRows();
		
		double resWidth = fullWidth * res;
		double resHeight = fullHeight * res;
		
		//If the image is rotated, swap height and widths
		if(rotation.equals("90") || rotation.equals("270")) {
			double temp = resHeight;
			resHeight = resWidth;
			resWidth = temp;
			
			double temp2 = tWidth;
			tWidth = tHeight;
			tHeight= temp2;
		}
		
		double boxWidth  = getBoxDimension(tWidth,resWidth,vWidth);
		double boxHeight = getBoxDimension(tHeight,resHeight,vHeight);

		Coordinate newPoint = convertFullSizeCoordate(resWidth, resHeight,
													tWidth, tHeight,x,y);
		
		newPoint = fixBoxPosition(boxWidth,boxHeight,tWidth,tHeight,
								newPoint.getX(),newPoint.getY());
		return convertThumbCoordinate(resWidth,resHeight,tWidth,tHeight,
									  newPoint.getX(),newPoint.getY());
	}
	/********************************************************************
	 * Corrects (if needed) the positioning of the blue bounding box
	 * if it extends beyond the edge of the thumbnail.
	 *******************************************************************/
	static private Coordinate fixBoxPosition(double boxWidth, double boxHeight,
										double tWidth, double tHeight,
										double x, double y) {
		//adjust X direction
		if(x > tWidth-boxWidth) {
			boxWidth = Math.rint(boxWidth);
			x = tWidth-(int)boxWidth;
		}
		if(x < 0) {
			x = 0;
		}

		//adjust Y direction
		if(y > tHeight-boxHeight) {
			boxHeight = Math.rint(boxHeight);
			y = tHeight-(int)boxHeight;
		}
		if(y < 0) {
			y = 0;
		}
		return new Coordinate((int)x,(int)y);
	}
	/*************************************************************************
	 * Calculates the blue bounding box dimension (x or y) based on the ratio
	 * of the parameters passed in
	 ************************************************************************/
	private static double getBoxDimension(double thumbdim, double resdim,
										  double viewdim) {
		return (thumbdim/resdim)*viewdim;
	}
	
	public static CoordinatePair getBoundingBoxDimensions(Jpeg2000 jp2,double res,
												double vWidth, double vHeight, 
												int clickX, int clickY,String rotation) {	
		Dimensions d = getThumbnailSize(jp2, rotation);
		Double tHeight = (double)d.getHeight();
		Double tWidth = (double)d.getWidth();
		
		if(clickX == -1) {
			clickX = (int)(tWidth/2);
		}
		if(clickY == -1) {
			clickY = (int)(tHeight/2);
		}
	
		int fullWidth = jp2.getColumns();
		int fullHeight = jp2.getRows();
				
		double resWidth = fullWidth * res;
		double resHeight = fullHeight * res;
		
		//If the image is rotated, swap height and widths
		if(rotation.equals("90") || rotation.equals("270")) {
			double temp = resHeight;
			resHeight = resWidth;
			resWidth = temp;
		}
		
		double boxW = getBoxDimension(tWidth,resWidth,vWidth);
		double boxH = getBoxDimension(tHeight,resHeight,vHeight);
		int halfBoxW = (int)boxW/2;
		int halfBoxH = (int)boxH/2;
		Coordinate xy1 = fixBoxPosition(boxW, boxH, tWidth, tHeight,clickX-halfBoxW,clickY-halfBoxH);
		int xdiff = clickX-halfBoxW-xy1.getX();
		int ydiff = clickY-halfBoxH-xy1.getY();
		double x2 = clickX+halfBoxW-xdiff;
		double y2 = clickY+halfBoxH-ydiff;
		if(x2 > tWidth) {
			x2 = tWidth;
		}
		if(y2 > tHeight) {
			y2 = tHeight;
		}
		Coordinate xy2 = new Coordinate((int)x2,(int)y2);
		return new CoordinatePair(xy1,xy2);
	}
	
	public static Coordinate rotatePoint(Jpeg2000 jp2, double res,
										double tWidth,double tHeight, 
										double angle, double x, double y) {
		double x0 = tWidth/2;
		double y0 = tHeight/2;
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		double newX = x0+c*(x-x0)-s*(y-y0);
		double newY = y0+s*(x-x0)+c*(y-y0);
		
		//Full sized image
		int fullWidth = jp2.getColumns();
		int fullHeight = jp2.getRows();
		
		//width and height at current resolution
		double resWidth = fullWidth * res;
		double resHeight = fullHeight * res;
		
		return convertThumbCoordinate(resWidth,resHeight,tWidth,tHeight,
				  newX,newY);
		
	}
	
	public static Dimensions getThumbnailSize(Jpeg2000 jp2, String rotation) {
		double fullheight = jp2.getRows();
		double fullwidth = jp2.getColumns();
		if(rotation.equals("90") || rotation.equals("270")) {
			double temp = fullheight;
			fullheight = fullwidth;
			fullwidth = temp;
		}
		int tHeight = 0;
		int tWidth = 0;			
		if(fullheight > fullwidth) {
			tHeight = (int)THUMB_LONG_EDGE;
			tWidth = (int)((fullwidth/fullheight) * THUMB_LONG_EDGE);
		}
		else {
			tWidth = (int)THUMB_LONG_EDGE;
			tHeight = (int)((fullheight/fullwidth) * THUMB_LONG_EDGE);
		}
		Dimensions d = new Dimensions();
		d.setHeight(tHeight);
		d.setWidth(tWidth);
		return d;
	}
}

