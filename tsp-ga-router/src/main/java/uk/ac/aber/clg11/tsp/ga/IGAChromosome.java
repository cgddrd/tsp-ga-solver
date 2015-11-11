package uk.ac.aber.clg11.tsp.ga;

public interface IGAChromosome<T extends Number> {
	
	public T calcFitness();
}
