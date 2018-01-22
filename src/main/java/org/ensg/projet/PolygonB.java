package org.ensg.projet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This class allows to handle and simplfy multipolygons geometries
 * @author evach
 *
 */

public class PolygonB {

	private List<Point> nodes = new ArrayList();
	private double surface;
	private boolean isBigEnough;
	public static List<MultiPolygon> simplified = new ArrayList();
	
	/**
	 * Constructor of an empty PolygonB
	 */
	public PolygonB() {

		this.nodes = new ArrayList();
	}
	
	
	/**
	 * PolygonB constructor given its nodes
	 * @param nodes The nodes of the polygon
	 */
	public PolygonB(List<Point> nodes) {

		this.nodes = nodes;
	}
	

	/**
	 * This function allows to access the polygon surface
	 * @return the polygon surface
	 */
	public double getSurface() {
		return surface;
	}


	/**
	 * This function allows to set a surface to the current PolygonB
	 * @param surface
	 */
	public void setSurface(double surface) {
		this.surface = surface;
	}


	/**
	 * This function allows to know the value of the attribute isBigEnough
	 * @return boolean isBigEnough
	 */
	public boolean isBigEnough() {
		return isBigEnough;
	}


	/**
	 * This function allows to set isBigEnough
	 * @param boolean 
	 */
	public void setBigEnough(boolean isBigEnough) {
		this.isBigEnough = isBigEnough;
	}


	/**
	 * This function allows to set the nodes of the current PolygonB
	 * @param list of nodes
	 */
	public void setNodes(List<Point> nodes) {
		this.nodes = nodes;
	}



	/**
	 * This function gives the nodes of a polygon
	 */
	public List<Point> getNodes() {
		return this.nodes;
	}
	
	/**
	 * This function gives the list of the simplified polygons
	 */
	public static List<MultiPolygon> getSimplified() {
		return simplified;
	}

	/**
	 * This function allows to set the simplified polygons of the current PolygonB
	 * @param list of nodes
	 */
	public static void setSimplified(List<MultiPolygon> simplified) {
		PolygonB.simplified = simplified;
	}
	
	/**
	 * This function set isBigEnough to true if the polygon surface is below a given value
	 * @param threshold is the limit surface value for a polygon to be deleted
	 */
	public void isBigEnough(double threshold) {
		// If the polygon surface is over a given value, isLongEnough is true
		if (this.surface > threshold) {
			this.isBigEnough = true;
		}
		else {
			this.isBigEnough = false;
		}
	}
	
	
	/**
	 * This function transforms a geotools WKT Polygon into a PolygonB and simplifies it
	 * @param coord The polygon nodes coordinates
	 * @return 
	 * @return PolygonB 
	 * @throws ParseException
	 */
	public static List<MultiPolygon> convertPolygon(String coord, double threshold) throws ParseException {
		
    	GeometryFactory geomFactory = JTSFactoryFinder.getGeometryFactory();

    	WKTReader reader = new WKTReader(geomFactory);

    	// We read the feature geometry and turn it into a PolygonB
    	MultiPolygon polygon = (MultiPolygon) reader.read(coord);
    	double area = polygon.getArea();
    	
    	Coordinate[] seq = polygon.getCoordinates();
    	
    	List<Point> pts = new ArrayList();
    	for (Coordinate c: seq) {
    		Point pt = new Point(c, null, 0);
    		pts.add(pt);
    	}
   	
    	// We create a Polygons from the Polygon 
    	PolygonB poly = new PolygonB(pts);
    	poly.setSurface(area);

    	poly.isBigEnough(threshold);
    	if (poly.isBigEnough) {
    		poly.simplified.add(polygon);
    	}
    	List<MultiPolygon> simplified = poly.getSimplified();
    	return simplified;
    	
	}



	

}