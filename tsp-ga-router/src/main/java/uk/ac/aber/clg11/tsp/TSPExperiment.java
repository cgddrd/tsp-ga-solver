package uk.ac.aber.clg11.tsp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Represents an individual TSP-GA experiment. Holds all configuration settings and results associated with an individual experiment.
 *
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 */
public class TSPExperiment {

	private String experimentName = "TSPExperiment_"
			+ new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(new Date());
	
	private int experimentGenerations;
	private int noOfLocations;
	private int experimentRuns = 1;
	
	private int overallBestDistance = Integer.MAX_VALUE;
	private TSPRoute overallFittestRoute = null;

	private TSPMutationSettings mutationSettings = new TSPMutationSettings();
	private TSPCrossoverSettings crossoverSettings = new TSPCrossoverSettings();
	private TSPSelectionSettings selectionSettings = new TSPSelectionSettings();
	private TSPPopulationSettings populationSettings = new TSPPopulationSettings();
	
	private ArrayList<TSPExperimentResult> experimentResults = new ArrayList<>();

	public TSPExperiment() {

	}

	public TSPExperiment(String experimentName, int noOfGens) {
		setExperimentName(experimentName);
		setExperimentGenerations(noOfGens);
	}
	
	/**
	 * Returns the overall fittest TSPRoute across all runs of the current experiment.
	 * @return TSPRoute holding the greatest overall fitness across all experiment runs.
	 */
	public TSPRoute getFittestRouteAllRuns() {
		
		int overallBestDistance = Integer.MAX_VALUE;
		
		if (this.overallFittestRoute == null) {
			
			for (TSPExperimentResult currentResult : this.experimentResults) {
				
				if (currentResult.getCurrentFittestCandidate().getRouteDistance() <= overallBestDistance) {
					
					this.overallFittestRoute = currentResult.getCurrentFittestCandidate();
					
				}
				
			}
			
		}
		
		return overallFittestRoute;
	}
	
	/**
	 * Returns the greatest overall distance from inspecting all runs of the current experiment.
	 * @return The greatest overall distance across all experiment runs.
	 */
	public int getBestDistanceAllRuns() {
		
		if (this.overallBestDistance == Integer.MAX_VALUE) {
			
			for (TSPExperimentResult currentResult : this.experimentResults) {
				
				if (currentResult.getBestDistance() <= this.overallBestDistance) {
					
					this.overallBestDistance = currentResult.getBestDistance();
					
				}
				
			}

		}
		
		return overallBestDistance;
	}
	
	/**
	 * Returns a collection of average best distances for each generation.
	 * @return Collection of average best distances for each generation.
	 */
	public ArrayList<Integer> getBestDistanceAveragePerGeneration() {
		
		ArrayList<Integer> experimentRunsBestOverallDistanceAverages = new ArrayList<>();
		
		for (int i = 0; i < this.getExperimentGenerations(); i++) {
			
			int sumGenerationDistances = 0;
			
			for (TSPExperimentResult currentResult : this.getExperimentResultsCollection()) {
				
				sumGenerationDistances += currentResult.getExperimentBestDistances().get(i);
				
			}
			
			experimentRunsBestOverallDistanceAverages.add(sumGenerationDistances / this.getExperimentRuns());
			
		}
		
		return experimentRunsBestOverallDistanceAverages;
		
	}
	
	/**
	 * Returns a collection of average average distances for each generation.
	 * @return Collection of average distances for each generation.
	 */
	public ArrayList<Integer> getAverageDistanceAveragePerGeneration() {
		
		ArrayList<Integer> experimentRunsAverageDistanceAverages = new ArrayList<>();
		
		for (int i = 0; i < this.experimentGenerations; i++) {
			
			int sumGenerationAverages = 0;
			
			for (TSPExperimentResult currentResult : this.getExperimentResultsCollection()) {
				
				sumGenerationAverages += currentResult.getExperimentAverageDistances().get(i);
				
			}
			
			experimentRunsAverageDistanceAverages.add(sumGenerationAverages / this.getExperimentRuns());
			
		}
		
		return experimentRunsAverageDistanceAverages;
		
	}
	
