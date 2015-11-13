package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;
import uk.ac.aber.clg11.tsp.ga.GAPopulation;

public class TSPPopulation extends GAPopulation<Double, TSPLocation, TSPRoute> {
	
	public TSPPopulation(int populationSize) {
		super(populationSize);
	}

	public TSPPopulation(int populationSize, boolean shouldInit, ArrayList<TSPLocation> genes) {
		super(populationSize, shouldInit, genes);
	}
	
	public void addRoute(TSPRoute newRoute) {
		super.addChromosome(newRoute);
	}

	@Override
	public GAChromosome<Double, TSPLocation> getFittestCandidate() {
		// TODO Auto-generated method stub
		return null;
	}

}
