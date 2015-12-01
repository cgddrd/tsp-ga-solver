package uk.ac.aber.clg11.tsp.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class GAChromosome<T extends Number, K extends GAGene> implements IGAChromosome<T> {	
	
	protected ArrayList<K> genes = new ArrayList<K>();
	//protected double currentFitness = 0;
	
	public GAChromosome() {
		this.genes = new ArrayList<K>();
	}
	
	//public GAChromosome(K[] genes) {
	//	this.setGenes(new ArrayList<K>(Arrays.asList(genes)), false);
	//}
	
	public GAChromosome(ArrayList<K> newGenes) {
		this.setGenes(newGenes, false);
	}
	
	public GAChromosome(ArrayList<K> newGenes, boolean shouldRandomise) {
		
		for (int i = 0; i < newGenes.size(); i++) {
			this.genes.add(null);
		}
		
		this.setGenes(newGenes, shouldRandomise);
	}
	
	private void shuffleGenes() {
		
		// Create new seed then shuffle collection - http://stackoverflow.com/a/4229001
		Collections.shuffle(this.genes);
		
	}
	
	protected void setGenes(ArrayList<K> newGenes, boolean shouldRandomise) {
		
		for (int i = 0; i < newGenes.size(); i++) {
			
			this.genes.set(i, newGenes.get(i));
			
		}
		
		if (shouldRandomise) {
			//this.shuffleGenes();
			
			Collections.shuffle(this.genes);
		}
	}
	
	public ArrayList<K> getGenes() {
		return this.genes;
	}
	
	public int getSize() {
		return this.genes.size();
	}
	
	protected void resetFitness() {
		//this.currentFitness = 0;
	}
	
	public abstract T getFitness();
}
