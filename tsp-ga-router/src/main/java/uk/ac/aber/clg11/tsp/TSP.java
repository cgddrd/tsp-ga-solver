package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import uk.ac.aber.clg11.tsp.TSPPlotter.TSPPlotterBuilder;
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
        
//        TSPPopulation pop = new TSPPopulation(50, true, cities);
        
//        System.out.println("Initial fitness: " + pop.getFittestCandidate().getFitness());
//        
        TSPAlgorithm ga = new TSPAlgorithm(0.015, 0.95, 5);
//        
//        pop = ga.evolvePopulation2(pop);
//        
//        // Evolve over 100 generations.
//         for (int i = 0; i < 400; i++) {
//            //pop = ga.evolvePopulation(pop);
//        	 pop = ga.evolvePopulation2(pop);
//        }
//         
//         // Print final results
//        System.out.println("Finished");
//        System.out.println("Final fitness: " + pop.getFittestCandidate().getFitness());
//        System.out.println("Solution:");
//        System.out.println(((TSPRoute) pop.getFittestCandidate()).getRouteLocations());
        
        
        
        System.out.println("\n");
        
        TSPPopulation pop2 = new TSPPopulation(50, true, cities);
        
        System.out.println("Initial fitness2: " + pop2.getFittestCandidate().getFitness());
        
        TSPAlgorithm ga2 = new TSPAlgorithm(0.015, 0.95, 5, true);
        
        pop2 = ga.evolvePopulation2(pop2);
        
        // Evolve over 100 generations.
         for (int i = 0; i < 100; i++) {
        	 pop2 = ga.evolvePopulation2(pop2);
        }
         
        System.out.println("Finished2");
        System.out.println("Final fitness2: " + pop2.getFittestCandidate().getFitness());
        
        System.out.println("Solution:");
        System.out.println(((TSPRoute) pop2.getFittestCandidate()).getRouteLocations());
        System.out.println(pop2.getPopulationSize());

//        TSPPlotter frame = new TSPPlotter.TSPPlotterBuilder().setAxisMaxRangeSettings(200, 200).buildTSPPlotter();
//        
//        frame.updateData();
//        frame.redrawPlot();
//        
//        Timer timer = new Timer();
//        
//	    timer.scheduleAtFixedRate(new TimerTask() {
//	  	  @Override
//	  	  public void run() {
//	  	   frame.updatePlot();
//	  	  }
//	  	}, 1*1000, 1*1000);

    }

}
