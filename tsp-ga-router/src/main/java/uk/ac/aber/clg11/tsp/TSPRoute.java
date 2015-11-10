package uk.ac.aber.clg11.tsp;

import uk.ac.aber.clg11.tsp.ga.GAChromosome;

public class TSPRoute extends GAChromosome<Integer, TSPLocation> {
	
	private int distance = 0;
	
	public TSPRoute() {
		super();
		//this.sequence = new java.util.LinkedList<>();
		//this.sequence = new ArrayList<String>();
	}
	
	@Override
	public Integer calcFitness() {
		return 1;
	}


}