	/**
	 * Calculates the best average distance across all experiment runs and generations.
	 * @return The best average distance across all experiment runs and generations.
	 */
	public int getAverageBestDistanceAllRuns() {
		
		int experimentOverallAverageBestDistance = 0;
		
		for (Integer averageBestDistance: this.getBestDistanceAveragePerGeneration()) {
			
			experimentOverallAverageBestDistance += averageBestDistance;
			
		}
		
		return experimentOverallAverageBestDistance / this.getBestDistanceAveragePerGeneration().size();
	}
	
	/**
	 * Calculates the average duration for the GA to complete across all experiment runs.
	 * @return The average duration (nanoseconds) for the GA to complete across all experiment runs.
	 */
	public long getAverageDurationAllRuns() {
		
		long experimentAverageDuration = 0;
		
		for (TSPExperimentResult currentResult: this.getExperimentResultsCollection()) {
			
			experimentAverageDuration += currentResult.getExperimentDuration();
			
		}
		
		return experimentAverageDuration / this.getExperimentResultsCollection().size();
		
	}
	
	/**
	 * Represents the average duration for the GA to complete across all experiment runs as a timestamp.
	 * @return Timestamp representing the average duration for the GA to complete across all experiment runs.
	 */
	public String getAverageDurationAllRunsToString() {
		
		return DurationFormatUtils.formatDuration(TimeUnit.NANOSECONDS.toMillis(getAverageDurationAllRuns()), "HH:mm:ss.SSS");
		
	}
	
	/**
	 * Returns a collection of all initial fittest candidates across all experiment runs.
	 * @return Collection of initial fittest candidates across all experiment runs.
	 */
	public ArrayList<TSPRoute> getInitialFittestCandidatesAllRuns() {
		
		ArrayList<TSPRoute> experimentOriginalFittestCandidates = new ArrayList<>();
		
		for (TSPExperimentResult currentResult: this.getExperimentResultsCollection()) {
			
			experimentOriginalFittestCandidates.add(currentResult.getOriginalFittestCandidate());
			
		} 
		
		return experimentOriginalFittestCandidates;
		
	}
	
