package uk.ac.aber.clg11.tsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import uk.ac.aber.clg11.tsp.ga.GAGene;

public class TSP {
	
	public static void main(String[] args) throws Exception {
        
        TSPLocation loc1 = new TSPLocation(20, 50);
        TSPLocation loc2 = new TSPLocation(30, 12);
        TSPLocation loc3 = new TSPLocation(102, 154);
        
        ArrayList<TSPLocation> test = new ArrayList<TSPLocation>();
        
        test.add(loc1);
        test.add(loc2);
        test.add(loc3);
        
        TSPRoute route = new TSPRoute(test);
        
       // int test2 = route.getRouteDistance();
        
        Random random = new Random();
        
//        while(true) {
//        	
//        	int crossoverSelectionStartIndex = ThreadLocalRandom.current().nextInt(7 - 1);
//    		
//        	int minIndex = crossoverSelectionStartIndex + 1;
//    		int maxIndex = 7 - 1;
//    		
//    		int crossoverSelectionEndIndex = ThreadLocalRandom.current().nextInt((maxIndex - minIndex) + 1) + minIndex;
//    		
//    		
//    		System.out.println("Start Index: " + crossoverSelectionStartIndex + " End Index: " + crossoverSelectionEndIndex);
//    		
//    		if (crossoverSelectionStartIndex == crossoverSelectionEndIndex) {
//    			throw new Exception();
//    		}
//        }
		
//          ArrayList<String> parent1 = new ArrayList<String>();
//          
//          parent1.add("A");
//          parent1.add("B");
//          parent1.add("C");
//          parent1.add("D");
//          parent1.add("E");
//          parent1.add("F");
//          
//          ArrayList<String> parent2 = new ArrayList<String>();
//          
//          parent2.add("F");
//          parent2.add("E");
//          parent2.add("D");
//          parent2.add("C");
//          parent2.add("B");
//          parent2.add("A");
//          
//         //System.out.println(test2.size());
//         
//         // .subList takes the start index INCLUSIVELY and the end index EXCLUSIVELY.
//         //List<String> blah = test2.subList(test2.size(), test2.size());
//          
//          int startIndex = 1;
//          int endIndex = 3;
//          
//         List<String> parent1SubCollection = parent1.subList(1, 3);
//         
//         ArrayList<String> child = new ArrayList<String>(Arrays.asList(new String[parent1.size()]));
//          
//         // Copy the sub collection of parent 1 over to the child.
//         for (int i = startIndex; i != endIndex; i++) {
//        	 child.set(i, parent1.get(i));
//         }
//         
//         ArrayList<String> parent2Elements = new ArrayList<String>();
//         
//         ArrayList<String> parent2Copy = new ArrayList<String>(parent2);
//         
//         //Collections.copy(parent2Copy, parent2);
//         
//         Collections.rotate(parent2Copy, startIndex);
//         
//         for (String currentP1Element : parent2Copy) {
//        	 
//        	 if (!parent1SubCollection.contains(currentP1Element)) {
//        		 parent2Elements.add(currentP1Element);
//        		 
//        	 }
//         }
//         
//        int j = 0;
//		 
//		//We want to loop through the range of size for the parents.
//		for (int i = 0; i < parent2Elements.size(); i++) {
//			
//			// We want to start moving from the next element on from the end index of the inserted selection from parent 1.
//			int currentIndex = (endIndex + i) % child.size();
//			
//			//if(!child.contains(test3.get(currentIndex))) {
//			child.set(currentIndex, parent2Elements.get(i));
//			//}
//			
//			j++;
//			
//		}
//		
//		System.out.println("Parent1: " + parent1 + "\n");
//		System.out.println("Parent2: " + parent2 + "\n");
//		System.out.println("Parent2Remaining: " + parent2Elements + "\n");
//		System.out.println("Child:" + child + "\n");

		
		
		
		
  ArrayList<Integer> parent1 = new ArrayList<Integer>();
          
          parent1.add(1);
          parent1.add(2);
          parent1.add(3);
          parent1.add(4);
          parent1.add(5);
          parent1.add(6);
          parent1.add(7);
          parent1.add(8);
          parent1.add(9);
          
          ArrayList<Integer> parent2 = new ArrayList<Integer>();
          
          parent2.add(9);
          parent2.add(3);
          parent2.add(7);
          parent2.add(8);
          parent2.add(2);
          parent2.add(6);
          parent2.add(5);
          parent2.add(1);
          parent2.add(4);
         
         // .subList takes the start index INCLUSIVELY and the end index EXCLUSIVELY.
         //List<String> blah = test2.subList(test2.size(), test2.size());
          
          int startIndex = 3;
          int endIndex = 7;
          
//         List<Integer> parent1SubCollection = parent1.subList(startIndex, endIndex);
         
         //ArrayList<Integer> child = new ArrayList<Integer>(Arrays.asList(new Integer[parent1.size()]));
         
//         ArrayList<Integer> child = new ArrayList<Integer>();
          
         // Copy the sub collection of parent 1 over to the child.
        // for (int i = startIndex; i != endIndex; i++) {
        //	 child.set(i, parent1.get(i));
        // }
         
//         child.addAll(parent1SubCollection);
         
         //ArrayList<Integer> parent2Elements = new ArrayList<Integer>();
         
        // ArrayList<Integer> parent2Copy = new ArrayList<Integer>(parent2);
         
         //Collections.rotate(parent2Copy, startIndex);
         
         //for (Integer currentP2Element : parent2Copy) {
        	 
        //	 if (!parent1SubCollection.contains(currentP2Element)) {
        	//	 parent2Elements.add(currentP2Element);
        //		 
        //	 }
       //  }
         
		//We want to loop through the range of size for the parents.
//		for (int i = 0; i < parent2.size(); i++) {
//			
//			// We want to start moving from the next element on from the end index of the inserted selection from parent 1.
//			int currentIndex = (endIndex + i) % parent2.size();
//			
//			if(!child.contains(parent2.get(currentIndex))) {
//			//child.set(currentIndex, parent2Elements.get(i));
//			
//				child.add(parent2.get(currentIndex));
//			}
//			
//		}
		
//		Collections.rotate(child, startIndex);
          
        
		
		System.out.println("Parent1: " + parent1 + "\n");
		System.out.println("Parent2: " + parent2 + "\n");
		//System.out.println("Parent2Remaining: " + parent2Elements + "\n");
		
		
		ArrayList<Integer> child1 = crossOver1(parent1, parent2);
		
		System.out.println("Child1:" + child1 + "\n");
		
		ArrayList<Integer> child2 = crossOver2(parent1, parent2);
		
		System.out.println("Child2:" + child1 + "\n");

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
