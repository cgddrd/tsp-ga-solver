package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;

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
	
	public abstract T getPopulationFitnessSum();
	
}
