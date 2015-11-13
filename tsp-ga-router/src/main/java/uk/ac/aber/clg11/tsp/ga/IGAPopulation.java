package uk.ac.aber.clg11.tsp.ga;

public interface IGAPopulation<T extends Number, K extends GAGene> {
	
	public GAChromosome<T, K> getFittestCandidate();

}
