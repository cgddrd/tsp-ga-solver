package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.List;

//public abstract class Chromosome<T extends Number, K extends List<?>> {
public abstract class GAChromosome<T extends Number, K> implements IGAChromosome<T> {	
	
	protected List<?> genes;
	
	protected double currentFitness = 0;
	
	public GAChromosome() {
		genes = new ArrayList<K>();
	}
	
}
