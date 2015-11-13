package uk.ac.aber.clg11.tsp;

import java.util.Random;

import uk.ac.aber.clg11.tsp.ga.GAAlgorithm;
import uk.ac.aber.clg11.tsp.ga.GAChromosome;
import uk.ac.aber.clg11.tsp.ga.GAGene;
import uk.ac.aber.clg11.tsp.ga.GAPopulation;

public class TSPAlgorithm extends GAAlgorithm {

	public TSPAlgorithm(int mutationRate, int crossoverRate, int tournamentSize) {
		super(mutationRate, crossoverRate, tournamentSize);
	}
	
	public TSPRoute performTournamentSelection(TSPPopulation currentPopulation) {
		
		Random random = new Random();
		
		TSPPopulation tournamentSelectionPool = new TSPPopulation(this.tournamentSize);
		
		for (int i = 0; i < tournamentSize; i++) {
			
			int randomIndex = random.nextInt(currentPopulation.getPopulatationSize());
			
			tournamentSelectionPool.addRoute(currentPopulation.getChromosomeCandidate(randomIndex));
			
		}
		
		return (TSPRoute) tournamentSelectionPool.getFittestCandidate();
		
	}

	

}