	/**
	 * Calculates the average initial best solution distance across all experiment runs and generations.
	 * @return The average initial best solution distance across all experiment runs and generations.
	 */
	public int getAverageInitialDistanceAllRuns() {
		
		int experimentOverallAverageOriginalDistance = 0;
		
		for (TSPRoute originalFittestCandidate: getInitialFittestCandidatesAllRuns()) {
			
			experimentOverallAverageOriginalDistance += originalFittestCandidate.getRouteDistance();
			
		}
		
		return experimentOverallAverageOriginalDistance / getInitialFittestCandidatesAllRuns().size();
		
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

	public int getExperimentRuns() {
		return experimentRuns;
	}

	public void setExperimentRuns(int experimentRuns) {
		this.experimentRuns = experimentRuns;
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

	public TSPExperimentResult createNewExperimentResult() {
		return new TSPExperimentResult();
	}
	
	public ArrayList<TSPExperimentResult> getExperimentResultsCollection() {
		return experimentResults;
	}
	
	public void addExperimentResult(TSPExperimentResult newResult) {
		this.experimentResults.add(newResult);
	}
	
	public ArrayList<Integer> getGenerationsRangeCollection() {
		return (ArrayList<Integer>) IntStream.rangeClosed(1, this.getExperimentGenerations()).boxed().collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "TSPExperiment\n\nexperimentName=" + experimentName + "\n\ndate="
				+ new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(new Date())
				+ "\n\nexperimentGenerations=" + experimentGenerations 
				+ "\n\nnoOfLocations=" + noOfLocations + "\n\nmutationSettings="
				+ mutationSettings.toString() + "\n\ncrossoverSettings=" + crossoverSettings.toString()
				+ "\n\nselectionSettings=" + selectionSettings.toString() + "\n\npopulationSettings="
				+ populationSettings.toString();
	}
	
	/**
	 * Nested class representing the collection of results for a given TSPExperiment.
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 */
	public class TSPExperimentResult {

		private long experimentDuration;
		private String experimentDurationString;
		private double averageFitness = -1;
		private double bestFitness = -1;
		private int bestDistance = -1;
		
		private TSPRoute originalFittestCandidate = null;
		private TSPRoute currentFittestCandidate = null;

		private ArrayList<Integer> experimentBestDistances = new ArrayList<>();
		private ArrayList<Integer> experimentAverageDistances = new ArrayList<>();
		private ArrayList<Double> experimentBestFitnesses = new ArrayList<>();
		private ArrayList<Double> experimentAverageFitnesses = new ArrayList<>();
		
		public TSPExperimentResult() {
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
			return DurationFormatUtils.formatDuration(TimeUnit.NANOSECONDS.toMillis(experimentDuration), "HH:mm:ss.SSS");
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

		public ArrayList<Integer> getExperimentBestDistances() {
			return experimentBestDistances;
		}

		public void setExperimentBestDistances(ArrayList<Integer> experimentBestDistances) {
			this.experimentBestDistances = experimentBestDistances;
		}

		public ArrayList<Double> getExperimentBestFitnesses() {
			return experimentBestFitnesses;
		}

		public void setExperimentBestFitnesses(ArrayList<Double> experimentBestFitnesses) {
			this.experimentBestFitnesses = experimentBestFitnesses;
		}

		public ArrayList<Double> getExperimentAverageFitnesses() {
			return experimentAverageFitnesses;
		}

		public void setExperimentAverageFitnesses(ArrayList<Double> experimentAverageFitnesses) {
			this.experimentAverageFitnesses = experimentAverageFitnesses;
		}

		public ArrayList<Integer> getExperimentAverageDistances() {
			return experimentAverageDistances;
		}

		public void setExperimentAverageDistances(ArrayList<Integer> experimentAverageDistances) {
			this.experimentAverageDistances = experimentAverageDistances;
		}

		public TSPRoute getOriginalFittestCandidate() {
			return originalFittestCandidate;
		}

		public void setOriginalFittestCandidate(TSPRoute originalFittestCandidate) {
			this.originalFittestCandidate = originalFittestCandidate;
		}

		public TSPRoute getCurrentFittestCandidate() {
			return currentFittestCandidate;
		}

		public void setCurrentFittestCandidate(TSPRoute currentFittestCandidate) {
			this.currentFittestCandidate = currentFittestCandidate;
		}

	}
	
	/**
	 * Nested class representing the available settings for the GA mutation operator.
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 */
	public class TSPMutationSettings {
		
		// We only have one mutation operator, but include this property in case we add more in future.
		private String mutationMethod = "swap";
		
		private Double mutationRate;

		private TSPMutationSettings() {

		}
		
		public String getMutationMethod() {
			return mutationMethod;
		}

		public void setMutationMethod(String mutationMethod ) {
			this.mutationMethod = mutationMethod;
		}

		public void setMutationRate(Double mutationRate) {
			this.mutationRate = mutationRate;
		}

		public Double getMutationRate() {
			return mutationRate;
		}

		@Override
		public String toString() {
			return "TSPMutationSettings [ mutationMethod=" + mutationMethod + ", " + "mutationRate=" + mutationRate + " ]";
		}

	}
	
	/**
	 * Nested class representing the available settings for the GA crossover operator.
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 */
	public class TSPCrossoverSettings {
		
		// Default to Tournament selection if nothing else is specified.
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
	
	/**
	 * Nested class representing the available settings for the GA selection operator.
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 */
	public class TSPSelectionSettings {
		
		// Default to Tournament selection if nothing else is specified.
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
	
	/**
	 * Nested class representing the available settings for GA population initialisation.
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 */
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
