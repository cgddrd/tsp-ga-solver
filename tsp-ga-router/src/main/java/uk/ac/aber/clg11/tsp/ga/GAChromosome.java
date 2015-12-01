package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class GAChromosome<T extends Number, K extends GAGene> implements IGAChromosome<T> {	
	
	protected ArrayList<K> genes;
	//protected double currentFitness = 0;
	
	public GAChromosome() {
		this.genes = new ArrayList<K>();
	}
	
	public GAChromosome(K[] genes) {
		this.setGenes(new ArrayList<K>(Arrays.asList(genes)), false);
	}
	
	public GAChromosome(ArrayList<K> genes) {
		this.setGenes(genes, false);
	}
	
	public GAChromosome(ArrayList<K> genes, boolean shouldRandomise) {
		this.setGenes(genes, shouldRandomise);
	}
	
	private void shuffleGenes() {
		
		// Create new seed then shuffle collection - http://stackoverflow.com/a/4229001
		long seed = System.nanoTime();
		Collections.shuffle(this.genes, new Random(seed));
		
	}
	
	protected void setGenes(ArrayList<K> genes, boolean shouldRandomise) {
		
		this.genes = new ArrayList<K>(genes);
		
		if (shouldRandomise) {
			this.shuffleGenes();
		}
	}
	
	public ArrayList<K> getGenes() {
		return this.genes;
	}
	
	public int getSize() {
		return genes.size();
	}
	
	protected void resetFitness() {
		//this.currentFitness = 0;
	}
	
	public abstract T getFitness();
}
