package eu.ensg.tsi.azarzelli.gama.generation;

import static org.junit.Assert.*;

import org.junit.Test;

public class RandomStrategyTest {

	@Test
	public void testValues() {
		RandomStrategy randomStrat = new RandomStrategy();
		double[][] matrix = new double[100][100];
		
		randomStrat.generate(matrix);
		
		double sum = 0;
		
        for (int i = 0; i<matrix.length; i++) {
        	for (int j = 0; j<matrix[0].length; j++) {
        		sum += matrix[i][j] ;
        	}
        }
	    
		assertTrue(matrix[0][0] >= 0);
		assertTrue(matrix[0][0] < 1);
		assertTrue(sum > 1);
		assertTrue(sum < 10000);
	}

}
