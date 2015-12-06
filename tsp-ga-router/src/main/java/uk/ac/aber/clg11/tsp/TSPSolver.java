package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import uk.ac.aber.clg11.tsp.TSPExperiment.TSPExperimentResult;
import uk.ac.aber.clg11.tsp.exception.TSPPlotterException;
import uk.ac.aber.clg11.tsp.utils.IOUtils;

/**
 * Provides overall setup and initiation of TSP-GA experiments. Contains the bootstrap 'main' method.
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 */
public class TSPSolver {

	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();
	private IOUtils io = new IOUtils();

	public static void main(String[] args) throws Exception {
		
		// Initiate the running of TSP-GA experiments.
		TSPSolver tsp = new TSPSolver();
		tsp.runTSPGASolver(args);
		
	}
	
	/**
	 * Bootstraps the main application process, loading-in all required configuration and data files prior to initiating experiments.
	 * @param args CLI arguments to be parsed via Apache Commons CLI parser.
	 */
	public void runTSPGASolver(String[] args) {
			
		// Grab all of the CLI configuration parameters.
		setupCLIParser();

		try {

			// Parse the CLI arguments.
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("TSP-GA-Router", options);

			} else {
				
				System.out.println("\nTSP-GA-Solver (Connor Goddard - clg11)");
				System.out.println("======================================");
				
				// Extract the settings from the CLI parser.
				String configFilePath = line.getOptionValue("cf");
				String dataFilePath = line.getOptionValue("df");
				String exportLocation = line.getOptionValue("e");
				
				// Load-in the XML configuration file.
				ArrayList<TSPExperiment> experiments = io.parseConfigFile(configFilePath);
				
				// Load-in the CSV data file.
				ArrayList<TSPLocation> locations = io.parseTSPLocationDataFile(dataFilePath);
				
				System.out.println("Config File: " + configFilePath);
				System.out.println("Data File: " + dataFilePath);
				System.out.println("Export Location: " + exportLocation);
				
				
				// Perform each experiment specified via the XML config file sequentially (saving the results for each)
				for (TSPExperiment experiment : experiments) {

					experiment.setNoOfLocations(locations.size());
					performTSPExperiment(experiment, locations, exportLocation);

				}

			}

		} catch (ParseException exp) {
			System.err.println("Error whilst parsing CLI options for TSP-GA-Router: " + exp.getMessage());
		} catch (NumberFormatException nFEx) {
			System.err.println("Error: Unable to parse integer CLI argument.");
			nFEx.printStackTrace();
		} catch (Exception ex) {
			System.err.println("Error occured while running TSP-GA-Router.");
			ex.printStackTrace();
		}

	}
	
	/**
	 * Sets up the Apache Commons CLI parser with the expected input paramaters.
	 */
	private void setupCLIParser() {

		options.addOption(Option.builder("df").longOpt("datafile").required().hasArg().argName("dataFile")
				.desc("Filepath of input data file.").build());

		options.addOption(Option.builder("cf").longOpt("configfile").required().hasArg().argName("configFile")
				.desc("Filepath of the input configuration file.").build());

		options.addOption(Option.builder("e").longOpt("exportlocation").required().hasArg().argName("exportLocation")
				.desc("Filepath of the target export location.").build());

		options.addOption(new Option("help", "Print this help message."));

	}
	
	/**
	 * Responsible for performing an individual TSP-GA experiment. Manages the entire lifecycle of an individual experiment.
	 * Repeats the same experiment for the number of experiment runs specified in the XML configuration file (defaults to 1).
	 * @param currentExperiment Settings for the current experiment.
	 * @param locations Collection of TSP locations to test the experiment GA parameters.
	 * @param exportFileLocation File path of the location specified to save experiment results to. 
	 * @throws Exception
	 */
	private void performTSPExperiment(TSPExperiment currentExperiment, ArrayList<TSPLocation> locations,
			String exportFileLocation) throws Exception {

		int noOfExperimentRuns = currentExperiment.getExperimentRuns();

		System.out.println("------------------------------------------------------------------------");
		System.out.println("Conducting experiment: '" + currentExperiment.getExperimentName() + "' - Runs: " + currentExperiment.getExperimentRuns());
		
		// Repeat the same experiment for the number of runs specified in the XML configuration file (defaults to 1).
		for (int j = 0; j < noOfExperimentRuns; j++) {
			
			// For each run, we need to create a new results container, and a new instance of the GA implementation itself.
			TSPExperimentResult currentExperimentResult = currentExperiment.createNewExperimentResult();
			
			// Set up the new instance of the GA implementation using the specified experiment settings.
			TSPAlgorithm ga = new TSPAlgorithm(currentExperiment.getMutationSettings().getMutationRate(),
					currentExperiment.getCrossoverSettings().getCrossoverRate(),
					currentExperiment.getSelectionSettings().getSelectionMethod(),
					currentExperiment.getCrossoverSettings().getCrossoverMethod(),
					currentExperiment.getSelectionSettings().getTournamentSize(),
					currentExperiment.getSelectionSettings().getReturnSingleChild(),
					currentExperiment.getSelectionSettings().getUseElitism());
			
			
			// 1. SETUP AND PERFORM THE GA TEST RUN.
			currentExperimentResult = conductTimedGARun(currentExperiment, currentExperimentResult, ga, locations);
			
			// 2. SAVE THE RESULTS OF THIS INDIVIDUAL EXPERIMENT RUN TO FILE.
			currentExperiment.addExperimentResult(currentExperimentResult);
			
			// 3. WHEN ON THE FINAL RUN, EXPORT THE RESULTS TO FILE.
			if (j == (noOfExperimentRuns - 1)) {
				
				// Plot the initial best TSP solution.
				this.exportSolutionGraphicToFile(currentExperimentResult.getOriginalFittestCandidate(), 
						FilenameUtils.concat(exportFileLocation, currentExperiment.getExperimentName()), "Initial_Solution.png");
				
				// Plot the final best TSP solution.
				this.exportSolutionGraphicToFile(currentExperimentResult.getCurrentFittestCandidate(), 
						FilenameUtils.concat(exportFileLocation, currentExperiment.getExperimentName()), "Final_Solution.png");

				io.exportExperimentParametersToFile(currentExperiment, FilenameUtils.concat(exportFileLocation, currentExperiment.getExperimentName()), "Experiment_Results.txt");
				
			}

		}
		
		// 4. ONCE ALL RUNS HAVE BEEN COMPLETED, EXPORT THE AVERAGES ACROSS ALL OF THE EXPERIMENT RUNS.
		exportExperimentResultAverages(currentExperiment, exportFileLocation);
		
		System.out.println("COMPLETED: " + currentExperiment.getExperimentName());
		System.out.println("Average Initial Distance: " + currentExperiment.getAverageInitialDistanceAllRuns());
		System.out.println("Average Best Distance: " + currentExperiment.getAverageBestDistanceAllRuns());
		System.out.println("Overall Best Distance: " + currentExperiment.getBestDistanceAllRuns());
		System.out.println("Overall Best Route: " + currentExperiment.getFittestRouteAllRuns().toString());
		System.out.println("Average Duration: " + currentExperiment.getAverageDurationToString());
		System.out.println("------------------------------------------------------------------------");
		
	}
	
	
	/**
	 * Performs an individual run of a given experiment, timing the total duration for the GA to reach the specified number of generations.
	 *
	 * Modified from original source: http://www.theprojectspot.com/tutorial-post/applying-a-genetic-algorithm-to-the-travelling-salesman-problem/5
	 *
	 * @param currentExperiment The current experiment under testing.
	 * @param currentExperimentResult The collection of results for the current experiment.
	 * @param ga The instance of the GA currently active.
	 * @param locations The collection of TSPLocations read in from the CSV data file.
	 * @return Updated collection of experiment results (now including timing data for the GA).
	 * @throws Exception
	 */
	public TSPExperimentResult conductTimedGARun(TSPExperiment currentExperiment, TSPExperimentResult currentExperimentResult, TSPAlgorithm ga, ArrayList<TSPLocation> locations) throws Exception {
		
		// Begin timing of the test run.
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		// 1. INITIALISE THE INITIAL POPULATION.
		TSPPopulation population = new TSPPopulation(currentExperiment.getPopulationSettings().getPopulationSize(),
				true, locations);
		
		currentExperimentResult.setOriginalFittestCandidate((TSPRoute) population.getFittestCandidate());

		population = ga.evolvePopulation(population);

		// 2. EVOLVE THE POPULATION OVER THE SPECIFIED NUMBER OF GENERATIONS.
		for (int i = 0; i < currentExperiment.getExperimentGenerations(); i++) {
			
			population = ga.evolvePopulation(population);
			
			// After each generation, update the experiment results.
			TSPRoute fittestCandiate = (TSPRoute) population.getFittestCandidate();
			
			currentExperimentResult.setCurrentFittestCandidate(fittestCandiate);
			currentExperimentResult.updateAverageFitness(population.getPopulationFitnessAverage());
			currentExperimentResult.updateBestFitness(fittestCandiate.getFitness());
			currentExperimentResult.updateBestDistance(fittestCandiate.getRouteDistance());
			
			currentExperimentResult.getExperimentBestDistances().add(fittestCandiate.getRouteDistance());
			currentExperimentResult.getExperimentAverageDistances().add(population.getPopulationDistanceAverage());
			currentExperimentResult.getExperimentAverageFitnesses().add(population.getPopulationFitnessAverage());
			currentExperimentResult.getExperimentBestFitnesses().add(population.getPopulationFitnessMax());
		}
		
		// 3. STOP TIMING ONCE GA HAS COMPLETED.
		stopWatch.stop();
	
		currentExperimentResult.setExperimentDuration(stopWatch.getNanoTime());

		return currentExperimentResult;
		
	}
	
	/**
	 * Responsible for exporting the result averages calculated across all of the specified runs conducted for an individual experiment.
	 * @param currentExperiment The experiment to export averages for.
	 * @param exportFileLocation The file path specifying the location to save exported results to.
	 * @throws TSPPlotterException
	 */
	public void exportExperimentResultAverages(TSPExperiment currentExperiment, String exportFileLocation) throws TSPPlotterException {
		
		TSPPlotter exportPlotter = new TSPPlotter.TSPPlotterBuilder()
				  .setShowLegend(true)
				  .setDisplayGUI(false)
				  .setXAxisTitle("Generation")
				  .setLineColours(new Color[] {new Color(255, 0, 0), new Color(0,0,255)})
				  .setYAxisTitle("Distance (Fitness)")
				  .buildTSPPlotter();
		
		// Generate a plot of the best and average distances calculated across all of the experiment runs.
		exportPlotter.updatePlotData(new String[]{"Generations", "Best Distance", "Average Distance"}, 
							currentExperiment.getGenerationsRangeCollection(), 
							currentExperiment.getBestDistanceAveragePerGeneration(), 
							currentExperiment.getAverageDistanceAveragePerGeneration());


		exportPlotter.generatePlot(true, false, String.format("TSP-GA: S:%s, C:%s (%.3f), M:%s (%.3f)", StringUtils.capitalize(currentExperiment.getSelectionSettings().getSelectionMethod()),
																		 StringUtils.capitalize(currentExperiment.getCrossoverSettings().getCrossoverMethod()), 
																	   	 currentExperiment.getCrossoverSettings().getCrossoverRate(),
																	   	 StringUtils.capitalize(currentExperiment.getMutationSettings().getMutationMethod()),
																	   	 currentExperiment.getMutationSettings().getMutationRate()));

		exportPlotter.exportToFile(FilenameUtils.concat(exportFileLocation, currentExperiment.getExperimentName()), "results.png");

	}
	
	/**
	 * Plots the specified TSPRoute before exporting to an image file.
	 * @param solutionToExport The TSPRoute instance to plot and export.
	 * @param filePath The location to save the image file.
	 * @param fileName The name of the new image file.
	 * @throws TSPPlotterException
	 */
	private void exportSolutionGraphicToFile(TSPRoute solutionToExport, String filePath, String fileName) throws TSPPlotterException {
		
		TSPPlotter plotter = new TSPPlotter.TSPPlotterBuilder().setDisplayGUI(false).setXAxisRangeSettings(0, 200).setYAxisRangeSettings(0, 200).buildTSPPlotter();
		plotter.plotTSPSolution(solutionToExport.getGenes());
		plotter.exportToFile(filePath, fileName);
		
	}

}
