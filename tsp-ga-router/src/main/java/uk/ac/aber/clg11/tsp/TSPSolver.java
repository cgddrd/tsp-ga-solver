package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

import uk.ac.aber.clg11.tsp.TSPExperiment.TSPExperimentResults;
import uk.ac.aber.clg11.tsp.exception.TSPPlotterException;
import uk.ac.aber.clg11.tsp.utils.IOUtils;

public class TSPSolver {

	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();
	private IOUtils io = new IOUtils();

	public static void main(String[] args) throws Exception {

		TSPSolver tsp = new TSPSolver();
		tsp.runTSPGASolver(args);
		
	}

	private void setupCLIParser() {

		options.addOption(Option.builder("df").longOpt("datafile").required().hasArg().argName("dataFile")
				.desc("Filepath of input data file.").build());

		options.addOption(Option.builder("cf").longOpt("configfile").required().hasArg().argName("configFile")
				.desc("Filepath of the input configuration file.").build());

		options.addOption(Option.builder("e").longOpt("exportlocation").required().hasArg().argName("exportLocation")
				.desc("Filepath of the target export location.").build());

		options.addOption(new Option("help", "Print this help message."));

	}
	
	private void performTSPExperiment(TSPExperiment currentExperiment, ArrayList<TSPLocation> locations,
			String exportFileLocation) throws Exception {

		int noOfExperimentRuns = currentExperiment.getExperimentRuns();
		
		ArrayList<Integer> experimentInitialDistances = new ArrayList<>();
		ArrayList<Integer> experimentBestDistanceAverages = new ArrayList<>();
		ArrayList<Integer> experimentAverageDistanceAverages = new ArrayList<>();
		
		//ArrayList<TSPExperimentResults> experimentResults = new ArrayList<>();
		//ArrayList<Integer> generations = (ArrayList<Integer>) IntStream.rangeClosed(1, experimentSettings.getExperimentGenerations()).boxed().collect(Collectors.toList());
		
		System.out.println("------------------------------------------------------------------------");
		System.out.println("Conducting experiment: '" + currentExperiment.getExperimentName() + "' - Runs: " + currentExperiment.getExperimentRuns());
		
		for (int j = 0; j < noOfExperimentRuns; j++) {
			
			TSPExperimentResults currentExperimentResult = currentExperiment.getNewExperimentResult();
	
			TSPAlgorithm ga = new TSPAlgorithm(currentExperiment.getMutationSettings().getMutationRate(),
					currentExperiment.getCrossoverSettings().getCrossoverRate(),
					currentExperiment.getSelectionSettings().getSelectionMethod(),
					currentExperiment.getCrossoverSettings().getCrossoverMethod(),
					currentExperiment.getSelectionSettings().getTournamentSize(),
					currentExperiment.getSelectionSettings().getReturnSingleChild(),
					currentExperiment.getSelectionSettings().getUseElitism());

			TSPPopulation population = new TSPPopulation(currentExperiment.getPopulationSettings().getPopulationSize(),
					true, locations);
			
			TSPRoute originalFittestCandidate = (TSPRoute) population.getFittestCandidate();
			
			currentExperimentResult.setOriginalFittestCandidate(originalFittestCandidate);
			
			experimentInitialDistances.add(originalFittestCandidate.getRouteDistance());
			
			//currentExperiment = conductTimedExperimentRun(currentExperiment, ga, population);
			
			currentExperimentResult = conductTimedExperimentRun(currentExperiment, currentExperimentResult, ga, population);
			
			currentExperiment.addExperimentResult(currentExperimentResult);
			
			
			// When we are on the final run of the experiment, export the results to file.
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
		
		for (int i = 0; i < currentExperiment.getExperimentGenerations(); i++) {
			
			int experimentRunsBestDistanceAverage = 0;
			int experimentRunsAverageDistanceAverage = 0;
			
			int sumBestDistance = 0;
			int sumAverageDistance = 0;
			
			for (TSPExperimentResults test : currentExperiment.getExperimentResultsCollection()) {
			
				sumBestDistance += test.getExperimentBestDistances().get(i);
				sumAverageDistance += test.getExperimentAverageDistances().get(i);
				
			}
			
			experimentRunsBestDistanceAverage = sumBestDistance / noOfExperimentRuns;
			experimentBestDistanceAverages.add(experimentRunsBestDistanceAverage);
			
			experimentRunsAverageDistanceAverage = sumAverageDistance / noOfExperimentRuns;
			experimentAverageDistanceAverages.add(experimentRunsAverageDistanceAverage);
			
		}
		
		
		TSPPlotter frame3 = new TSPPlotter.TSPPlotterBuilder()
										  .setShowLegend(true)
										  .setDisplayGUI(false)
										  .setXAxisTitle("Generation")
										  .setLineColours(new Color[] {new Color(255, 0, 0), new Color(0,0,255)})
										  .setYAxisTitle("Distance (Fitness)")
										  .buildTSPPlotter();
		
		frame3.updatePlotData(new String[]{"Generations", "Best Distance", "Average Distance"}, currentExperiment.getGenerationsRangeCollection(), experimentBestDistanceAverages, experimentAverageDistanceAverages);

		frame3.generatePlot(true, false, String.format("TSP-GA: S:%s, C:%s (%.3f), M:%s (%.3f)", StringUtils.capitalize(currentExperiment.getSelectionSettings().getSelectionMethod()),
																								 StringUtils.capitalize(currentExperiment.getCrossoverSettings().getCrossoverMethod()), 
																							   	 currentExperiment.getCrossoverSettings().getCrossoverRate(),
																							   	 StringUtils.capitalize(currentExperiment.getMutationSettings().getMutationMethod()),
																							   	 currentExperiment.getMutationSettings().getMutationRate()));
		
		long experimentAverageDuration = 0;
		
		for (TSPExperimentResults test: currentExperiment.getExperimentResultsCollection()) {
			
			experimentAverageDuration += test.getExperimentDuration();
			
		}
		
		int experimentOverallAverageBestDistance = 0;
		
		for (Integer test: experimentBestDistanceAverages) {
			
			experimentOverallAverageBestDistance += test;
			
		}
		
		int experimentOverallAverageOriginalDistance = 0;
		
		for (Integer test: experimentInitialDistances) {
			
			experimentOverallAverageOriginalDistance += test;
			
		}
		
		frame3.exportToFile(FilenameUtils.concat(exportFileLocation, currentExperiment.getExperimentName()), "results.png");

		System.out.println("COMPLETED: " + currentExperiment.getExperimentName());
		System.out.println("Average Initial Distance: " + experimentOverallAverageOriginalDistance / noOfExperimentRuns);
		System.out.println("Average Best Distance: " + (experimentOverallAverageBestDistance / experimentBestDistanceAverages.size()));
		//System.out.println("Overall Best Distance: " + currentExperiment.getExperimentResults().getBestDistance());
		
		System.out.println("Overall Best Distance: " + currentExperiment.getBestOverallDistance());
		System.out.println("Overall Best Route: " + currentExperiment.getOverallFittestRoute().toString());
		
		//System.out.println("Overall Best Route: " + currentExperiment.getExperimentResults().getCurrentFittestCandidate().toString());
		
		
		System.out.println("Average Duration: " + DurationFormatUtils.formatDuration(TimeUnit.NANOSECONDS.toMillis(experimentAverageDuration / noOfExperimentRuns), "HH:mm:ss.SSS"));
		System.out.println("------------------------------------------------------------------------");
	}
	
	public TSPExperimentResults conductTimedExperimentRun(TSPExperiment currentExperiment, TSPExperimentResults currentExperimentResult, TSPAlgorithm ga, TSPPopulation population) throws Exception {
		
		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();

		population = ga.evolvePopulation(population);

		for (int i = 0; i < currentExperiment.getExperimentGenerations(); i++) {
			
			population = ga.evolvePopulation(population);

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
		
		stopWatch.stop();
	
		currentExperimentResult.setExperimentDuration(stopWatch.getNanoTime());

		return currentExperimentResult;
		
	}
	
	private void exportSolutionGraphicToFile(TSPRoute solutionToExport, String filePath, String fileName) throws TSPPlotterException {
		
		TSPPlotter plotter = new TSPPlotter.TSPPlotterBuilder().setDisplayGUI(false).setXAxisRangeSettings(0, 200).setYAxisRangeSettings(0, 200).buildTSPPlotter();
		plotter.plotTSPSolution(solutionToExport.getGenes());
		plotter.exportToFile(filePath, "Initial_Solution.png");
		
	}

	public void runTSPGASolver(String[] args) {

		setupCLIParser();

		try {

			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("TSP-GA-Router", options);

			} else {
				
				System.out.println("\nTSP-GA-Solver (Connor Goddard - clg11)");
				System.out.println("======================================");
				String configFilePath = line.getOptionValue("cf");
				String dataFilePath = line.getOptionValue("df");
				String exportLocation = line.getOptionValue("e");
				
				ArrayList<TSPExperiment> experiments = io.parseConfigFile(configFilePath);
				ArrayList<TSPLocation> locations = io.parseTSPLocationDataFile(dataFilePath);
				
				System.out.println("Config File: " + configFilePath);
				System.out.println("Data File: " + dataFilePath);
				System.out.println("Export Location: " + exportLocation);

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
}
