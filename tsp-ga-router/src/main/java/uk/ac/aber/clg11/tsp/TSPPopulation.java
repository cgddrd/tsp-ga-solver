package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;
import uk.ac.aber.clg11.tsp.ga.GAPopulation;

public class TSPPopulation extends GAPopulation<Double, TSPLocation, TSPRoute> {
	
	public TSPPopulation(int populationSize) {
		super(populationSize);
	}

	public TSPPopulation(int populationSize, boolean shouldInit, ArrayList<TSPLocation> genes) {
		
		this.chromosomeCandidates = new ArrayList<TSPRoute>(populationSize);
		
		if (shouldInit) {
			
			// If we have been asked to initialize the population, set each chromosome to the collection of genes and then randomly shuffle each.
			for (int i = 0; i < populationSize; i++) {
				
				//chromosomeCandidates.add();
				
				this.addRoute(new TSPRoute(genes, true));
			
			}
		}
		
	}
	
	public ArrayList<TSPRoute> getRoutes() {
		return this.chromosomeCandidates;
	}
	
	public void addRoute(TSPRoute newRoute) {
		this.addChromosome(newRoute);
	}
	
	public void addRoutes(ArrayList<TSPRoute> newRoutes) {
		this.addChromosomes(newRoutes);
	}

	@Override
	public GAChromosome<Double, TSPLocation> getFittestCandidate() {
		
		for (TSPRoute currentCandidate : this.chromosomeCandidates) {
			
			// The fitness function for a TSPRoute gets LARGER, the SMALLER the total distance.
			if (this.currentFittestCandidate == null || currentCandidate.getFitness() >= currentFittestCandidate.getFitness()) {
				
				this.currentFittestCandidate = currentCandidate;
				
			}
		}
		
		return this.currentFittestCandidate;
		
	}

}
