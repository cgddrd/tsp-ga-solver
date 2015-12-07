package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Abstract class providing a common representation for an individual GA chromosome within the current GA implementation.
 * 
 * @author Connor Goddard (clg11@aber.ac.uk)
 * 
 * @param <T> The type extending 'java.lang.Number' that represents the fitness value for a GAChromosome. (Typically 'Double' or 'Integer')
 * @param <K> The type extending 'GAGene' that represents an individual gene within a GAChromosome within the current GA implementation. 
 * 
 */
public abstract class GAChromosome<T extends Number, K extends GAGene> implements IGAChromosome<T> {	
	
	protected ArrayList<K> genes;
	protected double currentFitness = 0;
	
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
	
	public ArrayList<K> getGenes() {
		return this.genes;
	}
	
	protected void setGenes(ArrayList<K> genes, boolean shouldRandomise) {
		
		this.genes = new ArrayList<K>(genes);
		
		// If set to true, we should randomise the collection of individuals (genes) for this specific chromosome.
		if (shouldRandomise) {
			this.shuffleGenes();
		}
	}
	
	public int getSize() {
		return genes.size();
	}
	
	public void resetFitness() {
		this.currentFitness = 0;
	}
	
	/**
	 * Shuffles the collection of individuals represented by the chromosome. Primarily required for generating the initial population set. 
	 */
	private void shuffleGenes() {
		
		// Create new seed then shuffle collection - http://stackoverflow.com/a/4229001
		long seed = System.nanoTime();
		Collections.shuffle(this.genes, new Random(seed));
		
	}
	
	public abstract T getFitness();
}
