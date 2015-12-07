package uk.ac.aber.clg11.tsp;

/**
 * Represents an individual pair of XY coordinates that depict the 2D position of an individual TSP location.
 * 
 * @author Connor Goddard (clg11@aber.ac.uk)
 */
public class TSPCoord {
	
	private int x;
	private int y;
	private String name;
	
	public TSPCoord() {
		
	}
	
	public TSPCoord(String name, int x, int y) {
		this.name = name;
		this.setCoords(x, y);	
	}
	
	public void setCoords(int x, int y) {
		this.x = x;
		this.y = y;	
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return x + ", " + y;
	}
	
	@Override
	public boolean equals(Object obj) {
		TSPCoord c = (TSPCoord) obj;
        return c.x == x && c.y == y;
	}
	
}
