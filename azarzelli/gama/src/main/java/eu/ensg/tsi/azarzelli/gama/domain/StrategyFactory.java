package eu.ensg.tsi.azarzelli.gama.domain;

import eu.ensg.tsi.azarzelli.gama.exceptions.GenerationMethodNotFoundException;
import eu.ensg.tsi.azarzelli.gama.generation.DiamondSquareStrategy;
import eu.ensg.tsi.azarzelli.gama.generation.IGenerationStrategy;
import eu.ensg.tsi.azarzelli.gama.generation.PerlinNoiseStrategy;
import eu.ensg.tsi.azarzelli.gama.generation.RandomStrategy;
import eu.ensg.tsi.azarzelli.gama.generation.ValueNoiseStrategy;

/**
 * Factory class for the strategies, used to encapsulate the creation
 * in the Terrain initialization.
 */
public class StrategyFactory {

    /**
     * Strategy creation method from its name
     * @param strategyName name of the wanted generation strategy.
     * @return an instance of the corresponding strategy.
     * @throws GenerationMethodNotFoundException 
     */
    public IGenerationStrategy createStrategy(String strategyName) throws GenerationMethodNotFoundException {
        strategyName = strategyName.toLowerCase();
        if (strategyName.equals("random")) {
        	return new RandomStrategy();
        } else if (strategyName.equals("diamondsquare")) {
        	return new DiamondSquareStrategy();
        } else if (strategyName.equals("perlinnoise")) {
        	return new PerlinNoiseStrategy();
        } else if (strategyName.equals("valuenoise")) {
        	return new ValueNoiseStrategy();
        } else {
        	throw new GenerationMethodNotFoundException();
        }
    }

}