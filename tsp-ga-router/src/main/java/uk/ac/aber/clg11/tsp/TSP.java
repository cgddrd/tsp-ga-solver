package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import uk.ac.aber.clg11.tsp.ga.GAGene;

//import org.apache.log4j.Logger;

public class TSP {
	
	public static void main(String[] args) throws Exception {
		
		ArrayList<TSPLocation> cities = new ArrayList<TSPLocation>();
        
		TSPLocation city = new TSPLocation(60, 200);
		cities.add(city);
        TSPLocation city2 = new TSPLocation(180, 200);
        cities.add(city2);
        TSPLocation city3 = new TSPLocation(80, 180);
        cities.add(city3);
        TSPLocation city4 = new TSPLocation(140, 180);
        cities.add(city4);
        TSPLocation city5 = new TSPLocation(20, 160);
        cities.add(city5);
        TSPLocation city6 = new TSPLocation(100, 160);
        cities.add(city6);
        TSPLocation city7 = new TSPLocation(200, 160);
        cities.add(city7);
        TSPLocation city8 = new TSPLocation(140, 140);
        cities.add(city8);
        TSPLocation city9 = new TSPLocation(40, 120);
        cities.add(city9);
        TSPLocation city10 = new TSPLocation(100, 120);
        cities.add(city10);
        TSPLocation city11 = new TSPLocation(180, 100);
        cities.add(city11);
        TSPLocation city12 = new TSPLocation(60, 80);
        cities.add(city12);
        TSPLocation city13 = new TSPLocation(120, 80);
        cities.add(city13);
        TSPLocation city14 = new TSPLocation(180, 60);
        cities.add(city14);
        TSPLocation city15 = new TSPLocation(20, 40);
        cities.add(city15);
        TSPLocation city16 = new TSPLocation(100, 40);
        cities.add(city16);
        TSPLocation city17 = new TSPLocation(200, 40);
        cities.add(city17);
        TSPLocation city18 = new TSPLocation(20, 20);
        cities.add(city18);
        TSPLocation city19 = new TSPLocation(60, 20);
        cities.add(city19);
        TSPLocation city20 = new TSPLocation(160, 20);
        cities.add(city20);
        
        //TSPRoute route1 = new TSPRoute(cities, true);
        //TSPRoute route2 = new TSPRoute(cities, true);
        
       // System.out.println(route1.getRouteLocations());
       // System.out.println(route2.getRouteLocations());
        
        //cities.set(0, new TSPLocation(-1, -1));
        
//        TSPRoute route3 = new TSPRoute(cities, true);
//        
//        System.out.println();
//        
//        System.out.println(route1.getRouteLocations());
//        System.out.println(route2.getRouteLocations());
//        System.out.println(route3.getRouteLocations());
        
        TSPPopulation pop = new TSPPopulation(50, true, cities);
        
        System.out.println("Initial fitness: " + pop.getFittestCandidate().getFitness());
        
        TSPAlgorithm ga = new TSPAlgorithm(0.015, 0.95, 5);
        
        for (TSPRoute test : pop.getRoutes()) {
        	
        	System.out.println(test.getRouteLocations());
        	
        }
        
        pop = ga.evolvePopulation(pop);
        
        // Evolve over 100 generations.
         for (int i = 0; i < 100; i++) {
            pop = ga.evolvePopulation(pop);
        }
         
         // Print final results
        System.out.println("Finished");
        System.out.println("Final fitness: " + pop.getFittestCandidate().getFitness());
        System.out.println("Solution:");
        System.out.println(pop.getFittestCandidate());

    }
	
	public static <K> ArrayList<K> crossOver1 (ArrayList<K> parent1, ArrayList<K> parent2) {
		
		int startIndex = 3;
        int endIndex = 7;
        
		ArrayList<K> child = new ArrayList<K>();
		
		List<K> parent1SubCollection = parent1.subList(startIndex, endIndex);
		
		child.addAll(parent1SubCollection);
		
		for (int i = 0; i < parent2.size(); i++) {
			
			// We want to start moving from the next element on from the end index of the inserted selection from parent 1.
			int currentIndex = (endIndex + i) % parent2.size();
			
			if(!child.contains(parent2.get(currentIndex))) {
				child.add(parent2.get(currentIndex));
			}
			
		}
		
		Collections.rotate(child, startIndex);
		
		return child;
		
	}
	
	public static <K> ArrayList<K> crossOver2 (ArrayList<K> parent1, ArrayList<K> parent2) {
		
		int startIndex = 3;
        int endIndex = 7;
        
        //Create empty Child chromosome set to the same size as the parent.
        ArrayList<K> child = new ArrayList<K>(Collections.nCopies(parent1.size(), null));
		
		// Copy the sub collection extracted from Parent 1 over to the Child.
		for (int i = startIndex; i != endIndex; i++) {
			child.set(i, parent1.get(i));
		}
		
		List<K> parent1SubCollection = parent1.subList(startIndex, endIndex);
		
		ArrayList<K> parent2Elements = new ArrayList<K>();
      
		ArrayList<K> workingParent2 = new ArrayList<K>(parent2);
		
		Collections.rotate(workingParent2, startIndex);
      
		for (K currentP2Element : workingParent2) {
		 	 
			if (!parent1SubCollection.contains(currentP2Element)) {
				parent2Elements.add(currentP2Element);
			}
			
		}
  
		//We want to loop through the range of size for the parents.
		for (int i = 0; i < parent2Elements.size(); i++) {
			
			// We want to start moving from the next element on from the end index of the inserted selection from parent 1.
			int currentIndex = (endIndex + i) % parent2.size();
			child.set(currentIndex, parent2Elements.get(i));
			
		}
		
		return child;
		
	}
	
	
}
