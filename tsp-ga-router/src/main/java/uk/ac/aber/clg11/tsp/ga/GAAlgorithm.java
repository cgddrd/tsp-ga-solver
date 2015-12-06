package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Abstract class providing implementations for common GA operators (swap mutation, order-one crossover, cycle crossover, tournament selection, RWS selection and SUS selection).
 * 
 * @author Connor Goddard (clg11@aber.ac.uk)
 */
public abstract class GAAlgorithm {
	
	protected double mutationRate;
	protected double crossoverRate;
	protected int tournamentSize;
	
	public GAAlgorithm(int mutationRate, int crossoverRate, int tournamentSize) {
		
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.tournamentSize = tournamentSize;
		
	}
	
	// We are passing by value, but NOT making a copy, so if we change the parameter object, it will change the original object also.
	// See: http://stackoverflow.com/a/12429953 for more information.
	public void performSwapMutation(GAChromosome<Number, GAGene> chromosome) {
		
		// We use a swap mutation for PERUMUTATION REPRESENTATIONS.
		
		Random random = new Random();
		
		if (mutationRate >= random.nextDouble()) {
			
			int swapIndex1 = random.nextInt(chromosome.getSize());
			int swapIndex2 = random.nextInt(chromosome.getSize());
			
			GAGene test1 = chromosome.getGenes().get(swapIndex1);
			GAGene test2 = chromosome.getGenes().get(swapIndex2);
			
			chromosome.getGenes().set(swapIndex1, test2);
			chromosome.getGenes().set(swapIndex2, test1);
			
		}

	}
		
	public ArrayList<GAGene> performOrderOneCrossover(GAChromosome parent1, GAChromosome parent2) throws Exception {
		
		Random random = new Random();
		
		// We need to check both parent chromosomes have the same number of genes.
		if ((crossoverRate >= random.nextDouble()) && (parent1.getSize() == parent2.getSize())) {
			
			//ArrayList<GAGene> child = new ArrayList<GAGene>(Arrays.asList(new GAGene[parent1.getSize()]));
			
			ArrayList<GAGene> childChromosome = new ArrayList<GAGene>();
			
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
			
			ArrayList<GAGene> parent1SubCollection = (ArrayList<GAGene>) parent1.getGenes().subList(crossoverSelectionStartIndex, crossoverSelectionStartIndex);
			
			childChromosome.addAll(parent1SubCollection);
			
			for (int i = 0; i < parent2.getSize(); i++) {
				
				int currentIndex = (crossoverSelectionEndIndex + i) % parent2.getSize();
				
				GAGene currentGene = (GAGene) parent2.getGenes().get(currentIndex);
				
				if (!childChromosome.contains(currentGene)) {
					
					childChromosome.add(currentGene);
				}
			
			}
			
			Collections.rotate(childChromosome, crossoverSelectionStartIndex);
			
			return childChromosome;
				
		}
		
		throw new Exception("Size mismatch between parent chromosomes.");
		
	
	}
	
}
