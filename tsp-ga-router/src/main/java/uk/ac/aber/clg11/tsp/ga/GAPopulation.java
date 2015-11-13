package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;

public abstract class GAPopulation<T extends Number, K extends GAGene, S extends GAChromosome> implements IGAPopulation<T, K> {
	
	private ArrayList<S> chromosomeCandidates;
	
	public GAPopulation(int populationSize) {
		chromosomeCandidates = new ArrayList<S>(populationSize);
	}
	
	public GAPopulation(int populationSize, boolean shouldInit, ArrayList<K> genes) {
		
		chromosomeCandidates = new ArrayList<S>(populationSize);
		
		if (shouldInit) {
			
			// If we have been asked to initialize the population, set each chromosome to the collection of genes and then randomly shuffle each.
			for (int i = 0; i < populationSize; i++) {
				
				chromosomeCandidates.get(i).setGenes((ArrayList<GAGene>) genes, true);
			
			}
		}
	}
	
	public S getChromosomeCandidate(int candidateIndex) {
		return this.chromosomeCandidates.get(candidateIndex);
	}
	
	protected void addChromosome(S newChromosome) {
		this.chromosomeCandidates.add(newChromosome);
	}
	
	public int getPopulatationSize() {
		return this.chromosomeCandidates.size();
	}

}
