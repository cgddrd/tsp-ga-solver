package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class TSPAlgorithm {

	private double mutationRate;
	private double crossoverRate;
	private int tournamentSize;
	private boolean useSingleChild;
	private boolean useElitism;
	private String selectionMethodType;
	
	public TSPAlgorithm(double mutationRate, double crossoverRate, String selectionMethodType) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.selectionMethodType = selectionMethodType.toLowerCase();
		this.useSingleChild = false;
		this.useElitism = false;
	}

	public TSPAlgorithm(double mutationRate, double crossoverRate, String selectionMethodType, int tournamentSize) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.selectionMethodType = selectionMethodType;
		this.tournamentSize = tournamentSize;
		this.useSingleChild = false;
		this.useElitism = false;
	}

	public TSPAlgorithm(double mutationRate, double crossoverRate, String selectionMethodType, int tournamentSize, boolean useSingleChild, boolean useElitism) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.selectionMethodType = selectionMethodType;
		this.tournamentSize = tournamentSize;
		this.useSingleChild = useSingleChild;
		this.useElitism = useElitism;
	}
	
	// Modified from original source - https://gist.github.com/anonymous/5233837
	// Based on pseudocode sourced - https://en.wikipedia.org/wiki/Stochastic_universal_sampling
	public ArrayList<TSPRoute> performStochasticSamplingSelection(TSPPopulation currentPopulation, int noOfIndividuals) throws Exception {
		
		// Get total fitness for population.
		Double currentFitness = currentPopulation.getPopulationFitnessSum();
		
		// Calculate what the distance should be between the pointers. 
		Double pointerDistance = currentFitness / noOfIndividuals;
		
		// Determine our random starting position between the bounds of the pointer separation distance.
		// Equivalent to spinning the equally spaced marks on top of the roulette wheel once. - See: http://www.fernandolobo.info/ec1516/lectures/GAs-2.pdf for more information.
		double startLoc = ThreadLocalRandom.current().nextDouble(pointerDistance);
		
		ArrayList<TSPRoute> selectedRoutes = new ArrayList<>(noOfIndividuals);
		
		int index = 0;
		
		TSPRoute currentSelectedRoute = currentPopulation.getRouteAtIndex(index); 
		
		double partialSum = currentSelectedRoute.getFitness();
		
		for (int i = 0; i < noOfIndividuals; i++) {
			
			double pointer = (startLoc + i) * pointerDistance;
		     
			if (partialSum >= pointer) {
				
				selectedRoutes.add(currentSelectedRoute);
				
			} else {
				
				
				for (++index; index < currentPopulation.getPopulationSize(); index++) {
					
					currentSelectedRoute = currentPopulation.getRouteAtIndex(index);
					partialSum += currentSelectedRoute.getFitness();
					
					if(partialSum >= pointer) {
						
						selectedRoutes.add(currentSelectedRoute);
						break;
						
					}
				}
			}
			
			//currentSelectedRoute = null;
		}
		
		
		
//		double[] pointers = new double[noOfIndividuals];
//				
//		for (int i = 0; i < noOfIndividuals; i++) {
//			
//			double pointer = startLoc + i * pointerDistance;
//			
//			pointers[i] = pointer;
//			
//		}
//		
//		selectedRoutes = test1(currentPopulation, pointers);
		
		if (selectedRoutes.isEmpty()) {
			throw new Exception("Something went very wrong here. - No route has been selected!");
		}
		
		return selectedRoutes;
	}
	
	public ArrayList<TSPRoute> test1 (TSPPopulation currentPopulation, double[] points) {
		
		ArrayList<TSPRoute> keep = new ArrayList<>();
		
		double partialSum = 0.0;
		int index = 0;
		
		for (double p : points)
		{
			
			while (partialSum < p) {
				partialSum += currentPopulation.getRouteAtIndex(index).getFitness();
				index++;
			}
			
			//This shouldn't be index-1??
			keep.add(currentPopulation.getRouteAtIndex(index-1));
			
		}
		
		return keep;
		
	}

	public TSPRoute performTournamentSelection(TSPPopulation currentPopulation) {

		Random random = new Random();

		TSPPopulation tournamentSelectionPool = new TSPPopulation(this.tournamentSize);

		for (int i = 0; i < tournamentSize; i++) {

			int randomIndex = random.nextInt(currentPopulation.getPopulationSize());

			tournamentSelectionPool.addRoute(currentPopulation.getRouteAtIndex(randomIndex));

		}

		return (TSPRoute) tournamentSelectionPool.getFittestCandidate();

	}
	
	public TSPRoute performRouletteWheelSelection(TSPPopulation currentPopulation) throws Exception {
		
		Random random = new Random();
		
		double populationFitnessSum = currentPopulation.getPopulationFitnessSum();
		
		double randomPoint = ThreadLocalRandom.current().nextDouble(populationFitnessSum);
		
		double partialSum = 0.0;
		
		TSPRoute currentSelectedRoute = null;
		
		for (int i = 0; (i < currentPopulation.getPopulationSize() && partialSum < randomPoint); i++) {
			
			currentSelectedRoute = currentPopulation.getRouteAtIndex(i);
			partialSum += currentSelectedRoute.getFitness();
			
		}
		
		if (currentSelectedRoute == null) {
			throw new Exception("Something went very wrong here. - No route has been selected!");
		}
		
		return currentSelectedRoute;
		
	}

	public TSPPopulation evolvePopulation2(TSPPopulation currentPopulation) throws Exception {

		TSPPopulation newPopulation = new TSPPopulation(currentPopulation.getPopulationSize());
		TSPPopulation tempPopulation = new TSPPopulation(currentPopulation.getRoutes());
		
		this.useElitism = false;
		
		// If we are using elitism, make sure we definitely copy over the best chromosome into the new population.
		if (this.useElitism) {

			newPopulation.addRoute((TSPRoute) tempPopulation.getFittestCandidate());
			
			tempPopulation.removeChromosome(tempPopulation.getFittestCandidate());
			
			newPopulation.addRoute((TSPRoute) tempPopulation.getFittestCandidate());
			
		}
		
		// Prepare for SUS selection.
		ArrayList<TSPRoute> stochasticSelectedRoutes = null;
		int selectionIncrement = 0;
		
		while (newPopulation.getPopulationSize() < currentPopulation.getPopulationSize()) {
			
			TSPRoute parent1 = null;
			TSPRoute parent2 = null;
			
			switch(this.selectionMethodType) {
				
				case "tournament":
				case "ts":
					
					if (this.tournamentSize < 1) {
						tournamentSize = 50;
						System.out.println("INFO: Tournament selection active with no specified tournament size. Defaulting to size: 50.");
						//throw new Exception("Tournament selection active, but tournament size is < 1. Aborting.");
					}
					
					parent1 = this.performTournamentSelection(currentPopulation);
					parent2 = this.performTournamentSelection(currentPopulation);

					break;
				case "sus":
				case "stochastic_uniform_sampling":
					
					// Check if we are yet to initialize the SUS selection (which should be selected all in one go prior to evolving the population)
					if (stochasticSelectedRoutes == null) {
						stochasticSelectedRoutes = performStochasticSamplingSelection(currentPopulation, (currentPopulation.getPopulationSize() - newPopulation.getPopulationSize()));
					}
					
					parent1 = stochasticSelectedRoutes.get(selectionIncrement++);
					parent2 = stochasticSelectedRoutes.get(selectionIncrement++);
					
					break;
				case "rws":
				case "roulette":
				case "roulette_wheel":
					
					parent1 = this.performRouletteWheelSelection(currentPopulation);
					parent2 = this.performRouletteWheelSelection(currentPopulation);
					
					break;
				default:
					throw new Exception("Specified selection method '" + this.selectionMethodType + "' not valid. Unable to perform GA selection. Aborting.");
				
			}

			newPopulation.addRoutes(this.performOrderOneCrossover2(parent1, parent2, this.useSingleChild));

		}

		for (int i = 0; i < newPopulation.getPopulationSize(); i++) {
			this.performSwapMutation(newPopulation.getRouteAtIndex(i));
		}

		return newPopulation;

	}

	public ArrayList<TSPRoute> performOrderOneCrossover2(TSPRoute parent1, TSPRoute parent2, boolean singleChildOnly)
			throws Exception {

		Random random = new Random();

		ArrayList<TSPRoute> routes = new ArrayList<>();
		
		if (random.nextDouble() <= this.crossoverRate) {

			if (parent1.getSize() == parent2.getSize()) {

				ArrayList<TSPLocation> child1Chromosome = new ArrayList<TSPLocation>();
				ArrayList<TSPLocation> child2Chromosome = new ArrayList<TSPLocation>();

				int randomSelectionIndex1 = random.nextInt(parent1.getSize());
				int randomSelectionIndex2 = random.nextInt(parent1.getSize());

				// Make sure both random indexes are not the same
				// (at the same time we also ensure that the largest possible
				// index
				// cannot be used as the start index - Will always be larger
				// than
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

			} else {
				// If the sizes of both parents do not match, then we have a problem.
				throw new Exception("Size mismatch between parent chromosomes.");
			}

		} else {
			
			// If we get to this point, we have determined that we shouldn't be performing crossover on this iteration.
			// Therefore, simply return the two original parents unchanged.
			routes.add(parent1);
			routes.add(parent2);

		}

		return routes;
	}
	
	public ArrayList<TSPRoute> performCycleCrossover(TSPRoute parent1, TSPRoute parent2) throws Exception {
		
		if (parent1.getSize() == parent2.getSize()) {
			
			int AlleleCount = parent1.getRouteSize();
			
			boolean[] flags = new boolean[AlleleCount];
			
			Double_Index_Value_Pair TempPair = new Double_Index_Value_Pair();
			
			// Generate a hashtable for parent 2's index.
			Hashtable HT1 = new Hashtable();
			for (int i = 0; i < AlleleCount; i++) {
				
				TempPair = new Double_Index_Value_Pair();
				TempPair.value2 = parent2.getLocationAtPosition(i);
				TempPair.index2 = i;
				TempPair.value1 = parent1.getLocationAtPosition(i);
				HT1.put(parent2.getLocationAtPosition(i), TempPair);
				
			}
			
			ArrayList<ArrayList <Double_Index_Value_Pair>> cycles = new ArrayList<>();
			
			int cycleStart = 0;
			
			for (int i = 0; i < AlleleCount; i++) {
				
				ArrayList<Double_Index_Value_Pair> tempCycle = new ArrayList<>();
				
				if (!flags[i]) {
					
					cycleStart = i;
					
					TempPair = (Double_Index_Value_Pair) HT1.get(parent1.getLocationAtPosition(i));
					tempCycle.add(TempPair);
					
					flags[TempPair.index2] = true;
					
					while(TempPair.index2 != cycleStart) {
						
						TempPair = (Double_Index_Value_Pair) HT1.get(parent1.getLocationAtPosition(TempPair.index2));
						tempCycle.add(TempPair);
						flags[TempPair.index2] = true;
						
					}
					
					cycles.add(tempCycle);
				}
				
			}
			
			// 3. Copy alternate cycles to children
			
			TSPRoute child1 = new TSPRoute();
			TSPRoute child2 = new TSPRoute();
			
			int counter = 0;
			
			for(ArrayList<Double_Index_Value_Pair> c : cycles) {
				
				for (Double_Index_Value_Pair tempPair : c) {
					
					if (counter % 2 == 0) {
						
						child1.setLocationAtPosition(tempPair.index2, tempPair.value1);
						child2.setLocationAtPosition(tempPair.index2, tempPair.value2);
						
					} else {
						
						child1.setLocationAtPosition(tempPair.index2, tempPair.value2);
						child2.setLocationAtPosition(tempPair.index2, tempPair.value1);
						
					}
				}
				
				counter++;
			}
			
			
			ArrayList<TSPRoute> children = new ArrayList<>();
			children.add(child2);
			children.add(child1);
			
			
			return children;
			
		}
		
		throw new Exception("oh dear");

	}
	
	public class Double_Index_Value_Pair {
		
		private TSPLocation value2;
		
		public TSPLocation getValue2() {
			return value2;
		}

		public void setValue2(TSPLocation value2) {
			this.value2 = value2;
		}

		public int getIndex2() {
			return index2;
		}

		public void setIndex2(int index2) {
			this.index2 = index2;
		}

		public TSPLocation getValue1() {
			return value1;
		}

		public void setValue1(TSPLocation value1) {
			this.value1 = value1;
		}

		private int index2;
		private TSPLocation value1;
		
		public Double_Index_Value_Pair() {
			
		}
	}

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

		if (random.nextDouble() <= this.mutationRate) {

			int swapIndex1 = random.nextInt(chromosome.getSize());
			int swapIndex2 = random.nextInt(chromosome.getSize());
			
			// Make sure the random indexes are not the same.
			while(swapIndex1 == swapIndex2) {
				swapIndex2 = random.nextInt(chromosome.getSize());
			}

			TSPLocation test1 = chromosome.getRouteLocations().get(swapIndex1);
			TSPLocation test2 = chromosome.getRouteLocations().get(swapIndex2);

			chromosome.getRouteLocations().set(swapIndex1, test2);
			chromosome.getRouteLocations().set(swapIndex2, test1);

		}

	}

}
