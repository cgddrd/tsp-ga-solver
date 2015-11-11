package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class GAChromosome<T extends Number, K> implements IGAChromosome<T> {	
	
	protected List<K> genes;
	
	protected double currentFitness = 0;
	
	public GAChromosome() {
		genes = new ArrayList<K>();
	}
	
	public GAChromosome(List<K> genes) {
		this.genes = genes;
	}
	
	public int getSize() {
		return genes.size();
	}
	
	protected void shuffleGenes() {
		
		// Create new seed then randomise collection - http://stackoverflow.com/a/4229001
		long seed = System.nanoTime();
		Collections.shuffle(this.genes, new Random(seed));
		
	}
	
	public List<K> getGenes() {
		return this.genes;
	}
	
	protected void resetFitness() {
		this.currentFitness = 0;
	}
}
