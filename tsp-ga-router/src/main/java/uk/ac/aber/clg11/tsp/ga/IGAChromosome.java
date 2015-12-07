package uk.ac.aber.clg11.tsp.ga;

/**
 * Interface providing method signatures for common operations relating to a GA chromosome required across all GA implementations.
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 * @param <T> The type extending 'java.lang.Number' that represents the fitness value for a GAChromosome. (Typically 'Double' or 'Integer')
 */
public interface IGAChromosome<T extends Number> {
	
	/**
	 * Calculates the combined sum of the fitnesses for all of the individuals represented by the chromosome.
	 * @return The total fitness for the chromosome.
	 */
	public T getFitness();
	
	/**
	 * Returns the size of the chromosome.
	 * @return The size of the collection represented by the chromosome.
	 */
	public int getSize();
	
	/**
	 * Resets the fitness of the chromosome. Should be called following an update to the chromosome representation.
	 */
	void resetFitness();
}
