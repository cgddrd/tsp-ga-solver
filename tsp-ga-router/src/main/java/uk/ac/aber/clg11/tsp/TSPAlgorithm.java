package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TSPAlgorithm {

	private double mutationRate;
	private double crossoverRate;
	private int tournamentSize;
	private boolean useSingleChild;
	
	public TSPAlgorithm(double mutationRate, double crossoverRate, int tournamentSize) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.tournamentSize = tournamentSize;
		this.useSingleChild = false;
	}

	public TSPAlgorithm(double mutationRate, double crossoverRate, int tournamentSize, boolean useSingleChild) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.tournamentSize = tournamentSize;
		this.useSingleChild = useSingleChild;
	}

	// public TSPRoute performTournamentSelection(ArrayList<TSPRoute>
	// currentRoutes) {
	//
	// Random random = new Random();
	//
	// TSPPopulation tournamentSelectionPool = new
	// TSPPopulation(this.tournamentSize);
	//
	// for (int i = 0; i < tournamentSize; i++) {
	//
	// int randomIndex = random.nextInt(currentRoutes.size());
	//
	// tournamentSelectionPool.addRoute(currentRoutes.get(randomIndex));
	//
	// }
	//
	// return (TSPRoute) tournamentSelectionPool.getFittestCandidate();
	//
	// }

	public TSPRoute performTournamentSelection(TSPPopulation currentPopulation) {

		Random random = new Random();

		TSPPopulation tournamentSelectionPool = new TSPPopulation(this.tournamentSize);

		for (int i = 0; i < tournamentSize; i++) {

			int randomIndex = random.nextInt(currentPopulation.getPopulationSize());

			tournamentSelectionPool.addRoute(currentPopulation.getChromosomeCandidate(randomIndex));

		}

		return (TSPRoute) tournamentSelectionPool.getFittestCandidate();

	}


	public TSPPopulation evolvePopulation(TSPPopulation currentPopulation) throws Exception {

		TSPPopulation newPopulation = new TSPPopulation(currentPopulation.getPopulationSize());

		for (int i = 0; i < currentPopulation.getPopulationSize(); i++) {

			TSPRoute parent1 = this.performTournamentSelection(currentPopulation);
			TSPRoute parent2 = this.performTournamentSelection(currentPopulation);

			ArrayList<TSPLocation> crossoverResult = this.performOrderOneCrossover(parent1, parent2);

			TSPRoute child = new TSPRoute(crossoverResult);

			newPopulation.addRoute(child);

		}

		for (int i = 0; i < newPopulation.getPopulationSize(); i++) {
			this.performSwapMutation(newPopulation.getChromosomeCandidate(i));
		}

		return newPopulation;

	}
	
	public TSPPopulation evolvePopulation2(TSPPopulation currentPopulation) throws Exception {

		TSPPopulation newPopulation = new TSPPopulation(currentPopulation.getPopulationSize());
		
		while (newPopulation.getPopulationSize() < currentPopulation.getPopulationSize()) {
			
			TSPRoute parent1 = this.performTournamentSelection(currentPopulation);
			TSPRoute parent2 = this.performTournamentSelection(currentPopulation);
			
			newPopulation.addRoutes(this.performOrderOneCrossover2(parent1, parent2, this.useSingleChild)); 
			
		}

		for (int i = 0; i < newPopulation.getPopulationSize(); i++) {
			this.performSwapMutation(newPopulation.getChromosomeCandidate(i));
		}

		return newPopulation;

	}

	public ArrayList<TSPRoute> performOrderOneCrossover2(TSPRoute parent1, TSPRoute parent2, boolean singleChildOnly) throws Exception {

		Random random = new Random();
		
		ArrayList<TSPRoute> routes = new ArrayList<>();

		if (parent1.getSize() == parent2.getSize()) {

			ArrayList<TSPLocation> child1Chromosome = new ArrayList<TSPLocation>();
			ArrayList<TSPLocation> child2Chromosome = new ArrayList<TSPLocation>();

			int randomSelectionIndex1 = random.nextInt(parent1.getSize());
			int randomSelectionIndex2 = random.nextInt(parent1.getSize());

			// Make sure both random indexes are not the same
			// (at the same time we also ensure that the largest possible index
			// cannot be used as the start index - Will always be larger than
			// the other ransdomly selected value.)
			// Could also achieve this by using the code here:
			// http://stackoverflow.com/a/11784059
			while (randomSelectionIndex1 == randomSelectionIndex2) {
				randomSelectionIndex2 = random.nextInt(parent1.getSize());
			}

			int crossoverSelectionStartIndex = Math.min(randomSelectionIndex1, randomSelectionIndex2);
			int crossoverSelectionEndIndex = Math.max(randomSelectionIndex1, randomSelectionIndex2);

			ArrayList<TSPLocation> parent1SubCollection = new ArrayList<TSPLocation>(
					parent1.getRouteLocations().subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));
			
			ArrayList<TSPLocation> parent2SubCollection = new ArrayList<TSPLocation>(
					parent2.getRouteLocations().subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));

			child1Chromosome.addAll(parent1SubCollection);
			child2Chromosome.addAll(parent2SubCollection);

			for (int i = 0; i < parent2.getSize(); i++) {

				int currentIndex = (crossoverSelectionEndIndex + i) % parent2.getSize();

				TSPLocation currentGene1 = (TSPLocation) parent2.getRouteLocations().get(currentIndex);
				TSPLocation currentGene2 = (TSPLocation) parent1.getRouteLocations().get(currentIndex);

				if (!child1Chromosome.contains(currentGene1)) {
					child1Chromosome.add(currentGene1);
				}
				
				if (!child2Chromosome.contains(currentGene2)) {
					child2Chromosome.add(currentGene2);
				}

			}

			Collections.rotate(child1Chromosome, crossoverSelectionStartIndex);
			Collections.rotate(child2Chromosome, crossoverSelectionStartIndex);
			
			routes.add(new TSPRoute(child1Chromosome));
			
			if (!singleChildOnly) {
				routes.add(new TSPRoute(child2Chromosome));
			}
			
			
			return routes;

		}

		throw new Exception("Size mismatch between parent chromosomes.");

	}

