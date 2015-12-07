package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import uk.ac.aber.clg11.tsp.ga.GAGene;

/**
 * Primary GA class providing implementations for common GA operators (swap mutation, order-one crossover, cycle crossover, tournament selection, RWS selection and SUS selection).
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 */
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

	/**
	 * Performs selection of chromosomes from the current population using the 'Tournament' selection operator.
	 * 
	 * @param currentPopulation The population to select from.
	 * @return A chromosome selected from the current population.
	 */
	public TSPRoute performTournamentSelection(TSPPopulation currentPopulation) {

		Random random = new Random();

		TSPPopulation tournamentSelectionPool = new TSPPopulation(this.tournamentSize);
		
		// Randomly select chromosomes from the current population to add to the tournament pool. 
		// Stop when we reach the specified tournament size (allows us to adjust the selective pressure)
		for (int i = 0; i < tournamentSize; i++) {

			int randomIndex = random.nextInt(currentPopulation.getPopulationSize());

			tournamentSelectionPool.addRoute(currentPopulation.getRouteAtIndex(randomIndex));

		}
		
		// Out of the candidates held in the generated tournament pool, select the strongest.
		return (TSPRoute) tournamentSelectionPool.getFittestCandidate();

	}
	
	/**
	 * Performs selection of chromosomes from the current population using the 'Roulette Wheel' selection operator.
	 * 
	 * Based originally upon the algorithm published at: http://www.obitko.com/tutorials/genetic-algorithms/selection.php
	 * 
	 * @param currentPopulation The population to select from.
	 * @param selectionPoint The point along the circumference of the wheel at which to make the selection.
	 * @return A chromosome selected from the current population.
	 * @throws Exception
	 */
	public TSPRoute performRouletteWheelSelection(TSPPopulation currentPopulation, double selectionPoint) {

		double partialSum = 0.0;

		TSPRoute currentSelectedRoute = null;
		
		// Continue moving along the theoretical roulette wheel until we reach the position of the selection point.
		for (int i = 0; (partialSum < selectionPoint) && (i < currentPopulation.getPopulationSize()); i++) {

			currentSelectedRoute = currentPopulation.getRouteAtIndex(i);
			partialSum += currentSelectedRoute.getFitness();

		}
		
		// Return the chromosome located at the selection point.
		return currentSelectedRoute;

	}
	
	/**
	 * Performs selection of chromosomes from the current population using the standard 'Roulette Wheel' selection operator.
	 * 
	 * Based originally upon the algorithm published at: http://www.obitko.com/tutorials/genetic-algorithms/selection.php
	 * 
	 * @param currentPopulation The population to select from.
	 * @return A chromosome selected from the current population.
	 * @throws Exception
	 */
	public TSPRoute performStandardRouletteWheelSelection(TSPPopulation currentPopulation) throws Exception {

		Random random = new Random();

		double populationFitnessSum = currentPopulation.getPopulationFitnessSum();
		
		// Generate a random selection point along the circumference of the theoretical roulette wheel.
		double randomPoint = ThreadLocalRandom.current().nextDouble(populationFitnessSum);
		
		// Perform RWS, returning the chromosome that is at the location of the random point.
		return performRouletteWheelSelection(currentPopulation, randomPoint);

	}
	
	/**
	 * Performs selection of chromosomes from the current population using the standard 'Stochastic Universal Sampling' selection operator.
	 * 
	 * Based on pseudocode sourced from: https://en.wikipedia.org/wiki/Stochastic_universal_sampling 
	 * and modified from original source: https://gist.github.com/anonymous/5233837
	 * 
	 * @param currentPopulation The population to select from.
	 * @param noOfSelectionPoints The number of selection points to calculate across the circumference of the wheel. (Equal to the number of individuals to be selected).
	 * @return A collection of chromosomes pre-selected from the original population at the calculated selection points.
	 * @throws Exception
	 */
	public ArrayList<TSPRoute> performStochasticUniversalSamplingSelection(TSPPopulation currentPopulation, int noOfSelectionPoints) throws Exception {

		// Get total fitness for population.
		Double currentFitness = currentPopulation.getPopulationFitnessSum();

		// Calculate what the distance should be between the selection points. 
		Double pointerDistance = currentFitness / noOfSelectionPoints;

		// Determine our random starting position between the bounds of the pointer separation distance.
		// Equivalent to spinning the equally spaced marks on top of the roulette wheel once.
		// See: http://www.fernandolobo.info/ec1516/lectures/GAs-2.pdf for more information.
		double startLocation = ThreadLocalRandom.current().nextDouble(pointerDistance);

		ArrayList<TSPRoute> selectedRoutes = new ArrayList<>(noOfSelectionPoints);

		for (int i = 0; i < noOfSelectionPoints; i++) {
			
			// Calculate the next selection point along the circumference of the theoretical wheel.
			double selectionPoint = (startLocation + i) * pointerDistance;
			
			// After calculating our selection pointers, we are in effect moving back to performing RWS.
			selectedRoutes.add(performRouletteWheelSelection(currentPopulation, selectionPoint));

		}

		if (selectedRoutes.isEmpty()) {
			throw new Exception("Something went very wrong here. - No route has been selected!");
		}

		return selectedRoutes;
	}

	/**
	 * Performs a single GA evolution iteration (i.e. generation) for the specified population.
	 * @param currentPopulation The population to evolve.
	 * @return The newly-evolved population.
	 * @throws Exception
	 */
	public TSPPopulation evolvePopulation(TSPPopulation currentPopulation) throws Exception {

		// Create a copy of the current population to represent what will become the new population.
		TSPPopulation newPopulation = new TSPPopulation(currentPopulation.getPopulationSize());
			
		// Create another of the current population that will be used to extract the 'elite' parents.
		TSPPopulation tempPopulation = new TSPPopulation(currentPopulation.getRoutes());
		
		
		// Depending on whether elitism is active, we need to decide on the number of individuals to breed/mutate to make up a equally-sized new population.
		int targetNewPopulationSize = this.useElitism ? (currentPopulation.getPopulationSize() - 2) : currentPopulation.getPopulationSize();

		// Prepare for SUS selection.
		ArrayList<TSPRoute> stochasticSelectedRoutes = null;
		int selectionIncrement = 0;
		
		// Perform selection/crossover until we have reached our target population size.
		while (newPopulation.getPopulationSize() < targetNewPopulationSize) {

			TSPRoute parent1 = null;
			TSPRoute parent2 = null;
			
			
			// 1. PERFORM SELECTION.
			switch(this.selectionMethodType) {

			case "tournament":
			case "ts":

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
					stochasticSelectedRoutes = performStochasticUniversalSamplingSelection(currentPopulation, (currentPopulation.getPopulationSize() - newPopulation.getPopulationSize()));
				}

				parent1 = stochasticSelectedRoutes.get(selectionIncrement++);
				parent2 = stochasticSelectedRoutes.get(selectionIncrement++);

				break;
			case "rws":
			case "roulette":
			case "roulette_wheel":

				parent1 = this.performStandardRouletteWheelSelection(currentPopulation);
				parent2 = this.performStandardRouletteWheelSelection(currentPopulation);

				break;
			default:
				throw new Exception("Specified selection method '" + this.selectionMethodType + "' not valid. Unable to perform GA selection. Aborting.");

			}

			// 2. PERFORM CROSSOVER
			switch (this.crossoverMethodType) {

			case "ordered":
			case "orderone":
			case "order-one":
			case "ox1":
				newPopulation.addRoutes(this.performOrderOneCrossover(parent1, parent2, this.useSingleChild));
				break;

			case "cycle":
			case "cyclecrossover":
				newPopulation.addRoutes(this.performCycleCrossover(parent1, parent2));
				break;
			default:
				throw new Exception("Specified crossover method '" + this.crossoverMethodType + "' not valid. Unable to perform GA crossover. Aborting.");
			}

		}
		
		
		// 3. PERFORM MUTATION.
		for (int i = 0; i < newPopulation.getPopulationSize(); i++) {
			this.performSwapMutation(newPopulation.getRouteAtIndex(i));
		}
		
		// 4. PERFORM ELITISM (IF SPECIFIED) - COPIES OVER THE TWO BEST CHROMOSOMES (UNALTERED) FROM THE CURRENT POPULATION.
		if (this.useElitism) {
			
			// Copy the top best chromosome.
			newPopulation.addRoute((TSPRoute) tempPopulation.getFittestCandidate());
			
			// Prevent the current top chromosome from being selected again.
			tempPopulation.removeChromosome(tempPopulation.getFittestCandidate());
			
			// Copy the next best chromosome.
			newPopulation.addRoute((TSPRoute) tempPopulation.getFittestCandidate());

		}

		return newPopulation;

	}

	/**
	 * Performs crossover between two parent chromosomes in order to generate new child chromosomes using the 'Order-One Crossover' operator.
	 * 
	 * See: https://www.youtube.com/watch?v=HATPHZ6P7c4&index=18&list=PLea0WJq13cnARQILcbHUPINYLy1lOSmjH for more information.
	 * 
	 * @param parent1 The first parent chromosome to breed.
	 * @param parent2 The first parent chromosome to breed.
	 * @return Two new child chromosomes consisting of values selected and copied from the two original parent chromosomes.
	 * @throws Exception
	 */
	public ArrayList<TSPRoute> performOrderOneCrossover(TSPRoute parent1, TSPRoute parent2, boolean singleChildOnly)
			throws Exception {

		Random random = new Random();

		ArrayList<TSPRoute> childRoutes = new ArrayList<>();

		if (random.nextDouble() <= this.crossoverRate) {

			if (parent1.getSize() == parent2.getSize()) {

				ArrayList<TSPLocation> child1Chromosome = new ArrayList<TSPLocation>();
				ArrayList<TSPLocation> child2Chromosome = new ArrayList<TSPLocation>();

				int randomSelectionIndex1 = random.nextInt(parent1.getSize());
				int randomSelectionIndex2 = random.nextInt(parent1.getSize());

				// Make sure both random indexes are not the same.
				// (at the same time we also ensure that the largest possible index cannot be used as the start index - Will always be larger than the other randomly selected value.)
				// Could also achieve this by using the code here:
				// http://stackoverflow.com/a/11784059
				while (randomSelectionIndex1 == randomSelectionIndex2) {
					randomSelectionIndex2 = random.nextInt(parent1.getSize());
				}

				// Calculate the start and end indexes for the block of individuals to copy over from each parent.
				int crossoverSelectionStartIndex = Math.min(randomSelectionIndex1, randomSelectionIndex2);
				int crossoverSelectionEndIndex = Math.max(randomSelectionIndex1, randomSelectionIndex2);

				// Extract the randomly-sized blocks from each parent.
				ArrayList<TSPLocation> parent1SubCollection = new ArrayList<TSPLocation>(parent1.getRouteLocations().subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));
				ArrayList<TSPLocation> parent2SubCollection = new ArrayList<TSPLocation>(parent2.getRouteLocations().subList(crossoverSelectionStartIndex, crossoverSelectionEndIndex));

				// Assign the extracted blocks to the opposite child.
				child1Chromosome.addAll(parent1SubCollection);
				child2Chromosome.addAll(parent2SubCollection);


				// Loop through both parents, adding any remaining individuals (FROM THE RIGHT OF THE BLOCK) and paying attention to preserve the order of these other individuals.
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

				// Rotate both collections so that the blocks contained within both children match the corresponding start and end indexes from the parents.
				Collections.rotate(child1Chromosome, crossoverSelectionStartIndex);
				Collections.rotate(child2Chromosome, crossoverSelectionStartIndex);

				childRoutes.add(new TSPRoute(child1Chromosome));

				if (!singleChildOnly) {
					childRoutes.add(new TSPRoute(child2Chromosome));
				}

			} else {
				// If the sizes of both parents do not match, then we have a problem.
				throw new Exception("Parent 1 and Parent 2 sizes do not match. Aborting order one crossover.");
			}

		} else {

			// If we get to this point, we have determined that we shouldn't be performing crossover for this iteration.
			// Therefore, simply return the two original parents unchanged.
			childRoutes.add(parent1);
			childRoutes.add(parent2);

		}

		return childRoutes;
	}

	/**
	 * Performs crossover between two parent chromosomes in order to generate new child chromosomes using the 'Cycle Crossover' operator.
	 * 
	 * Based originally upon a VB implementation sourced from: http://www.rubicite.com/Tutorials/GeneticAlgorithms/CrossoverOperators/CycleCrossoverOperator.aspx
	 * 
	 * @param parent1 The first parent chromosome to breed.
	 * @param parent2 The first parent chromosome to breed.
	 * @return Two new child chromosomes consisting of values selected and copied from the two original parent chromosomes.
	 * @throws Exception
	 */
	public ArrayList<TSPRoute> performCycleCrossover(TSPRoute parent1, TSPRoute parent2) throws Exception {

		Random random = new Random();

		ArrayList<TSPRoute> childRoutes = new ArrayList<>();

		if (random.nextDouble() <= this.crossoverRate) {

			// If the parent sizes aren't the same, bail out.
			if (parent1.getSize() == parent2.getSize()) {

				// We need a way of detecting if we have already generated a cycle containing a given individual's index.
				boolean[] flags = new boolean[parent1.getSize()];

				// Create a new CycleCrossoverItem instance to hold the values extracted for the initial cycle.
				CycleCrossoverItem<TSPLocation> currentCycleItem = new CycleCrossoverItem<TSPLocation>();

				// 1. Generate a hashtable for the index of Parent 1.
				// This enables a fast lookup when mapping indexes of the parent's gene collection to their associated values.
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


					// Check that we haven't previously processed the current index in another cycle. - PREVENTS DUPLICATES.
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

						// Use the 'mod' operator to select either the first or second parent's cycle value based on whether the counter is ODD or EVEN.
						child1[tempPair.parent1Index] = (counter % 2 == 0) ? (TSPLocation) tempPair.parent1Value : (TSPLocation) tempPair.parent2Value;
						child2[tempPair.parent1Index] = (counter % 2 == 0) ? (TSPLocation) tempPair.parent2Value : (TSPLocation) tempPair.parent1Value;

					}

					counter++;
				}

				childRoutes.add(new TSPRoute(child1));
				childRoutes.add(new TSPRoute(child2));

			} else {
				throw new Exception("Parent 1 and Parent 2 sizes do not match. Aborting cycle crossover.");
			}

			

		} else {

			// If we get to this point, we have determined that we shouldn't be performing crossover for this iteration.
			// Therefore, simply return the two original parents unchanged.
			childRoutes.add(parent1);
			childRoutes.add(parent2);

		}

		return childRoutes;

	}


	/**
	 * Performs mutation on the specified chromosome using the PERMUTATION-ENCODING FRIENDLY 'Swap Mutation' approach.
	 * See: https://www.youtube.com/watch?v=13YQVcuT30Q for more information.
	 * @param chromosome The 'TSPRoute' chromosome to mutate.
	 */
	public void performSwapMutation(TSPRoute chromosome) {

		// We are passing by value, but NOT making a copy, so if we change the
		// parameter object, it will change the original object also.
		// See: http://stackoverflow.com/a/12429953 for more information.

		// We use a swap mutation for PERUMUTATION REPRESENTATIONS.
		Random random = new Random();

		ArrayList<TSPLocation> routeLocations = chromosome.getRouteLocations();

		for (int i = 0; i < routeLocations.size(); i++) {

			if (random.nextDouble() < this.mutationRate) {

				int swapIndex1 = random.nextInt(chromosome.getSize());
				int swapIndex2 = random.nextInt(chromosome.getSize());

				// Make sure the random indexes are not the same.
				while(swapIndex1 == swapIndex2) {
					swapIndex2 = random.nextInt(routeLocations.size());
				}

				// Simply swap the positions of the two selected chromosome individuals.
				TSPLocation swapLoc1 = chromosome.getRouteLocations().get(swapIndex1);
				TSPLocation swapLoc2 = chromosome.getRouteLocations().get(swapIndex2);

				chromosome.setLocationAtPosition(swapIndex2, swapLoc1);
				chromosome.setLocationAtPosition(swapIndex1, swapLoc2);

			}
		}

	}

	/**
	 * Represents a single 'Cycle' generated between parent chromosomes for Cycle Crossover. 
	 * See: http://www.rubicite.com/Tutorials/GeneticAlgorithms/CrossoverOperators/CycleCrossoverOperator.aspx for more information.
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 * @param <T> Type extending 'GAGene' that represents an individual from a parent chromosome.
	 */
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
