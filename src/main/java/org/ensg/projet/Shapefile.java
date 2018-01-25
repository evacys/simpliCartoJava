package org.ensg.projet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.CurveSegment;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geomgraph.Position;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.ensg.projet.Points;

/**
 * This class allows to handle and simplfy input geometries and write them into a new layer
 * @author evach
 *
 */

public class Shapefile {
	
    private static List<Coordinate> coordinateList = new ArrayList();
    private static List<Coordinate> agregated = new ArrayList();
    private static List<LineStringB> simplifiedLine = new ArrayList();
    private static List<MultiPolygon> simplifiedPolygon = new ArrayList();


	/**
	 * This function creates a shapefile of a given geometry type
	 * @param geomType The geometry type to create 
	 * @param fileName The name that we want to give to the shapefile
	 * @throws IOException
	 * @throws SchemaException
	 */
	
	public static void createShp(String geomType, String fileName) throws IOException, SchemaException {
		
		//Shapefile creation
		FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
	    File file = new File(fileName);
	    
	    Map map = Collections.singletonMap( "url", file.toURI().toURL() );
	    DataStore myData = factory.createNewDataStore( map );
	    
	    // If we want to create a Point shapefile
	    if (geomType.equals("com.vividsolutions.jts.geom.Point")) {
		    SimpleFeatureType featureType = DataUtilities.createType( "Results", "geom:Point" );
		    myData.createSchema( featureType );
	    }
	    
	    // If we want to create a MultiLineString shapefile
	    if (geomType.equals("com.vividsolutions.jts.geom.MultiLineString")) {
		    SimpleFeatureType featureType = DataUtilities.createType( "Results", "geom:MultiLineString" );
		    myData.createSchema( featureType );
	    }
	    
	    // If we want to create a Polygon shapefile
	    if (geomType.equals("com.vividsolutions.jts.geom.Polygon")) {
		    SimpleFeatureType featureType = DataUtilities.createType( "Results", "geom:Polygon" );
		    myData.createSchema( featureType );
	    }
	}
	
	
	/**
	 * This function gets the features collection of a given Shapefile
	 * @param fileName The name of the Shapefile
	 * @return the shapefile features 
	 * @throws IOException
	 */
	public static FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatureCollection(String fileName) throws IOException {
		// First we read the Shapefile
		File file = new File(fileName);

	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("url", file.toURI().toURL());

	    DataStore dataStore = DataStoreFinder.getDataStore(map);
	    String typeName = dataStore.getTypeNames()[0];
	    
	    SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);    

	    FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore
	            .getFeatureSource(typeName);
	    Filter filter = Filter.INCLUDE; // ECQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)")

	    FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
	    
