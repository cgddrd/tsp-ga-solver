package uk.ac.aber.clg11.tsp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TSPExperiment {

	private String experimentName = "TSPExperiment_"
			+ new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(new Date());
	private int experimentGenerations;
	private int noOfLocations;

	private TSPMutationSettings mutationSettings = new TSPMutationSettings();
	private TSPCrossoverSettings crossoverSettings = new TSPCrossoverSettings();
	private TSPSelectionSettings selectionSettings = new TSPSelectionSettings();
	private TSPPopulationSettings populationSettings = new TSPPopulationSettings();
	private TSPExperimentResults experimentResults = new TSPExperimentResults();

	public TSPExperiment() {

	}

	public TSPExperiment(String experimentName, int generationNumber) {
		setExperimentName(experimentName);
		setExperimentGenerations(generationNumber);
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public String getExperimentName() {
		return experimentName;
	}

	public int getExperimentGenerations() {
		return experimentGenerations;
	}

	public void setExperimentGenerations(int experimentGenerations) {
		this.experimentGenerations = experimentGenerations;
	}

	public int getNoOfLocations() {
		return noOfLocations;
	}

	public void setNoOfLocations(int noOfLocations) {
		this.noOfLocations = noOfLocations;
	}

	public TSPMutationSettings getMutationSettings() {
		return mutationSettings;
	}

	public TSPCrossoverSettings getCrossoverSettings() {
		return crossoverSettings;
	}

	public TSPSelectionSettings getSelectionSettings() {
		return selectionSettings;
	}

	public TSPPopulationSettings getPopulationSettings() {
		return populationSettings;
	}

	public TSPExperimentResults getExperimentResults() {
		return experimentResults;
	}

	public void setExperimentResults(TSPExperimentResults experimentResults) {
		this.experimentResults = experimentResults;
	}

	@Override
	public String toString() {
		return "TSPExperiment\n\nexperimentName=" + experimentName + "\n\ndate="
				+ new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(new Date())
				+ "\n\nexperimentResults=" + experimentResults.toString()
				+ "\n\nexperimentGenerations=" + experimentGenerations 
				+ "\n\nnoOfLocations=" + noOfLocations + "\n\nmutationSettings="
				+ mutationSettings.toString() + "\n\ncrossoverSettings=" + crossoverSettings.toString()
				+ "\n\nselectionSettings=" + selectionSettings.toString() + "\n\npopulationSettings="
				+ populationSettings.toString();
	}

	public class TSPExperimentResults {

		private long experimentDuration;
		private String experimentDurationString;
		private double averageFitness = -1;
		private double bestFitness = -1;
		private int bestDistance = -1;

		public TSPExperimentResults() {
		}
		
		@Override
		public String toString() {
			return "TSPExperimentResults [experimentDuration=" + experimentDuration + ", experimentDurationString="
					+ experimentDurationString + ", averageFitness=" + averageFitness + ", bestFitness=" + bestFitness
					+ ", bestDistance=" + bestDistance + "]";
		}

		public long getExperimentDuration() {
			return experimentDuration;
		}

		public void setExperimentDuration(long experimentDuration) {
			this.experimentDuration = experimentDuration;
		}

		public String getExperimentDurationString() {
			return experimentDurationString;
		}

		public void setExperimentDurationString(String experimentDurationString) {
			this.experimentDurationString = experimentDurationString;
		}

		public double getAverageFitness() {
			return averageFitness;
		}

		public void updateAverageFitness(double averageFitness) {
			this.averageFitness = (this.averageFitness == -1 || averageFitness > this.averageFitness) ? averageFitness : this.averageFitness;
		}

		public double getBestFitness() {
			return bestFitness;
		}

		public void updateBestFitness(double bestFitness) {
			this.bestFitness = (this.bestFitness == -1 || bestFitness > this.bestFitness) ? bestFitness : this.bestFitness;
		}

		public int getBestDistance() {
			return bestDistance;
		}

		public void updateBestDistance(int bestDistance) {
			this.bestDistance = (this.bestDistance == -1 || bestDistance < this.bestDistance) ? bestDistance : this.bestDistance;
		}

	}

	public class TSPMutationSettings {

		private Double mutationRate;

		private TSPMutationSettings() {

		}

		public void setMutationRate(Double mutationRate) {
			this.mutationRate = mutationRate;
		}

		public Double getMutationRate() {
			return mutationRate;
		}

		@Override
		public String toString() {
			return "TSPMutationSettings [mutationRate=" + mutationRate + "]";
		}

	}

	public class TSPCrossoverSettings {

		private String crossoverMethod = "ordered";
		private Double crossoverRate;
		private Boolean returnSingleChild = false;

		private TSPCrossoverSettings() {

		}

		public Double getCrossoverRate() {
			return crossoverRate;
		}

		public void setCrossoverRate(Double crossoverRate) {
			this.crossoverRate = crossoverRate;
		}

		public String getCrossoverMethod() {
			return crossoverMethod;
		}

		public void setCrossoverMethod(String crossoverMethod) {
			this.crossoverMethod = crossoverMethod;
		}

		@Override
		public String toString() {
			return "TSPCrossoverSettings [crossoverMethod=" + crossoverMethod + ", crossoverRate=" + crossoverRate
					+ ", returnSingleChild=" + returnSingleChild + "]";
		}

	}

	public class TSPSelectionSettings {

		private String selectionMethod = "tournament";
		private Integer tournamentSize;
		private Boolean useElitism = true;
		private Boolean returnSingleChild = false;

		private TSPSelectionSettings() {

		}

		public String getSelectionMethod() {
			return selectionMethod;
		}

		public void setSelectionMethod(String selectionMethod) {
			this.selectionMethod = selectionMethod.toLowerCase();
		}

		public Integer getTournamentSize() {
			return tournamentSize;
		}

		public void setTournamentSize(Integer tournamentSize) {
			this.tournamentSize = tournamentSize;
		}

		public Boolean getUseElitism() {
			return useElitism;
		}

		public void setUseElitism(Boolean useElitism) {
			this.useElitism = useElitism;
		}

		public Boolean getReturnSingleChild() {
			return returnSingleChild;
		}

		public void setReturnSingleChild(Boolean returnSingleChild) {
			this.returnSingleChild = returnSingleChild;
		}

		@Override
		public String toString() {
			return "TSPSelectionSettings [selectionMethod=" + selectionMethod + ", tournamentSize=" + tournamentSize
					+ ", useElitism=" + useElitism + ", returnSingleChild=" + returnSingleChild + "]";
		}

	}

	public class TSPPopulationSettings {

		private int populationSize;

		private TSPPopulationSettings() {

		}

		public int getPopulationSize() {
			return populationSize;
		}

		public void setPopulationSize(int populationSize) {
			this.populationSize = populationSize;
		}

		@Override
		public String toString() {
			return "TSPPopulationSettings [populationSize=" + populationSize + "]";
		}

	}

}
