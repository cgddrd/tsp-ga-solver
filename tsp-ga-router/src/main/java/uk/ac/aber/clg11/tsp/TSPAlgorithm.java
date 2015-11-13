package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TSPAlgorithm {
	
	private double mutationRate;
	private double crossoverRate;
	private int tournamentSize;

	public TSPAlgorithm(double mutationRate, double crossoverRate, int tournamentSize) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.tournamentSize = tournamentSize;
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

	public TSPPopulation evolvePopulation(TSPPopulation currentPopulation) throws Exception {
		
		TSPPopulation newPopulation = new TSPPopulation(currentPopulation.getPopulatationSize());
		
		for (int i = 0; i < currentPopulation.getPopulatationSize(); i++) {
			
			TSPRoute parent1 = this.performTournamentSelection(currentPopulation);
			TSPRoute parent2 = this.performTournamentSelection(currentPopulation);
			
			ArrayList<TSPLocation> name = this.performOrderOneCrossover(parent1, parent2);
					
			TSPRoute child = new TSPRoute(name);
			
			newPopulation.addRoute(child);
			
		}
		
	   for (int i = 0; i < newPopulation.getPopulatationSize(); i++) {
		   this.performSwapMutation(newPopulation.getChromosomeCandidate(i));
	   }
	   
	   return newPopulation;
	   
	}
	
	public ArrayList<TSPLocation> performOrderOneCrossover(TSPRoute parent1, TSPRoute parent2) throws Exception {
		
		Random random = new Random();
		
		// We need to check both parent chromosomes have the same number of genes.
		//if ((crossoverRate >= random.nextDouble()) && (parent1.getSize() == parent2.getSize())) {
		
		if (parent1.getSize() == parent2.getSize()) {
			
			//ArrayList<GAGene> child = new ArrayList<GAGene>(Arrays.asList(new GAGene[parent1.getSize()]));
			
			ArrayList<TSPLocation> childChromosome = new ArrayList<TSPLocation>();
			
			int randomSelectionIndex1 = random.nextInt(parent1.getSize());
			int randomSelectionIndex2 = random.nextInt(parent1.getSize());
			
			// Make sure both random indexes are not the same 
			// (at the same time we also ensure that the largest possible index cannot be used as the start index - Will always be larger than the other ransdomly selected value.)
			// Could also achieve this by using the code here: http://stackoverflow.com/a/11784059
			while (randomSelectionIndex1 == randomSelectionIndex2) {
				randomSelectionIndex2 = random.nextInt(parent1.getSize());
			}
			
			int crossoverSelectionStartIndex = Math.min(randomSelectionIndex1, randomSelectionIndex2);
			int crossoverSelectionEndIndex = Math.max(randomSelectionIndex1, randomSelectionIndex2);
			
			ArrayList<TSPLocation> parent1SubCollection = new ArrayList<TSPLocation>(parent1.getRouteLocations().subList(crossoverSelectionStartIndex, crossoverSelectionStartIndex));
			
			childChromosome.addAll(parent1SubCollection);
			
			for (int i = 0; i < parent2.getSize(); i++) {
				
				int currentIndex = (crossoverSelectionEndIndex + i) % parent2.getSize();
				
				TSPLocation currentGene = (TSPLocation) parent2.getRouteLocations().get(currentIndex);
				
				if (!childChromosome.contains(currentGene)) {
					
					childChromosome.add(currentGene);
				}
			
			}
			
			Collections.rotate(childChromosome, crossoverSelectionStartIndex);
			
			return childChromosome;
				
		}
		
		throw new Exception("Size mismatch between parent chromosomes.");
		
	
	}

	//We are passing by value, but NOT making a copy, so if we change the parameter object, it will change the original object also.
	// See: http://stackoverflow.com/a/12429953 for more information.
	public void performSwapMutation(TSPRoute chromosome) {
		
		// We use a swap mutation for PERUMUTATION REPRESENTATIONS.
		
		Random random = new Random();
		
		if (mutationRate >= random.nextDouble()) {
			
			int swapIndex1 = random.nextInt(chromosome.getSize());
			int swapIndex2 = random.nextInt(chromosome.getSize());
			
			TSPLocation test1 = chromosome.getRouteLocations().get(swapIndex1);
			TSPLocation test2 = chromosome.getRouteLocations().get(swapIndex2);
			
			chromosome.getRouteLocations().set(swapIndex1, test2);
			chromosome.getRouteLocations().set(swapIndex2, test1);
			
		}

	}

}
