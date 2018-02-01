package org.ensg.projet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

/**
 * This class tests the several functions of the LineStringB class
 * @author evach
 *
 */

public class LineStringBTest extends LineStringB {

	
	private LineStringB linestring;
	
	@Before
	public void initialize() {
		
		//Point pt1 = new Point(new Coordinate(2.0,2.0), null, 0);
		Point pt2 = new Point(new Coordinate(3.0,3.0), null, 0);
		Point pt3 = new Point(new Coordinate(5.0,1.0), null, 0);
		Point pt4 = new Point(new Coordinate(7.0,4.0), null, 0);


		List<Point> pts = new ArrayList();
		pts.add(pt1);
		pts.add(pt2);
		pts.add(pt3);
		pts.add(pt4);

		
		linestring = new LineStringB(pts);
		
	}
	
	@Test
	public void testAddNode() {		
				
		linestring.addNode(new Point(new Coordinate(5.0,4.0), null, 0));
		assertEquals(linestring.getLength(),5);	

		assertEquals(linestring.getNode(linestring.getLength()-1).getCoordinate().x, 5.0, 0.01);

		assertEquals(linestring.getNode(linestring.getLength()-1).getCoordinate().y, 4.0, 0.01);	
	}
	
	
	@Test
	public void testGetNode() {
		
		assertEquals(linestring.getNode(0), new Point(new Coordinate(2.0,2.0), null, 0));	
	
	}
	
	@Test
	public void testDistancePointSegment() {
		Point pt = new Point(new Coordinate(0.0,7.0), null, 0);
		Coordinate[] coord = new Coordinate[] {new Coordinate(3.0,1.0), new Coordinate(1.0, 1.0)};
		LineString line = new LineString(coord, null, 0);
		assertEquals(distancePointSegment(pt, line), 7.0, 0.1);
	}
	
	
	@Test
	public void testConvertMultiLineString() throws ParseException {
		// This function allows to test the simplifyCurves function
		
		String line = "MULTILINESTRING((0 0, 1 5, 3 2))";
		
		
		List<LineStringB> result = convertMultiLineString(line);
		// The result line is composed of the two extreme points
		assertEquals(result.get(0).getNode(0).getX(),0.0, 0.01);
		assertEquals(result.get(0).getNode(0).getY(),0.0, 0.01);
		assertEquals(result.get(0).getNode(1).getX(),3.0, 0.01);
		assertEquals(result.get(0).getNode(1).getY(),2.0, 0.01);

		}
	
}
