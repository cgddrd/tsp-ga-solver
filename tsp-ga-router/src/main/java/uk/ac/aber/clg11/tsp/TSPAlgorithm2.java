package uk.ac.aber.clg11.tsp;

public class TSPAlgorithm2 {

	/* GA parameters */
	private static final double mutationRate = 0.015;
	private static final int tournamentSize = 5;
	private static final boolean elitism = true;

	// Evolves a population over one generation
	public static TSPPopulation evolvePopulation(TSPPopulation pop) {
		
		TSPPopulation newPopulation = new TSPPopulation(pop.getPopulationSize());

		// Keep our best individual if elitism is enabled
		int elitismOffset = 0;
		
		if (elitism) {
			newPopulation.addRoute(pop.getFittestCandidate());
			elitismOffset = 1;
			//System.out.println("dsada");
		}

		// Crossover population
		// Loop over the new population's size and create individuals from
		// Current population
		for (int i = elitismOffset; i < pop.getPopulationSize(); i++) {
			// Select parents
			TSPRoute parent1 = tournamentSelection(pop);
			TSPRoute parent2 = tournamentSelection(pop);
			// Crossover parents
			TSPRoute child = crossover(parent1, parent2);
			// Add child to new population
			newPopulation.addRoute(child);
		}

		// Mutate the new population a bit to add some new genetic material
		for (int i = elitismOffset; i < newPopulation.getPopulationSize(); i++) {
			mutate(newPopulation.getRouteAtIndex(i));
		}

		return newPopulation;
	}

	// Applies crossover to a set of parents and creates offspring
	public static TSPRoute crossover(TSPRoute parent1, TSPRoute parent2) {
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

	// Mutate a tour using swap mutation
	private static void mutate(TSPRoute tour) {
		// Loop through tour cities
		for (int tourPos1 = 0; tourPos1 < tour.getRouteSize(); tourPos1++) {
			// Apply mutation rate
			if (Math.random() < mutationRate) {
				// Get a second random position in the tour
				int tourPos2 = (int) (tour.getRouteSize() * Math.random());

				// Get the cities at target position in tour
				TSPLocation city1 = tour.getLocationAtPosition(tourPos1);
				TSPLocation city2 = tour.getLocationAtPosition(tourPos2);

				// Swap them around
				tour.setLocationAtPosition(tourPos2, city1);
				tour.setLocationAtPosition(tourPos1, city2);
			}
		}
	}

	// Selects candidate tour for crossover
	private static TSPRoute tournamentSelection(TSPPopulation pop) {
		// Create a tournament population
		TSPPopulation tournament = new TSPPopulation(tournamentSize);
		// For each place in the tournament get a random candidate tour and
		// add it
		for (int i = 0; i < tournamentSize; i++) {
			int randomId = (int) (Math.random() * pop.getPopulationSize());
			tournament.addRoute(pop.getRouteAtIndex(randomId));
		}
		// Get the fittest tour
		TSPRoute fittest = tournament.getFittestCandidate();
		return fittest;
	}
}
