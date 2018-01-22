package org.ensg.projet;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.ensg.projet.Points;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * This class tests the several functions of the Points class
 * @author evach
 *
 */

public class PointsTest extends Points {

	@Test	
	public void testAddCoordinate() {
		List<Coordinate> coordinate1 = new ArrayList();
		coordinate1.add(new Coordinate(0,0));
		coordinate1.add(new Coordinate(0,1));
		
		Coordinate coordinate = new Coordinate(1,2);
		
		Points pts = new Points(coordinate1);
		pts.addCoordinate(coordinate);
		assertEquals(pts.getCoordinate().size(),3);
		assertEquals(pts.getCoordinate().get(2), new Coordinate(1,2));

	}
	
	@Test
	public void testGetEnveloppe() {
		List<Coordinate> coordinate1 = new ArrayList();
		coordinate1.add(new Coordinate(0,0));
		coordinate1.add(new Coordinate(2,5));
		coordinate1.add(new Coordinate(4,3));
		coordinate1.add(new Coordinate(4,0));
		
		Points pts = new Points(coordinate1);

		List<Point> env = pts.getEnvelope(coordinate1);
		List<Point> result = new ArrayList();
		result.add(new Point(new Coordinate(0,0), null, 0));
		result.add(new Point(new Coordinate(0,5), null, 0));
		result.add(new Point(new Coordinate(4,0), null, 0));
		result.add(new Point(new Coordinate(4,5), null, 0));

		assertEquals(env, result);
		
	}
	
	@Test 
	public void testRegularGridAgregate() {
		
		List<Coordinate> coordList = new ArrayList();
		coordList.add(new Coordinate(0,0));
		coordList.add(new Coordinate(0,4));
		coordList.add(new Coordinate(4,4));
		coordList.add(new Coordinate(4,0));
		
		Points point = new Points(coordList);

		List<Point> env = point.getEnvelope(coordList);
		
		List<Polygon> regularGrid = regularGrid(coordToPoint(point.getCoordinate()));

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		
		// !!!!!!! FIRST !!!!!!
		Coordinate point11 = new Coordinate(0.0, 4.0);
		Coordinate point12 = new Coordinate(2.0, 4.0);
		Coordinate point13 = new Coordinate(2.0, 2.0);
		Coordinate point14 = new Coordinate(0.0, 2.0);
		
		// To close the polygon we need to specify the first point twice 
		Coordinate[] newPoly1 = new Coordinate[] {point11, point12, point13, point14, point11};
		
		
		LinearRing ring1 = geometryFactory.createLinearRing( newPoly1 );
		LinearRing holes1[] = null; // use LinearRing[] to represent holes
		Polygon polygon1 = geometryFactory.createPolygon(ring1, holes1 );
				
		//System.out.println(regularGrid);
		assertEquals(regularGrid.get(0), polygon1);
		
		/*
		// !!!!!!!! SECOND !!!!!!!!
		Coordinate point21 = new Coordinate(0.0, 2.0);
		Coordinate point22 = new Coordinate(0.0, 4.0);
		Coordinate point23 = new Coordinate(2.0, 4.0);
		Coordinate point24 = new Coordinate(2.0, 2.0);
		
		// To close the polygon we need to specify the first point twice 
		Coordinate[] newPoly2 = new Coordinate[] {point21, point22, point23, point24, point21};
		
		
		LinearRing ring2 = geometryFactory.createLinearRing( newPoly2 );
		LinearRing holes2[] = null; // use LinearRing[] to represent holes
		Polygon polygon2 = geometryFactory.createPolygon(ring1, holes2 );
				
		assertEquals(regularGrid.get(1), polygon2);*/

		// Test of the agregatePoints method
		List<Coordinate> coord = new ArrayList();
		coord.add(new Coordinate(1,3));
		coord.add(new Coordinate(1.5,3));
		coord.add(new Coordinate(3,1));

		Points pt = new Points(coord);
		Points agregated = pt.agregatePoints(regularGrid, coord);
		
		List<Coordinate> coord2 = new ArrayList();
		coord2.add(new Coordinate(1.25,3));
		coord2.add(new Coordinate(3,1));
		Points pt2 = new Points(coord2);

		assertEquals(agregated.getCoordinate(), pt2.getCoordinate());
		
		
	}


}
