package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Arrays;

public class TSP {
	
	public static void main(String[] args) {
        
        TSPLocation loc1 = new TSPLocation(20, 50);
        TSPLocation loc2 = new TSPLocation(30, 12);
        TSPLocation loc3 = new TSPLocation(102, 154);
        
        ArrayList<TSPLocation> test = new ArrayList<TSPLocation>();
        
        test.add(loc1);
        test.add(loc2);
        test.add(loc3);
        
        TSPRoute route = new TSPRoute(test);
        
        int test2 = route.getRouteDistance();
        
        
    }
	
	
}
