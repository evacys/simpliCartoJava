package eu.ensg.tsi.azarzelli.gama.domain;

import eu.ensg.tsi.azarzelli.gama.generation.IGenerationStrategy;
import eu.ensg.tsi.azarzelli.gama.io.AscWriter;
import eu.ensg.tsi.azarzelli.gama.io.GeotiffWriter;

/**
 * 
 */
public class Terrain {

	//Attributes ------------------------------------------
	
    /**
     * Minimum X coordinate. Units depends of the projection.
     */
    private double xMin;

    /**
     * Minimum Y coordinate. Units depends of the projection.
     */
    private double yMin;
    
    /**
     * Maximum X coordinate. Units depends of the projection.
     */
    private double xMax;

    /**
     * Maximum Y coordinate. Units depends of the projection.
     */
    private double yMax;

    /**
     * Size of a DEM cell  along X and Y axes in the projection unit.
     */
    private double cellSize;
    
    /**
     * Name of the projection system. Example: "EPSG:4326"
     */
    private String projectionName;

    /**
     * Factor to multiply the default results (between 0 and 1)
     * and obtain realistic altitudes. Corresponds to the maximum
     * altitude wanted.
     */
    private double altitudeFactor;


    /**
     * Procedural generation algorithm strategy.
     */
    private IGenerationStrategy generationStrategy;

    /**
     * Elevation matrix containing an altitude value for each point.
     */
    private double[][] matrix;
    
    
    // Constructors ---------------------------------------

    /**
     * Constructor using all the arguments, called by all the other basic constructors
     */
    public Terrain(double xMin, double yMin, double xMax, double yMax, double cellSize,
    		String projectionName, double altitudeFactor, String generationStrategyName) {

		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
		this.projectionName = projectionName;
		this.altitudeFactor = altitudeFactor;
		this.cellSize = cellSize;
		
		int ySize = (int) ((yMax-yMin)/cellSize);
		int xSize = (int) ((xMax-xMin)/cellSize);
		
		this.matrix = new double[ySize][xSize];
		
		StrategyFactory factory = new StrategyFactory();
		this.generationStrategy = factory.createStrategy(generationStrategyName);
	}
    
    
	public Terrain(double xMin, double yMin, double xMax, double yMax,
			double cellSize) {
		
		this(xMin,yMin,xMax,yMax,cellSize,"EPSG:4326",1.,"random");
	}
	

	public Terrain(String generationStrategyName) {
		this(0.,0.,100.,100.,1.,"EPSG:4326",1.,generationStrategyName);
	}

	
	// Getters --------------------------------------------
	
	public double[][] getMatrix() {
		return matrix;
	}
	
	public IGenerationStrategy getGenerationStrategy() {
		return generationStrategy;
	}

	
	// Methods --------------------------------------------

	/**
     * Generates procedurally the terrain according to the
     * generation strategy.
     */
    public void generate() {
        generationStrategy.generate(matrix);
    }

    /**
     * Writes the Terrain matrix into an asc file.
     * @param filepath: path of the file to write.
     */
    public void toAsc(String filepath) {
        AscWriter writer = new AscWriter();
        writer.write(this, filepath);
    }

    /**
     * Writes the Terrain matrix into a geotiff file.
     * @param filepath: path of the file to write.
     */
    public void toGeotiff(String filepath) {
    	GeotiffWriter writer = new GeotiffWriter();
        writer.write(this, filepath);

    }

}