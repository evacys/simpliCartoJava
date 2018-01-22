package eu.ensg.tsi.azarzelli.gama.generation;

/**
 * 
 */
public final class RandomStrategy implements IGenerationStrategy {

    /**
     * Generating a totally random grid of values between 0 and 1
     * @param matrix empty matrix to fill
     */
	@Override
    public void generate(double[][] matrix) {
        for (int i = 0; i<matrix.length; i++) {
        	for (int j = 0; j<matrix[0].length; j++) {
        		matrix[i][j] = Math.random();
        	}
        }
    }

}