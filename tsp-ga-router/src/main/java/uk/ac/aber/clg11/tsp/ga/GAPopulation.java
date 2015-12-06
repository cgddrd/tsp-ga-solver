package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;

/**
 * Abstract class providing a common representation for an individual GA population (collection of GA chromosomes) within the current GA implementation.
 * 
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 * @param <T> The type extending 'java.lang.Number' that represents the fitness value for a GAChromosome. (Typically 'Double' or 'Integer')
 * @param <K> The type extending 'GAGene' that represents an individual gene within a GAChromosome within the current GA implementation. 
 * @param <S> The type extending 'GAChromosome' that represents an individual chromosome within the current GA implementation. 
 */
public abstract class GAPopulation<T extends Number, K extends GAGene, S extends GAChromosome> implements IGAPopulation<T, K> {
	
	protected ArrayList<S> chromosomeCandidates;
	protected S currentFittestCandidate = null;
	protected T populationFitness = null;
	
	public GAPopulation() {}
	
	public GAPopulation(int populationSize) {
		chromosomeCandidates = new ArrayList<S>(populationSize);
	}
	
	public GAPopulation(ArrayList<S> chromosomes) {
		chromosomeCandidates = new ArrayList<S>(chromosomes);
	}
	
	protected S getChromosomeCandidate(int candidateIndex) {
		return this.chromosomeCandidates.get(candidateIndex);
	}
	
	protected void addChromosome(S newChromosome) {
		this.chromosomeCandidates.add(newChromosome);
	}
	
	public void removeChromosome(int removeIndex) {
		this.chromosomeCandidates.remove(removeIndex);
	}
	
	public void removeChromosome(GAChromosome chromosomeToRemove) {
		this.chromosomeCandidates.remove(chromosomeToRemove);
	}
	
	protected void addChromosomes(ArrayList<S> newChromosomes) {
		this.chromosomeCandidates.addAll(newChromosomes);
	}
	
	public int getPopulationSize() {
		return this.chromosomeCandidates.size();
	}
	
	// These method signatures inherited from the interface must be implemented for a specific problem space, therefore we make them abstract.
	public abstract T getPopulationFitnessSum();
	public abstract T getPopulationFitnessAverage();
	public abstract T getPopulationFitnessMax();
	
}
