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
		return "TSPLocation [xCoord = " + xCoord + ", yCoord = " + yCoord + "]";
	}
	
	// Based on 2D straight-line distance equation taken. 
	// See: http://www.calculatorsoup.com/calculators/geometry-plane/distance-two-points.php for more information.
    public double distanceTo(TSPLocation otherLocation) {
    	
        int xDistance = Math.abs(getxCoord() - otherLocation.getxCoord());
        int yDistance = Math.abs(getyCoord() - otherLocation.getxCoord());
        
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
        
    }
	
}
