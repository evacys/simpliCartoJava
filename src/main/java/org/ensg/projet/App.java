package org.ensg.projet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SchemaException, IOException, ParseException
    {	
    	// Initialization
    	Shapefile shp = new Shapefile();
    	System.out.println( "Hello World!" );
    	
    	/* To do the test for a point geometry uncomment the following lines
    	 * replacing the parameter by the correct input layer path */
    	
//    	test.readShp("input/refuges_4.shp");
//    	test.createFeaturePoint("input/refuges_4.shp");
    	
    	/* To do the test for a multilinestring geometry uncomment the following lines 
    	 * replacing the parameter by the correct input layer path */

//    	test.readShp("input/testLine4.shp");
//    	test.createFeatureLine("input/testLine4.shp");
    	
    	
    	/* To do the test for a multipolygon geometry uncomment the following lines 
    	 * replacing the parameter by the correct input layer path */

    	shp.readShp("input/testPolygon2.shp");
    	shp.createFeaturePolygon("input/testPolygon2.shp");
    	
    	System.out.println( "Done!" );
    }
}
