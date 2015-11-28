package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;
import uk.ac.aber.clg11.tsp.ga.GAPopulation;

public class TSPPopulation extends GAPopulation<Double, TSPLocation, TSPRoute> {
	
	public TSPPopulation(int populationSize) {
		super(populationSize);
	}
	
	public TSPPopulation(ArrayList<TSPRoute> populationChromosomes) {
		super(populationChromosomes);
	}

	public TSPPopulation(int populationSize, boolean shouldInit, ArrayList<TSPLocation> genes) {
		
		this.chromosomeCandidates = new ArrayList<TSPRoute>(populationSize);
		
		if (shouldInit) {
			
			// If we have been asked to initialize the population, set each chromosome to the collection of genes and then randomly shuffle each.
			for (int i = 0; i < populationSize; i++) {
				
				// Setting true on the constructor for TSPRoute makes sure the genes that make up the chromosome are shuffled.
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
	
	public TSPRoute getRouteAtIndex(int index) {
		return this.getChromosomeCandidate(index);
	}

	@Override
	public GAChromosome<Double, TSPLocation> getFittestCandidate() {
		
		// We need to make sure that we reset the current fittest to null, to prevent an out-of-date fittest candidate.
		this.currentFittestCandidate = null;
		
		for (TSPRoute currentCandidate : this.chromosomeCandidates) {
			
			// REMEMBER: The fitness function for a TSPRoute gets LARGER when the total distance gets SMALLER.
			if (this.currentFittestCandidate == null || currentCandidate.getFitness() >= currentFittestCandidate.getFitness()) {
				
				this.currentFittestCandidate = currentCandidate;
				
			}
		}
		
		return this.currentFittestCandidate;
		
	}

	@Override
	public Double getPopulationFitnessSum() {
		
		if (this.populationFitness == null) {
			
			Double fitnessSum = 0.0;
			
			for (TSPRoute current : this.chromosomeCandidates) {
				fitnessSum += current.getFitness();
			}
			
			this.populationFitness = fitnessSum;
		}
		
		return this.populationFitness;
		
	}

	@Override
	public Double getPopulationFitnessAverage() {
		
		Double populationFitnessSum = this.getPopulationFitnessSum();
		
		return populationFitnessSum / this.getPopulationSize();
		
	}

	@Override
	public Double getPopulationFitnessMax() {
		
		Double maxRouteFitness = 0.0;
		
		for (TSPRoute currentRoute : this.getRoutes()) {
			
			if (maxRouteFitness < currentRoute.getFitness()) {
				
				maxRouteFitness = currentRoute.getFitness();
				
			}
		}
		
		return maxRouteFitness;
		
	}

}