//	public void performOrderOneCrossover2(TSPRoute parent1, TSPRoute parent2) throws Exception {
//
//		Random random = new Random();
//
//		if (parent1.getSize() == parent2.getSize()) {
//
//			ArrayList<TSPLocation> child1Chromosome = new ArrayList<TSPLocation>();
//			ArrayList<TSPLocation> child2Chromosome = new ArrayList<TSPLocation>();
//
//			// TSPRoute child1 = new TSPRoute();
//			// TSPRoute child2 = new TSPRoute();
//
//			ArrayList<TSPLocation> parent1Locations = (ArrayList<TSPLocation>) parent1.getRouteLocations().clone();
//			ArrayList<TSPLocation> parent2Locations = (ArrayList<TSPLocation>) parent1.getRouteLocations().clone();
//
//			int randomSelectionIndex1 = random.nextInt(parent1.getSize());
//			int randomSelectionIndex2 = random.nextInt(parent1.getSize());
//
//			// Make sure both random indexes are not the same
//			// (at the same time we also ensure that the largest possible index
//			// cannot be used as the start index - Will always be larger than
//			// the other ransdomly selected value.)
//			// Could also achieve this by using the code here:
//			// http://stackoverflow.com/a/11784059
//			while (randomSelectionIndex1 == randomSelectionIndex2) {
//				randomSelectionIndex2 = random.nextInt(parent1.getSize());
//			}
//
//			int crossoverSelectionStartIndex = Math.min(randomSelectionIndex1, randomSelectionIndex2);
//			int crossoverSelectionEndIndex = Math.max(randomSelectionIndex1, randomSelectionIndex2);
//
//			ArrayList<TSPLocation> parent1SubCollection = new ArrayList<TSPLocation>(
//					parent1Locations.subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));
//			ArrayList<TSPLocation> parent2SubCollection = new ArrayList<TSPLocation>(
//					parent2Locations.subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));
//
//			child1Chromosome.addAll(parent1SubCollection);
//			child2Chromosome.addAll(parent2SubCollection);
//
//			for (int i = 0; i < parent2.getSize(); i++) {
//
//				int currentIndex = (crossoverSelectionEndIndex + i) % parent2.getSize();
//
//				TSPLocation currentGene1 = (TSPLocation) parent2.getRouteLocations().get(currentIndex);
//				TSPLocation currentGene2 = (TSPLocation) parent1.getRouteLocations().get(currentIndex);
//
//				if (!child1Chromosome.contains(currentGene1)) {
//					child1Chromosome.add(currentGene1);
//				}
//
//				if (!child2Chromosome.contains(currentGene2)) {
//					child2Chromosome.add(currentGene2);
//				}
//
//			}
//
//			Collections.rotate(child1Chromosome, crossoverSelectionStartIndex);
//			Collections.rotate(child2Chromosome, crossoverSelectionStartIndex);
//
//			if (child1Chromosome.size() == child2Chromosome.size()) {
//
//				 Collections.copy(parent1.getRouteLocations(),
//				 child1Chromosome);
//				Collections.copy(parent2.getRouteLocations(),
//				 child2Chromosome);
//
//			} else {
//
//				System.out.println("FUCK");
//
//			}
//
//		} else {
//			throw new Exception("Size mismatch between parent chromosomes.");
//		}
//
//	}

	public ArrayList<TSPLocation> performOrderOneCrossover(TSPRoute parent1, TSPRoute parent2) throws Exception {

		Random random = new Random();

		if (parent1.getSize() == parent2.getSize()) {

			// ArrayList<GAGene> child = new ArrayList<GAGene>(Arrays.asList(new
			// GAGene[parent1.getSize()]));

			ArrayList<TSPLocation> childChromosome = new ArrayList<TSPLocation>();

			int randomSelectionIndex1 = random.nextInt(parent1.getSize());
			int randomSelectionIndex2 = random.nextInt(parent1.getSize());

			// Make sure both random indexes are not the same
			// (at the same time we also ensure that the largest possible index
			// cannot be used as the start index - Will always be larger than
			// the other randomly selected value.)
			// Could also achieve this by using the code here:
			// http://stackoverflow.com/a/11784059
			while (randomSelectionIndex1 == randomSelectionIndex2) {
				randomSelectionIndex2 = random.nextInt(parent1.getSize());
			}

			int crossoverSelectionStartIndex = Math.min(randomSelectionIndex1, randomSelectionIndex2);
			int crossoverSelectionEndIndex = Math.max(randomSelectionIndex1, randomSelectionIndex2);

			ArrayList<TSPLocation> parent1SubCollection = new ArrayList<TSPLocation>(
					parent1.getRouteLocations().subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));

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

	// We are passing by value, but NOT making a copy, so if we change the
	// parameter object, it will change the original object also.
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
