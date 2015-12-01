package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import uk.ac.aber.clg11.tsp.TSPAlgorithm.CycleCrossoverItem;
import uk.ac.aber.clg11.tsp.ga.GAGene;

public class TSPAlgorithm {

	private double mutationRate;
	private double crossoverRate;
	private int tournamentSize;
	private boolean useSingleChild;
	private boolean useElitism;
	private String selectionMethodType;
	private String crossoverMethodType;

	public TSPAlgorithm(double mutationRate, double crossoverRate, String selectionMethodType, String crossoverMethodType) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.selectionMethodType = selectionMethodType.toLowerCase();
		this.crossoverMethodType = crossoverMethodType.toLowerCase();
		this.useSingleChild = false;
		this.useElitism = false;
	}

	public TSPAlgorithm(double mutationRate, double crossoverRate, String selectionMethodType, String crossoverMethodType, int tournamentSize) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.selectionMethodType = selectionMethodType.toLowerCase();
		this.crossoverMethodType = crossoverMethodType.toLowerCase();
		this.tournamentSize = tournamentSize;
		this.useSingleChild = false;
		this.useElitism = false;
	}

	public TSPAlgorithm(double mutationRate, double crossoverRate, String selectionMethodType, String crossoverMethodType, int tournamentSize, boolean useSingleChild, boolean useElitism) {
		this.mutationRate = mutationRate;
		this.crossoverRate = crossoverRate;
		this.selectionMethodType = selectionMethodType.toLowerCase();
		this.crossoverMethodType = crossoverMethodType.toLowerCase();
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

	public TSPPopulation evolvePopulation(TSPPopulation currentPopulation) throws Exception {

		TSPPopulation newPopulation = new TSPPopulation(currentPopulation.getPopulationSize());
		TSPPopulation tempPopulation = new TSPPopulation(currentPopulation.getRoutes());

		// Prepare for SUS selection.
		ArrayList<TSPRoute> stochasticSelectedRoutes = null;
		ArrayList<TSPRoute> tournamentSelectedRoutes = null;
		int selectionIncrement = 0;

		Random random = new Random();

		//int targetPopSize = this.useElitism ? (currentPopulation.getPopulationSize() - 2) : currentPopulation.getPopulationSize();
		
		int elitismOffset = this.useElitism ? 1 : 0;
		
		this.useSingleChild = true;
		
		//while (newPopulation.getPopulationSize() < targetPopSize) {
		
		if (this.useElitism) {
			newPopulation.addRoute(currentPopulation.getFittestCandidate());
		}
		
		for (int i = elitismOffset; i < currentPopulation.getPopulationSize(); i++) {

			TSPRoute parent1 = null;
			TSPRoute parent2 = null;

			switch(this.selectionMethodType) {

			case "tournament":
			case "ts":
				
				//System.out.println("tournament selection");
				
				if (this.tournamentSize < 1) {
					tournamentSize = 50;
					System.out.println("INFO: Tournament selection active with no specified tournament size. Defaulting to size: 50.");
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


				switch (this.crossoverMethodType) {

				case "ordered":
				case "orderone":
				case "order-one":
				case "ox1":
					//newPopulation.addRoutes(this.performOrderOneCrossover(parent1, parent2, this.useSingleChild));
					
					newPopulation.addRoute(this.crossover(parent1, parent2));
					break;

				case "cycle":
				case "cyclecrossover":
					newPopulation.addRoutes(this.performCycleCrossover(parent1, parent2));
					break;
				default:
					throw new Exception("Specified crossover method '" + this.crossoverMethodType + "' not valid. Unable to perform GA crossover. Aborting.");
				}


		}

		for (int i = 0; i < newPopulation.getPopulationSize(); i++) {

			this.performSwapMutation(newPopulation.getRouteAtIndex(i));

		}

		// If we are using elitism, make sure we definitely copy over the best chromosome into the new population.
//		if (this.useElitism) {
//
//			newPopulation.addRoute((TSPRoute) currentPopulation.getFittestCandidate());
//
//			tempPopulation.removeChromosome(tempPopulation.getFittestCandidate());
//
//		    newPopulation.addRoute((TSPRoute) tempPopulation.getFittestCandidate());
//
//		}

		return newPopulation;

	}
	
	// Applies crossover to a set of parents and creates offspring
    public TSPRoute crossover(TSPRoute parent1, TSPRoute parent2) {
        // Create new child tour
        TSPRoute child = new TSPRoute(parent1.getRouteSize());

        // Get start and end sub tour positions for parent1's tour
        int startPos = (int) (Math.random() * parent1.getRouteSize());
        int endPos = (int) (Math.random() * parent1.getRouteSize());

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < parent1.getRouteSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setLocationAtPosition(i, parent1.getLocationAtPosition(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                	child.setLocationAtPosition(i, parent1.getLocationAtPosition(i));
                }
            }
        }

        // Loop through parent2's city tour
        for (int i = 0; i < parent2.getRouteSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsLocation(parent2.getLocationAtPosition(i))) {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < child.getRouteSize(); ii++) {
                    // Spare position found, add city
                    if (child.getLocationAtPosition(ii) == null) {
                        child.setLocationAtPosition(ii, parent2.getLocationAtPosition(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

	public ArrayList<TSPRoute> performOrderOneCrossover(TSPRoute parent1, TSPRoute parent2, boolean singleChildOnly)
			throws Exception {

		Random random = new Random();

		ArrayList<TSPRoute> routes = new ArrayList<>();

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

				TSPLocation currentGene1 = parent2.getRouteLocations().get(currentIndex);
				TSPLocation currentGene2 = parent1.getRouteLocations().get(currentIndex);

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

		return routes;
	}

	public ArrayList<TSPRoute> performCycleCrossover(TSPRoute parent1, TSPRoute parent2) throws Exception {

		if (parent1.getSize() == parent2.getSize()) {

			boolean[] flags = new boolean[parent1.getSize()];

			CycleCrossoverItem<TSPLocation> currentCycleItem = new CycleCrossoverItem<TSPLocation>();

			// 1. Generate a hashtable for parent 1's index.
			Hashtable<TSPLocation, CycleCrossoverItem<TSPLocation>> parent1Lookup = new Hashtable();

			for (int i = 0; i < parent1.getSize(); i++) {

				currentCycleItem = new CycleCrossoverItem<TSPLocation>(i, parent1.getLocationAtPosition(i), parent2.getLocationAtPosition(i));
				parent1Lookup.put(parent1.getLocationAtPosition(i), currentCycleItem);

			}

			// 2. Generate the cycles.

			ArrayList<ArrayList <CycleCrossoverItem<TSPLocation>>> cycles = new ArrayList<>();

			int cycleStart = 0;

			for (int i = 0; i < parent1.getSize(); i++) {

				ArrayList<CycleCrossoverItem<TSPLocation>> tempCycle = new ArrayList<>();

				if (!flags[i]) {

					cycleStart = i;

					// Look for the VALUE of the current element from Parent 2 inside Parent 1 via the hashtable lookup.
					currentCycleItem = (CycleCrossoverItem<TSPLocation>) parent1Lookup.get(parent2.getLocationAtPosition(i));

					// Add this to the array of values for this particular cycle, and make sure that we cannot add it to a future cycle.
					tempCycle.add(currentCycleItem);
					flags[currentCycleItem.parent1Index] = true;

					// The current cycle should continue UNTIL we return back to the ORIGINAL INDEX that the cycle started at.
					while(currentCycleItem.parent1Index != cycleStart) {

						// Follow the cycle along to the next element by finding the VALUE of the next element in the chain from Parent 2 inside Parent 1.
						TSPLocation parent2ElementFromParent1Index = parent2.getLocationAtPosition(currentCycleItem.parent1Index);

						// Locate the element with the same value from Parent 2 in Parent 2 (follow the cycle chain..)
						currentCycleItem = (CycleCrossoverItem<TSPLocation>) parent1Lookup.get(parent2ElementFromParent1Index);

						// Add this to the array of values for this particular cycle, and make sure that we cannot add it to a future cycle.
						tempCycle.add(currentCycleItem);
						flags[currentCycleItem.parent1Index] = true;

					}

					cycles.add(tempCycle);
				}

			}

			// 3. Copy alternate cycles to children

			TSPLocation[] child1 = new TSPLocation[parent1.getSize()];
			TSPLocation[] child2 = new TSPLocation[parent1.getSize()];

			int counter = 0;

			for(ArrayList<CycleCrossoverItem<TSPLocation>> c : cycles) {

				for (CycleCrossoverItem<TSPLocation> tempPair : c) {

					child1[tempPair.parent1Index] = (counter % 2 == 0) ? (TSPLocation) tempPair.parent1Value : (TSPLocation) tempPair.parent2Value;
					child2[tempPair.parent1Index] = (counter % 2 == 0) ? (TSPLocation) tempPair.parent2Value : (TSPLocation) tempPair.parent1Value;

				}

				counter++;
			}


			ArrayList<TSPRoute> children = new ArrayList<>();

			children.add(new TSPRoute(child1));
			children.add(new TSPRoute(child2));

			return children;

		}

		throw new Exception("Parent 1 and Parent 2 sizes do not match. Aborting cycle crossover.");

	}

	// We are passing by value, but NOT making a copy, so if we change the
	// parameter object, it will change the original object also.
	// See: http://stackoverflow.com/a/12429953 for more information.
	public void performSwapMutation(TSPRoute chromosome) {

		// We use a swap mutation for PERUMUTATION REPRESENTATIONS.

		Random random = new Random();
		
		for (int i = 0; i < chromosome.getRouteSize(); i++) {
			
			if (random.nextDouble() < this.mutationRate) {
				
				//int swapIndex1 = random.nextInt(chromosome.getSize());
				int swapIndex2 = random.nextInt(chromosome.getSize());

				// Make sure the random indexes are not the same.
				while(i == swapIndex2) {
					swapIndex2 = random.nextInt(chromosome.getSize());
				}

				TSPLocation test1 = chromosome.getLocationAtPosition(i);
				TSPLocation test2 = chromosome.getLocationAtPosition(swapIndex2);

				chromosome.setLocationAtPosition(i, test2);
				chromosome.setLocationAtPosition(swapIndex2, test1);
			}
			
			
		}

	}

	public class CycleCrossoverItem<T extends GAGene>  {

		private GAGene parent1Value;
		private GAGene parent2Value;
		private int parent1Index;

		public CycleCrossoverItem() {

		}

		public CycleCrossoverItem(int parent1Index, GAGene parent1Value, GAGene parent2Value) {

			this.parent1Index = parent1Index;
			this.parent1Value = parent1Value;
			this.parent2Value = parent2Value;

		}

		public GAGene getParent1Value() {
			return parent1Value;
		}

		public void setParent1Value(GAGene parent1Value) {
			this.parent1Value = parent1Value;
		}

		public GAGene getParent2Value() {
			return parent2Value;
		}

		public void setParent2Value(GAGene parent2Value) {
			this.parent2Value = parent2Value;
		}

		public int getParent1Index() {
			return parent1Index;
		}

		public void setParent1Index(int parent1Index) {
			this.parent1Index = parent1Index;
		}
	}

}
