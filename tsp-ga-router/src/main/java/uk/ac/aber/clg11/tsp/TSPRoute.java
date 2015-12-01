package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;

public class TSPRoute extends GAChromosome<Double, TSPLocation> {

	// TSPRoute provides PERMUTATION ENCODING of TSP (which is an ORDERING
	// PROBLEM).
	// See: https://youtu.be/jnnN38tBpTo for more information.

	// private int routeDistance = 0;

	public TSPRoute() {
		super();
	}

	public TSPRoute(int size) {

		this.genes = new ArrayList<TSPLocation>(Collections.nCopies(size, null));
	}

	public TSPRoute(TSPLocation[] locations) {
		super(locations);
	}

	public TSPRoute(ArrayList<TSPLocation> locations) {
		super(locations, false);
	}

	public TSPRoute(ArrayList<TSPLocation> locations, boolean shouldRandomise) {
		super(locations, shouldRandomise);
	}

	public ArrayList<TSPLocation> getRouteLocations() {
		return this.getGenes();
	}

	public TSPLocation getLocationAtPosition(int positionIndex) {
		return this.getRouteLocations().get(positionIndex);
	}

	public void setLocationAtPosition(int positionIndex, TSPLocation newLocation) {

		this.getRouteLocations().set(positionIndex, newLocation);

	}

	public int getRouteDistance() {

		int routeDistance = calcRouteDistance();

		return routeDistance;
	}

	private int calcRouteDistance() {

		int tourDistance = 0;
		// Loop through our tour's cities
		for (int cityIndex = 0; cityIndex < this.getRouteSize(); cityIndex++) {
			// Get city we're travelling from
			TSPLocation fromCity = this.getLocationAtPosition(cityIndex);
			// City we're travelling to
			TSPLocation destinationCity;
			// Check we're not on our tour's last city, if we are set our
			// tour's final destination city to our starting city
			if (cityIndex + 1 < this.getRouteSize()) {
				destinationCity = this.getLocationAtPosition(cityIndex + 1);
			} else {
				destinationCity = this.getLocationAtPosition(0);
			}
			// Get the distance between the two cities

			tourDistance += this.calcStraightLineDistance(fromCity, destinationCity);

		}

		return tourDistance;

	}

	// Based on 2D straight-line distance equation.
	// See:
	// http://www.calculatorsoup.com/calculators/geometry-plane/distance-two-points.php
	// for more information.
	private double calcStraightLineDistance(TSPLocation startLocation, TSPLocation endLocation) {

		int xDistance = Math.abs(startLocation.getxCoord() - endLocation.getxCoord());
		int yDistance = Math.abs(startLocation.getyCoord() - endLocation.getxCoord());

		double distance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));

		return distance;

	}

	@Override
	public void resetFitness() {
		// this.currentFitness = 0;
		// this.routeDistance = 0;
	}

	@Override
	public Double getFitness() {

		Double currentFitness = (1 / (double) this.getRouteDistance());

		return currentFitness;
	}

	public int getRouteSize() {
		return super.getSize();
	}

	// TODO: Need to make this better.
	public boolean containsLocation(TSPLocation searchLocation) {
		return this.genes.contains(searchLocation);
	}

	@Override
	public String toString() {
		String geneString = "|";
		for (int i = 0; i < this.getRouteSize(); i++) {
			geneString += this.getLocationAtPosition(i) + "|";
		}
		return geneString;
	}

}