	    return collection;
	}
	
	
	/**
	 * This function reads a shapefile and applies simplification to its geometry
	 * @throws IOException
	 * @throws SchemaException
	 * @throws ParseException 
	 */
	public static void readShp(String fileName) throws IOException, SchemaException, ParseException {
		
		// First we read the Shapefile
		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = getFeatureCollection(fileName);

    	FeatureIterator<SimpleFeature> features = collection.features();

    	
    	// Then we apply a simplification by geometry
        while (features.hasNext()) {
            SimpleFeature feature = features.next();

            // If the feature is a Polygon
            if (feature.getDefaultGeometry().getClass().toString().equals("class com.vividsolutions.jts.geom.MultiPolygon")) {

            	ReferencedEnvelope env = collection.getBounds();
            	double left = env.getMinX();
            	double right = env.getMaxX();
            	double top = env.getMaxY();
            	double bottom = env.getMinY();
            	
            	double globalArea = Math.abs((Math.abs(right) - Math.abs(left)) * ((Math.abs(top) - Math.abs(bottom)))); 
            	
            	// This area corresponds to a rectangle area so it is reasonnable to divide into  surfaces and take this surface as the minimum acceptable
            	double threshold = globalArea / 4;

            	List<MultiPolygon> p = PolygonB.convertPolygon(feature.getDefaultGeometry().toString(), threshold); 
            	for (MultiPolygon multipoly: p) {
            		simplifiedPolygon.add(multipoly);
            	}
            	

            }
            	         
            // If the feature is a LineString
            else if (feature.getDefaultGeometry().getClass().toString().equals("class com.vividsolutions.jts.geom.MultiLineString")) {

            	List<LineStringB> l = LineStringB.convertMultiLineString(feature.getDefaultGeometry().toString());
            	for (LineStringB line: l) {
                	simplifiedLine.add(line);
            	}
            	
            }
            
            // If it is a Point
            else if (feature.getDefaultGeometry().getClass().toString().equals("class com.vividsolutions.jts.geom.Point")) {
            	
            	Points.convertPoint(feature.getDefaultGeometry().toString(), coordinateList);

            }
        }
        
        // Once we've filled the list of points, if it's a Point geometry Shapefile
        if (!coordinateList.isEmpty()) {
        	// We simplify it
        	Points listPoint = new Points(coordinateList);        	
        	
        	// We get the regular grid
        	List<Polygon> listPolygon = listPoint.regularGrid(listPoint.coordToPoint(coordinateList));

        	// And eventually agregate the points
        	Points agreg = listPoint.agregatePoints(listPolygon, coordinateList);
        	for (Coordinate c: agreg.getCoordinate()) {
        		agregated.add(c);
        		}
        }
	}
	

	
	/**
	 * This function creates a point shapefile and add the correct features to it
	 * @param inputName the input shapefile (before any simplification)
	 * @throws SchemaException
	 * @throws IOException
	 */
	public static void createFeaturePoint(String inputName) throws SchemaException, IOException {
		// We get the real name of the shapefile to generate a new one beginning the same way
		int beginIndex = inputName.indexOf("/", 0);
		int endIndex = inputName.indexOf(".shp", beginIndex);
		String realName = inputName.substring(beginIndex +1, endIndex);
		File newFile = new File("output/" + realName + "_result.shp");

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        
		//Type creation

        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		b.setName("MyFeatureType");
		b.setCRS(DefaultGeographicCRS.WGS84);
		
		//Feature collection creation

		b.add("the_geom", Point.class);
		b.add("X", Double.class);
		b.add("Y", Double.class);
		final SimpleFeatureType featureType = b.buildFeatureType();
		
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(featureType);

		//FeatureCollection<SimpleFeatureType, SimpleFeature> collection = getFeatureCollection(fileName);
		List<Coordinate> finalPoint = new ArrayList();

		// If there was points to be agregated, we add the new point to the shapefile
		if (agregated.size() != 0) {	
			for (int i =0; i< agregated.size(); i++) {
				finalPoint.add(agregated.get(i));

			}
		}
		// Else, we rewrite the previous point
		else {
			for (int i =0; i< coordinateList.size(); i++) {
				finalPoint.add(coordinateList.get(i));

			}
		}
		for (Coordinate c: finalPoint) {
			Point point = geometryFactory.createPoint(c);
			featureBuilder.add(point);
			featureBuilder.add(point.getX());
			featureBuilder.add(point.getY());
			SimpleFeature feature = featureBuilder.buildFeature( "fid.1" );
			features.add(feature);
		}
			
		
		//Transaction
		// We create the feature and write it to the layer
		Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
        	
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            featureStore.setTransaction(transaction);
            
            try {
                featureStore.addFeatures(collection);
                transaction.commit();

            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();

            } finally {
                transaction.close();
            }

        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }


		
	}
	
	/**
	 * This function create a multilinestring shapefile and add the correct features in it
	 * @param inputName
	 * @throws SchemaException
	 * @throws IOException
	 */
	public static void createFeatureLine(String inputName) throws SchemaException, IOException {
		// We get the real name of the shapefile to generate a new one beginning the same way
		int beginIndex = inputName.indexOf("/", 0);
		int endIndex = inputName.indexOf(".shp", beginIndex);
		String realName = inputName.substring(beginIndex +1, endIndex);
		File newFile = new File("output/" + realName + "_result.shp");

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        
		//Type creation

        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		b.setName("MyFeatureType");
		b.setCRS(DefaultGeographicCRS.WGS84);
		
		//Feature collection creation

		b.add("the_geom", MultiLineString.class);

		final SimpleFeatureType featureType = b.buildFeatureType();
		
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(featureType);

		// We get the simplified elements
		for (LineStringB l: simplifiedLine) {
			Coordinate start = new Coordinate(l.getNode(0).getCoordinate().x,l.getNode(0).getCoordinate().y);
			Coordinate end = new Coordinate(l.getNode(1).getCoordinate().x,l.getNode(1).getCoordinate().y);
			Coordinate[] coord = new Coordinate[]{start, end};
			
			LineString line = geometryFactory.createLineString(coord);
			
			featureBuilder.add(line);
			
			SimpleFeature feature = featureBuilder.buildFeature( "fid.1" );
			features.add(feature);
		}
        		
		//Transaction	
		// We create the feature and write it to the layer
		Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
        	
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            featureStore.setTransaction(transaction);
            
            try {
                featureStore.addFeatures(collection);
                transaction.commit();

            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();

            } finally {
                transaction.close();
            }

        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }


		
	}
	
	/**
	 * This function create a multilinestring shapefile and add the correct features in it
	 * @param inputName
	 * @throws SchemaException
	 * @throws IOException
	 */
	public static void createFeaturePolygon(String inputName) throws SchemaException, IOException {
		// We get the real name of the shapefile to generate a new one beginning the same way
		int beginIndex = inputName.indexOf("/", 0);
		int endIndex = inputName.indexOf(".shp", beginIndex);
		String realName = inputName.substring(beginIndex +1, endIndex);
		File newFile = new File("output/" + realName + "_result.shp");

        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("url", newFile.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        
		//Type creation

        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		b.setName("MyFeatureType");
		b.setCRS(DefaultGeographicCRS.WGS84);
		
		//Feature collection creation

		b.add("the_geom", MultiPolygon.class);

		final SimpleFeatureType featureType = b.buildFeatureType();
		
		GeometryBuilder builder = new GeometryBuilder( DefaultGeographicCRS.WGS84 );
		
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		newDataStore.createSchema(featureType);

		// We get the simplified elements
		
		for (MultiPolygon mp: simplifiedPolygon) {
			Coordinate[] coord = mp.getCoordinates();
			
			//MultiPolygon m = geometryFactory.createMultiPolygon(mp);
			
			LinearRing ring = geometryFactory.createLinearRing( coord );
			LinearRing holes[] = null; // use LinearRing[] to represent holes
			Polygon polygon = geometryFactory.createPolygon(ring, holes );
			
			featureBuilder.add(polygon);
			
			SimpleFeature feature = featureBuilder.buildFeature( "fid.1" );
			features.add(feature);
		}
        
		
		//Transaction
		// We create the feature and write it to the layer
		Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

        if (featureSource instanceof SimpleFeatureStore) {
        	
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            featureStore.setTransaction(transaction);
            
            try {
                featureStore.addFeatures(collection);
                transaction.commit();

            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();

            } finally {
                transaction.close();
            }

        } else {
            System.out.println(typeName + " does not support read/write access");
            System.exit(1);
        }


		
	}

	
}