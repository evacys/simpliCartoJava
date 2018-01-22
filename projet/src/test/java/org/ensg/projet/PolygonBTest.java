package org.ensg.projet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This class tests the several functions of the PolygonB class
 * @author evach
 *
 */

public class PolygonBTest extends PolygonB {

	private PolygonB polygon;
	
	@Before
	public void initialize() {
		
		Point pt1 = new Point(new Coordinate(20.0,10.0), null, 0);
		Point pt2 = new Point(new Coordinate(30.0,0.0), null, 0);
		Point pt3 = new Point(new Coordinate(40.0,10.0), null, 0);
		Point pt4 = new Point(new Coordinate(30.0,20.0), null, 0);
		Point pt5 = new Point(new Coordinate(20.0,10.0), null, 0);


		List<Point> pts = new ArrayList();
		pts.add(pt1);
		pts.add(pt2);
		pts.add(pt3);
		pts.add(pt4);
		pts.add(pt5);
		
		polygon = new PolygonB(pts);
		
	}
	
	@Test
	public void testIsBigEnough() {

		polygon.setSurface(30);
		// We set the attribute isBigEnough 
		polygon.isBigEnough(10);
		// We get this attribute and check its value
		assertEquals(polygon.isBigEnough(), true);
	}
	
	@Test
	public void testConvertPolygon() throws ParseException {
		// We create a second polygon 
		// Surface polygon2 = 	0.03
		String polygon2 = "MULTIPOLYGON (((-1.2792140408396118 37.289466322391725, -1.3151040109671077 37.669301839574395, -1.1117275135779634 37.627430207758984, -1.2792140408396118 37.289466322391725)))";
		
		// Surface polygon3 = 0.9
		String polygon3 = "MULTIPOLYGON (((-0.7857269515865408 36.60157522828138, -0.887415200281113 37.48686115809296, -0.3939281110280421 37.02627320812342, 0.4135962168406191 36.54175861140223, 0.0128248837502465 35.82395920885231, -0.7857269515865408 36.60157522828138)))";
		
		// If the limit surface is 0.04, only polygon3 shoud be in the result kept
		List<MultiPolygon> result2 = convertPolygon(polygon2, 0.04);
		assertEquals(result2, new ArrayList());
		
		List<MultiPolygon> result3 = convertPolygon(polygon3, 0.04);
		
		
		GeometryFactory geomFactory = JTSFactoryFinder.getGeometryFactory();

    	WKTReader reader = new WKTReader(geomFactory);

    	List<MultiPolygon> list = new ArrayList();
    	MultiPolygon polygon = (MultiPolygon) reader.read(polygon3);
    	list.add(polygon);
    	
		assertEquals(result3, list);

	}

}
