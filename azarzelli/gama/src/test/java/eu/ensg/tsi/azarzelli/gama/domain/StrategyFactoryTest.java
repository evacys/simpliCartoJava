package eu.ensg.tsi.azarzelli.gama.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.ensg.tsi.azarzelli.gama.generation.IGenerationStrategy;
import eu.ensg.tsi.azarzelli.gama.generation.RandomStrategy;

public class StrategyFactoryTest {

	@Test
	public void creationTest() {
		StrategyFactory factory = new StrategyFactory();
		
		IGenerationStrategy strategy;
		strategy = factory.createStrategy("random");
		assertTrue(strategy instanceof RandomStrategy);
		
		strategy = factory.createStrategy("Random");
		assertTrue(strategy instanceof RandomStrategy);
		
		strategy = factory.createStrategy("RANDOM");
		assertTrue(strategy instanceof RandomStrategy);
	}

}
