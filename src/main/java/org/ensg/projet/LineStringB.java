package org.ensg.projet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

//import org.locationtech.jts.geom.PrecisionModel;

/**
 * This class allows to handle and simplfy multilinestring geometries
 * @author evach
 *
 */

public class LineStringB {

	private List<Point> nodes = new ArrayList();
	private int length;
	public List<LineStringB> simplified = new ArrayList();
	
	/**
	 * LineStringB constructor
	 */
	public LineStringB() {
		this.nodes = new ArrayList();
	}
	
	/**
	 * LineStringB constructor based on a list of point
	 * @param nodes The LineString nodes
	 */
	public LineStringB(List<Point> nodes) {
		this.nodes = nodes;
		this.length = nodes.size();
	}
	
	/**
	 * This method allows to access the LineString Nodes
	 * @return The list of nodes
	 */
	public List<Point> getNodes() {
		return this.nodes;
	}

	/**
	 * This function allows to set the LineString nodes
	 * @param nodes The nodes to assign to the LineString
	 */
	public void setNodes(List<Point> nodes) {
		this.nodes = nodes;
	}

	
	/**
	 * This method allows to access the LineString number of nodes
	 * @return The LineString length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * This method allows to set the LineString number of nodes
	 * @param length The length to give to the LineString
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/** This function allows to access the simplified linestring
	 * 
	 * @return Simplified list of LineStringB
	 */
	public List<LineStringB> getSimplified() {
			return simplified;
	}

	/**
	 * This function allows to set a simplified geometry to the current linestring
	 * @param simplified The simplify geometry to associate
	 */
	public void setSimplified(List<LineStringB> simplified) {
		this.simplified = simplified;
	}
	
	

	
	/**
	 * This function adds a coordinate into the existing list of points
	 * @param coordinate The coordinate to add to the list
	 */
	public void addNode(Point point) {
		this.nodes.add(point);
		this.length += 1;
	}
	
	
	/**
	 * This function gives the nodes of a polyline and add them to the current object attributes
	 * @param polyline
	 */
	public Point getNode(int i) {
		return this.nodes.get(i);
	}
	
	
	/**
	 * This function calculate the distance between a point and a linestring
	 * @param point
	 * @param segment
	 * @return the distance between the 2 geometries
	 */
	public double distancePointSegment(Point point, LineString segment) {
		// We calculate the line equation parameters
		double a = (segment.getEndPoint().getY() - segment.getStartPoint().getY())/(segment.getEndPoint().getX()-segment.getStartPoint().getX());
		double b = segment.getEndPoint().getY() - a* segment.getEndPoint().getX();
		
		// We calculate the distance between point and segment
		double distancePointSegment = (a * point.getX() + b * point.getY())/ Math.pow((Math.pow(a, 2) + Math.pow(b, 2)), 1/2);
	
		return distancePointSegment;
	}
	
	
	
	/**
	 * This function applies the Douglas-Peucker algorithm to simplify a line curve
	 * It deletes some nodes to smoothen a curve
	 * @return It returns a list of new nodes 
	 */
	public List<LineStringB> simplifyCurves(List<Point> points, double epsilon) {
		
		double   dmax = 0;
		int   index = 0;
		int end = points.size()-1;

		 Coordinate[] coord = new Coordinate[] {points.get(0).getCoordinate(), points.get(end).getCoordinate()};
		 LineString line = new LineString(coord, null, 0) ;
		 List<Point> pt = new ArrayList();
		 pt.add(points.get(0));
		 pt.add(points.get(end));

		 LineStringB lineB = new LineStringB(pt);
		 
		 // Find the further point from the segment
		 for (int i = 1; i< end; i++) {

			 double d = Math.abs(distancePointSegment(points.get(i), line)) ;

			 if (d > dmax) {
			      index = i;
			      dmax = d;
			 }
		 }

		// If dmax is over a given value we simplify
		if (dmax > epsilon) {

			List<Point> pts1 = new ArrayList();
			List<Point> pts2 = new ArrayList();

			for (int j= 0; j < index+1; j++) {
				pts1.add(points.get(j));
			}
			
			for (int j= index; j < end +1; j++) {
				pts2.add(points.get(j));
			}
			
			List<LineStringB> result1 = simplifyCurves(pts1, epsilon);
			List<LineStringB> result2 = simplifyCurves(pts2, epsilon);

			List<LineStringB> result = result1;
			result.addAll(result2.subList(1, result2.size()));
			return result;
		}
		
		// Every points are close, returns a segment with extreme points
		else {
			this.simplified.add(lineB);

			return Arrays.asList(lineB);
		}
		}
		
	
	/**
	 * This function convert a geotools WKT MultiLineString into a LineStringB and simplifies it
	 * @param coord The line coordinates
	 * @return A list of simplified LineStringB
	 * @throws ParseException
	 */
	
	public static List<LineStringB> convertMultiLineString(String coord) throws ParseException {
    	GeometryFactory geomFactory = JTSFactoryFinder.getGeometryFactory();

    	WKTReader reader = new WKTReader(geomFactory);
    	// We read the feature geometry and add each coordinate into a list of points
    	MultiLineString line = (MultiLineString) reader.read(coord);

    	Coordinate[] seq = line.getCoordinates();
    	List<Point> pts = new ArrayList();
    	for (Coordinate c: seq) {
    		Point pt = new Point(c, null, 0);
    		pts.add(pt);
    	}
    	
    	// We create a LineStringB from the LineString 
    	LineStringB poly = new LineStringB(pts);
    	Point p = pts.get((int) Math.ceil(pts.size()/2));
    	LineString l = new LineString(seq, null, 2);
    	
    	// We simplify it
     	double epsilon = poly.distancePointSegment(p,l);
    	List<LineStringB> newLine = poly.simplifyCurves(poly.getNodes(), epsilon);
    	List<LineStringB> simplified = poly.getSimplified();
    	
    	return simplified;
	}
	
	
}
