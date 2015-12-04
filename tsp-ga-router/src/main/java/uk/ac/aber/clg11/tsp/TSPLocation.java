package uk.ac.aber.clg11.tsp;

import uk.ac.aber.clg11.tsp.ga.GAGene;

public class TSPLocation extends GAGene {
	
	int xCoord;
	int yCoord;
	
	public TSPLocation(int newX, int newY) {
		this.xCoord = newX;
		this.yCoord = newY;
	}

	public int getxCoord() {
		return xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	@Override
	public String toString() {
		return "(" + xCoord + ", " + yCoord + ")";
	}
	
}
