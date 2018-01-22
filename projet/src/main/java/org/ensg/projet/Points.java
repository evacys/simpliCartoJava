package org.ensg.projet;

import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
/**
 * This class allows to handle and simplfy point layers
 * @author evach
 *
 */

public class Points {
	
	private List<Coordinate> coordinate;

	
	/**
	 * This function allows to access the coordinates of the point list
	 * @return list of coordinates
	 */
	public List<Coordinate> getCoordinate() {
		return this.coordinate;
	}

	/**
	 * This function allows to set a list of coordinates to the current object
	 * @param coordinate
	 */
	public void setCoordinate(List<Coordinate> coordinate) {
		this.coordinate = coordinate;
	}

	/**
	 * Empty Points constructor
	 */
	public Points() {
		this.coordinate = new ArrayList();
	}
	
	
	/**
	 * Points constructor based on an given list of coordinates 
	 * @param coordinate
	 */
	public Points(List<Coordinate> coordinates) {
		this.coordinate = coordinates;
	}
	
	
	/**
	 * This function adds a coordinate into the current list of points
	 * @param coordinate the coordinate to add to the list
	 */
	public void addCoordinate(Coordinate coord) {
		this.coordinate.add(coord);
	}
	
	/**
	 * This function returns the envelope of a clouf of points
	 * @return The 4 extreme coordinates List<Point> {(xmin, ymin), (xmin, ymax), (xmax, ymin), (xmax, ymax)}
	 */
	public List<Point> getEnvelope(List<Coordinate> coordinates) {
		// We initialize th min and max
		double minX = coordinates.get(0).x;
		double minY = coordinates.get(0).y;
		double maxX = coordinates.get(0).x;
		double maxY = coordinates.get(0).y;
		
		for (Coordinate coord: coordinates) {
			if (coord.x < minX) {
				minX = coord.x;
			}
			
			if (coord.y < minY) {
				minY = coord.y;
			}
			
			if (coord.y > maxX) {
				maxX = coord.x;
			}
			
			if (coord.y > maxY) {
				maxY = coord.y;
			}
		}
		
		// We create 4 points that have the correct enveloppe coordinates
		Coordinate coord1 = new Coordinate(minX, minY);
		Coordinate coord2 = new Coordinate(minX, maxY);
		Coordinate coord3 = new Coordinate(maxX, minY);
		Coordinate coord4 = new Coordinate(maxX, maxY);

		List<Point> points  = new ArrayList();
		points.add(new Point(coord1, null, 0));
		points.add(new Point(coord2, null, 0));
		points.add(new Point(coord3, null, 0));
		points.add(new Point(coord4, null, 0));

		return points;
	}
	
	
	/**
	 * This function transforms a Coordinate into a Point
	 * @param coord the coordinate to transform
	 * @return
	 */
	public List<Point> coordToPoint(List<Coordinate> coordinates) {
		List<Point> points = new ArrayList();
		for (Coordinate c: coordinates) {
			Point p = new Point(c, null, 0);
			points.add(p);
		}
		return points;
	}
	
	/**
	 * This function transforms a Point to a Coordinate
	 * @param point The point to transform
	 * @return
	 */
	public static List<Coordinate> pointToCoord(List<Point> points) {
		List<Coordinate> coordinates = new ArrayList();
		for (Point p: points) {
			Coordinate c = new Coordinate(p.getX(), p.getY());
			coordinates.add(c);
		}
		return coordinates;
	}
	
	/**
	 * This function calculate a regular grid for a list of point in order to agregate them later
	 * @param listPoint The list of points to aggregate
	 * @return The list of polygon that composes the regular grid
	 */
	public List<Polygon> regularGrid(List<Point> listPoint) {

		List<Polygon> regularGrid = new ArrayList();

		// We get the cloud of points envelope
		List<Point> points  = getEnvelope(pointToCoord(listPoint));

		// Coordinate (minX, minY)
		Coordinate coord1 = new Coordinate(points.get(0).getX(), points.get(0).getY());
		// Coordinate (minX, maxY)
		Coordinate coord2 = new Coordinate(points.get(1).getX(), points.get(1).getY());
		// Coordinate (maxX, minY)
		Coordinate coord3 = new Coordinate(points.get(2).getX(), points.get(2).getY());
		// Coordinate (maxX, maxY)
		Coordinate coord4 = new Coordinate(points.get(3).getX(), points.get(3).getY());

		// We calculate the horizontal and vertical spacement
		double dX = Math.ceil(coord4.x - coord1.x) /(listPoint.size()/2);
		double dY = Math.ceil(coord4.y - coord1.y) /(listPoint.size()/2);

		
		// We get the number of lines and columns in the grid
		double nbLine = Math.ceil(coord4.x - coord1.x)/dX;
		double nbColumn = Math.ceil(coord4.y - coord1.y)/dY;

		// To generate the regular grid
		//x = xmin et y = ymax + dY
		double x = coord1.x - dX;
		double y = coord4.y + dY;

		// For each line and column, we generate a polygon: width = dX , heigth = dY
		for (int i = 0; i< nbLine ; i++) {
			y = y - dY;
			x = coord1.x;

			for (int j = 0; j < nbColumn; j++) {

				GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
				
				Coordinate c1 = new Coordinate(x, y);
				Coordinate c2 = new Coordinate(x + dX, y);
				Coordinate c3 = new Coordinate(x + dX, y - dY);
				Coordinate c4 = new Coordinate(x, y - dY);
				
				// To close the polygon we need to specify the first point twice 
				Coordinate[] newPoly = new Coordinate[] {c1, c2, c3, c4, c1};
				
				
				LinearRing ring = geometryFactory.createLinearRing( newPoly );
				LinearRing holes[] = null; // use LinearRing[] to represent holes
				Polygon polygon = geometryFactory.createPolygon(ring, holes );

				regularGrid.add(polygon);

				x += dX;

			}

		}
		return regularGrid;
		

	}

	/** 
	 * This function aggregate the points that are within the same regular grid square
	 * @param listPolygon The polygon composing the regular grid
	 * @param listPoint The cloud of points to aggregate
	 * return Points The final geometry of agregated points
	 */
	
	public Points agregatePoints(List<Polygon> listPolygon, List<Coordinate> listCoord) {
	
		Points agregated = new Points();
		
		// Count the number of point within every square
		for (Polygon p: listPolygon) {

			int cmpt = 0;
			double sumX = 0;
			double sumY = 0;
	
			for (Coordinate c: listCoord) {
				Point pt = new Point(c, null, 0);
				if (pt.intersects(p)) {
					cmpt ++; 
					sumX += pt.getX();
					sumY += pt.getY();
				}
			}
			// For a polygon, if it intersects several points
			if (cmpt != 0) {
				// We generate a new point that is the center of all the points within the polygon
				Coordinate coordinate = new Coordinate(sumX/cmpt, sumY/cmpt);
				agregated.addCoordinate(coordinate);
				System.out.println(coordinate);
			}
			
		}
		return agregated;

	}
	
	
	/**
	 * This function convert a WKT Point into a Coordinate and add it to a list of already existing coordinates
	 * @param coord The point coordinates
	 * @param coordinateList The list to add the point to 
	 * @throws ParseException
	 */
	public static void convertPoint(String coord, List<Coordinate> coordinateList) throws ParseException {
    	GeometryFactory geomFactory = JTSFactoryFinder.getGeometryFactory();
    	
    	WKTReader reader = new WKTReader(geomFactory);
    	Point point = (Point) reader.read(coord);
    	
    	coordinateList.add(point.getCoordinate());

    	

	}
	
}
