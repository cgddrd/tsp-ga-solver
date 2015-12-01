package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;
import uk.ac.aber.clg11.tsp.ga.GAPopulation;

public class TSPPopulation extends GAPopulation<Double, TSPLocation, TSPRoute> {
	
	//public TSPPopulation(int populationSize) {
	//	super(populationSize);
	//}
	
	//public TSPPopulation(ArrayList<TSPRoute> populationChromosomes) {
	//	super(populationChromosomes);
	//}
	
	public TSPPopulation(int populationSize) {
		
		this.chromosomeCandidates = new TSPRoute[populationSize];
		
	}

	public TSPPopulation(int populationSize, ArrayList<TSPLocation> genes, boolean shouldInit) {
		
		//this.chromosomeCandidates = new ArrayList<TSPRoute>(populationSize);
		
		this.chromosomeCandidates = new TSPRoute[populationSize];
		
		if (shouldInit) {
			
			// If we have been asked to initialize the population, set each chromosome to the collection of genes and then randomly shuffle each.
			for (int i = 0; i < populationSize; i++) {
				
				// Setting true on the constructor for TSPRoute makes sure the genes that make up the chromosome are shuffled.
				//this.addRoute(new TSPRoute(genes, true));
				
				TSPRoute route = new TSPRoute(genes, true);
				
				this.addChromosomeAtIndex(route, i);
			
			}
		}
		
	}
	
	//public ArrayList<TSPRoute> getRoutes() {
	//	return this.chromosomeCandidates;
	//}
	
	//public void addRoute(TSPRoute newRoute) {
		//this.addChromosome(newRoute);
	//}
	
	//public void addRoutes(ArrayList<TSPRoute> newRoutes) {
	//	this.addChromosomes(newRoutes);
	//}
	
	public void addRouteAtIndex(TSPRoute newRoute, int index) {
		this.addChromosomeAtIndex(newRoute, index);
	}
	
	public TSPRoute getRouteAtIndex(int index) {
		return this.getChromosomeCandidate(index);
	}

	@Override
	public TSPRoute getFittestCandidate() {
		
		// We need to make sure that we reset the current fittest to null, to prevent an out-of-date fittest candidate.
		TSPRoute currentFittestCandidate = this.getRouteAtIndex(0);
		
		for (int i = 1; i< this.getPopulationSize(); i++) {
			
			if (currentFittestCandidate.getRouteDistance() >= this.getRouteAtIndex(i).getRouteDistance()) {
				
				currentFittestCandidate = this.getRouteAtIndex(i);
			}
		}
		
		return currentFittestCandidate;
		
	}

	@Override
	public Double getPopulationFitnessSum() {
			
		Double fitnessSum = 0.0;
		
		for (TSPRoute current : this.chromosomeCandidates) {
			fitnessSum += current.getFitness();
		}
		
		return fitnessSum;
		
	}

	@Override
	public Double getPopulationFitnessAverage() {
		
		Double populationFitnessSum = this.getPopulationFitnessSum();
		
		return populationFitnessSum / this.getPopulationSize();
		
	}

	@Override
	public Double getPopulationFitnessMax() {
		
//		Double maxRouteFitness = 0.0;
//		
//		for (TSPRoute currentRoute : this.getRoutes()) {
//			
//			if (maxRouteFitness < currentRoute.getFitness()) {
//				
//				maxRouteFitness = currentRoute.getFitness();
//				
//			}
//		}
//		
//		return maxRouteFitness;
		
		return null;
		
	}

}
