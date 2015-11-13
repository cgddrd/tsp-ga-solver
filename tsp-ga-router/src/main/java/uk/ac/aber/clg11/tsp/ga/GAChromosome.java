package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class GAChromosome<T extends Number, K extends GAGene> implements IGAChromosome<T> {	
	
	private ArrayList<K> genes;
	protected double currentFitness = 0;
	
	protected GAChromosome() {
		this.genes = new ArrayList<K>();
	}
	
	protected GAChromosome(ArrayList<K> genes) {
		this.setGenes(genes, false);
	}
	
	protected GAChromosome(ArrayList<K> genes, boolean shouldRandomise) {
		this.setGenes(genes, shouldRandomise);
	}
	
	private void shuffleGenes() {
		
		// Create new seed then randomise collection - http://stackoverflow.com/a/4229001
		long seed = System.nanoTime();
		Collections.shuffle(this.genes, new Random(seed));
		
	}
	
	public void setGenes(ArrayList<K> genes, boolean shouldRandomise) {
		
		this.genes = genes;
		
		if (shouldRandomise) {
			this.shuffleGenes();
		}
	}
	
	protected ArrayList<K> getGenes() {
		return this.genes;
	}
	
	public int getSize() {
		return genes.size();
	}
	
	protected void resetFitness() {
		this.currentFitness = 0;
	}
	
	public abstract T getFitness();
}
