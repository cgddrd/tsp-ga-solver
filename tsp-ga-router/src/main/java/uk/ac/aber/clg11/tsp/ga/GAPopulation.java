package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;

public abstract class GAPopulation<T extends Number, K extends GAGene, S extends GAChromosome> implements IGAPopulation<T, K> {
	
	//protected ArrayList<S> chromosomeCandidates;
	protected S[] chromosomeCandidates;
	
	public GAPopulation() {}
	
	//public GAPopulation(int populationSize) {
		//chromosomeCandidates = new ArrayList<S>(populationSize);
		
		//chromosomeCandidates = new S[populationSize];
		
	//}
	
	//public GAPopulation(ArrayList<S> chromosomes) {
	//	chromosomeCandidates = new ArrayList<S>(chromosomes);
	//}
	
	protected S getChromosomeCandidate(int candidateIndex) {
		//return this.chromosomeCandidates.get(candidateIndex);
		
		return chromosomeCandidates[candidateIndex];
	}
	
	//protected void addChromosome(S newChromosome) {
	//	this.chromosomeCandidates.add(newChromosome);
	//}
	
	protected void addChromosomeAtIndex(S newChromosome, int index) {
		//this.chromosomeCandidates.add(newChromosome);
		
		chromosomeCandidates[index] = newChromosome;
	}
	
	//public void removeChromosome(int removeIndex) {
	//	this.chromosomeCandidates.remove(removeIndex);
		
		
	//}
	
	//public void removeChromosome(GAChromosome chromosomeToRemove) {
	//	this.chromosomeCandidates.remove(chromosomeToRemove);
	//}
	
//	protected void addChromosomes(ArrayList<S> newChromosomes) {
	//	this.chromosomeCandidates.addAll(newChromosomes);
	//}
	
	public int getPopulationSize() {
		//return this.chromosomeCandidates.size();
		
		return chromosomeCandidates.length;
	}
	
	public abstract T getPopulationFitnessSum();
	public abstract T getPopulationFitnessAverage();
	public abstract T getPopulationFitnessMax();
	
}
