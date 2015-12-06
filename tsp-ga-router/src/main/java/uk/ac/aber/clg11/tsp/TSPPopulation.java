package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;
import uk.ac.aber.clg11.tsp.ga.GAPopulation;

/**
 * Provides a representation of an individual TSP population.
 * 
 * Extends the 'GAChromosome' abstract class.
 * 
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 */
public class TSPPopulation extends GAPopulation<Double, TSPLocation, TSPRoute> {
	
	// Set the total population distance to -1 by default in order to ensure distance is calculated for the first time.
	private int populationDistance = -1;
	
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
		
		// We need to make sure that we reset the current fittest to null, to prevent an out-of-date fittest candidate from being returned.
		this.currentFittestCandidate = null;
		
		for (TSPRoute currentCandidate : this.chromosomeCandidates) {
			
			// The fitness score for a TSPRoute INCREASES as the total distance DECREASES.
			if (this.currentFittestCandidate == null || currentCandidate.getFitness() >= currentFittestCandidate.getFitness()) {
				
				this.currentFittestCandidate = currentCandidate;
				
			}
		}
		
		return this.currentFittestCandidate;
		
	}
	
	/**
	 * Sums the combined distances for all TSPRoutes within the population.
	 * @return The total distance for all TSPRoutes within the population.
	 */
	public Integer getPopulationDistanceSum() {
		
		if (this.populationDistance < 0) {
			
			int distanceTotal = 0;
			
			for (TSPRoute currentRoute : this.chromosomeCandidates) {
				distanceTotal += currentRoute.getRouteDistance();
			}
			
			this.populationDistance = distanceTotal;
		}
		
		return this.populationDistance;
		
	}
	
	/**
	 * Calculates the average distance across all TSPRoutes within the population.
	 * @return The average distance across all TSPRoutes within the population.
	 */
	public Integer getPopulationDistanceAverage() {
		
		Integer populationDistanceSum = this.getPopulationDistanceSum();
		
		return populationDistanceSum / this.getPopulationSize();
		
	}
	
	@Override
	public Double getPopulationFitnessSum() {
		
		if (this.populationFitness == null) {
			
			Double fitnessTotal = 0.0;
			
			for (TSPRoute currentRoute : this.chromosomeCandidates) {
				fitnessTotal += currentRoute.getFitness();
			}
			
			this.populationFitness = fitnessTotal;
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
	
		return this.getFittestCandidate().getFitness();

	}

}
