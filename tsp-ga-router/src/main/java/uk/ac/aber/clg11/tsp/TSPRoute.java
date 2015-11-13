package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;

public class TSPRoute extends GAChromosome<Double, TSPLocation> {
	
	// TSPRoute provides PERMUTATION ENCODING of TSP (which is an ORDERING PROBLEM).
	// See: https://youtu.be/jnnN38tBpTo for more information.
	
	private int routeDistance = 0;

	public TSPRoute() {
		super();
	}
	
	public TSPRoute(ArrayList<TSPLocation> locations) {
		super(locations, false);
	}
	
	public TSPRoute(ArrayList<TSPLocation> locations, boolean shouldRandomise) {
		super(locations, shouldRandomise);
//		currentFitness = 0;
//		genes = new ArrayList<TSPLocation>();
//		testString = "Blah";
//		
//		genes = new ArrayList<>(locations);
//		
//		Collections.shuffle(genes);
//		
//		System.out.println(genes);
	}
	
	public ArrayList<TSPLocation> getRouteLocations() {
		return this.getGenes();
	}
	
	
	public TSPLocation getLocationAtPosition(int positionIndex) {
		return this.getRouteLocations().get(positionIndex);
	}
	
	public void setLocationAtPosition(int positionIndex, TSPLocation newLocation) {
		
		this.getRouteLocations().set(positionIndex, newLocation);
		
		// We need to reset the distance and fitness of the current route if we change any of the route locations.
		this.routeDistance = 0;
		super.currentFitness = 0;
		
	}

	public int getRouteDistance() {
		
		if (this.routeDistance <= 0) {
			
			this.routeDistance = calcRouteDistance();
		}
		
		return this.routeDistance;
	}
	
	private int calcRouteDistance() {
		
		int totalRouteDistance = 0;
		int routeSize = this.getRouteSize();
		ArrayList<TSPLocation> routeLocations = this.getRouteLocations();
	
		for (int i = 0; i < this.getRouteSize(); i++) {
			
			TSPLocation currentLocation = routeLocations.get(i);
			
			int currentIndex = i;
			
			// We use the 'modulo' operator to make sure we always return to the first location when we process the last location.
			int nextIndex = ++currentIndex % routeSize;
			
			TSPLocation nextLocation = routeLocations.get(nextIndex);
			
			//Calculate distance between the two cities.
			totalRouteDistance += calcStraightLineDistance(currentLocation, nextLocation);
		}
		
		return totalRouteDistance;
		
	}
	
	// Based on 2D straight-line distance equation taken. 
	// See: http://www.calculatorsoup.com/calculators/geometry-plane/distance-two-points.php for more information.
	private double calcStraightLineDistance(TSPLocation startLocation, TSPLocation endLocation) {
		
		int xDistance = Math.abs(startLocation.getxCoord() - endLocation.getxCoord());
        int yDistance = Math.abs(startLocation.getyCoord() - endLocation.getxCoord());
        
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
		
	}
	
	@Override
	public void resetFitness() {
		this.currentFitness = 0;
		this.routeDistance = 0;
	}
	
	@Override
	public Double getFitness() {
		
		if (this.currentFitness <= 0) {
			this.currentFitness = (1 / (double) this.getRouteDistance());
		}
		
		return this.currentFitness;
	}

	public int getRouteSize() {
		return super.getSize();
	}
	
	//TODO: Need to make this better.
	public boolean containsLocation(TSPLocation searchLocation) {
		return this.getGenes().contains(searchLocation);
	}

}
