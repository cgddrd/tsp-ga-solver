package uk.ac.aber.clg11.tsp.ga;

/**
 * Interface providing method signatures for common operations relating to a GA population required across all GA implementations.
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 * @param <T> The type extending 'java.lang.Number' that represents the fitness value for a GAChromosome. (Typically 'Double' or 'Integer')
 * @param <K> The type extending 'GAGene' that represents an individual gene within a GAChromosome within the current GA implementation. 
 */
public interface IGAPopulation<T extends Number, K extends GAGene> {
	
	/**
	 * Returns the chromosome (solution candidate) that holds the highest fitness score from the population.
	 * @return The strongest 'GAChromosome' instance from the collection held in the population. 
	 */
	public GAChromosome<T, K> getFittestCandidate();
	
	/**
	 * Returns the total number of chromosomes held in the population.
	 * @return Integer representing the total number of chromosomes held in the population.
	 */
	public int getPopulationSize();
	
	/**
	 * Sums the combined fitness for all chromosomes within the population.
	 * @return The total fitness for all chromosomes within the population.
	 */
	public T getPopulationFitnessSum();
	
	/**
	 * Calculates the average fitness across all chromosomes within the population.
	 * @return The average fitness across all chromosomes within the population.
	 */
	public T getPopulationFitnessAverage();
	
	/**
	 * Determines the highest fitness score provided across the collection of chromosomes within the population.
	 * @return The highest fitness score provided across the collection of chromosomes within the population.
	 */
	public T getPopulationFitnessMax();

}
