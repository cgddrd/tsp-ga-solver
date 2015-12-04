package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

import uk.ac.aber.clg11.tsp.TSPExperiment.TSPExperimentResults;
import uk.ac.aber.clg11.tsp.utils.IOUtils;

public class TSP {

	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();
	private IOUtils io = new IOUtils();

	public static void main(String[] args) throws Exception {

		TSP tsp = new TSP();
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

	private void performTSPExperiment(TSPExperiment experimentSettings, ArrayList<TSPLocation> locations,
			String exportFileLocation) throws Exception {

		int noOfExperimentRuns = experimentSettings.getExperimentRuns();
		
		ArrayList<Double> experimentBestDistanceAverages = new ArrayList<>();
		ArrayList<Double> experimentAverageDistanceAverages = new ArrayList<>();
		ArrayList<Double> experimentAverageFitnessAverages = new ArrayList<>();
		ArrayList<Double> experimentBestFitnessAverages = new ArrayList<>();
		
		ArrayList<TSPExperimentResults> experimentsResults = new ArrayList<>();
		ArrayList<Integer> generations = (ArrayList<Integer>) IntStream.rangeClosed(1, experimentSettings.getExperimentGenerations()).boxed().collect(Collectors.toList());
		
		TSPPlotter plotter = null;
		
		System.out.println("------------------------------------------------------------------------");
		System.out.println("Conducting experiment: '" + experimentSettings.getExperimentName() + "' - Runs: " + experimentSettings.getExperimentRuns());
		
		for (int j = 0; j < noOfExperimentRuns; j++) {
	
			TSPAlgorithm ga = new TSPAlgorithm(experimentSettings.getMutationSettings().getMutationRate(),
					experimentSettings.getCrossoverSettings().getCrossoverRate(),
					experimentSettings.getSelectionSettings().getSelectionMethod(),
					experimentSettings.getCrossoverSettings().getCrossoverMethod(),
					experimentSettings.getSelectionSettings().getTournamentSize(),
					experimentSettings.getSelectionSettings().getReturnSingleChild(),
					experimentSettings.getSelectionSettings().getUseElitism());

			TSPPopulation population = new TSPPopulation(experimentSettings.getPopulationSettings().getPopulationSize(),
					true, locations);
			
			
			if (j == (noOfExperimentRuns - 1)) {
				
				TSPRoute originalFittestCandidate = (TSPRoute) population.getFittestCandidate();
				
				experimentSettings.getExperimentResults().setOriginalFittestCandidate(originalFittestCandidate);
				
				// Plot the initial best TSP solution.
				plotter = new TSPPlotter.TSPPlotterBuilder().setDisplayGUI(false).setAxisMaxRangeSettings(200, 200).buildTSPPlotter();
				
				plotter.updateData(originalFittestCandidate.getGenes());
				plotter.generatePlot();
				plotter.exportToFile(FilenameUtils.concat(exportFileLocation, experimentSettings.getExperimentName()), "start.png");
				
			}
			
			
			experimentSettings = conductTimedExperiment(experimentSettings, ga, population);
			experimentsResults.add(experimentSettings.getExperimentResults());
			
			
			if (j == (noOfExperimentRuns - 1)) {
				
				// Plot the final best TSP solution.
				plotter.updateData(experimentSettings.getExperimentResults().getCurrentFittestCandidate().getGenes());
				plotter.generatePlot();
				plotter.exportToFile(FilenameUtils.concat(exportFileLocation, experimentSettings.getExperimentName()), "end.png");

				io.exportExperimentParametersToFile(experimentSettings, FilenameUtils.concat(exportFileLocation, experimentSettings.getExperimentName()), "experiment.txt");
				
			}

		}
		
		for (int i = 0; i < generations.size(); i++) {
			
			double experimentRunsBestDistanceAverage = 0.0;
			double experimentRunsAverageDistanceAverage = 0.0;
			double experimentRunsAverageFitnessAverage = 0.0;
			double experimentRunsBestFitnessAverage = 0.0;
			
			int sumBestDistance = 0;
			int sumAverageDistance = 0;
			double sumAverageFitness = 0;
			double sumBestFitness = 0;
			
			for (TSPExperimentResults test : experimentsResults) {
			
				sumBestDistance += test.getExperimentBestDistances().get(i);
				sumAverageDistance += test.getExperimentAverageDistances().get(i);
				sumBestFitness += test.getExperimentBestFitnesses().get(i);
				sumAverageFitness += test.getExperimentAverageFitnesses().get(i);
				
			}
			
			experimentRunsBestDistanceAverage = (double) sumBestDistance / noOfExperimentRuns;
			experimentBestDistanceAverages.add(experimentRunsBestDistanceAverage);
			
			experimentRunsAverageDistanceAverage = (double) sumAverageDistance / noOfExperimentRuns;
			experimentAverageDistanceAverages.add(experimentRunsAverageDistanceAverage);
			
			experimentRunsBestFitnessAverage = (double) sumBestFitness / noOfExperimentRuns;
			experimentBestFitnessAverages.add(experimentRunsBestFitnessAverage);
			
			experimentRunsAverageFitnessAverage = (double) sumAverageFitness / noOfExperimentRuns;
			experimentAverageFitnessAverages.add(experimentRunsAverageFitnessAverage);
			
		}
		
		
		TSPPlotter frame3 = new TSPPlotter.TSPPlotterBuilder()
										  .setShowLegend(true)
										  .setDisplayGUI(false)
										  .setXAxisTitle("Generation")
										  .setYAxisTitle("Distance (Fitness)")
										  .setLineColour(new Color(255, 0, 0))
										  .buildTSPPlotter();
		
		frame3.updateData(Double.class, new String[]{"Generations", "Best Distance", "Average Distance"}, generations, experimentBestDistanceAverages, experimentAverageDistanceAverages);

		frame3.generatePlot(true, false, String.format("TSP-GA: S:%s, C:%s (%.3f), M:%s (%.3f)", StringUtils.capitalize(experimentSettings.getSelectionSettings().getSelectionMethod()),
																								 StringUtils.capitalize(experimentSettings.getCrossoverSettings().getCrossoverMethod()), 
																							   	 experimentSettings.getCrossoverSettings().getCrossoverRate(),
																							   	 StringUtils.capitalize(experimentSettings.getMutationSettings().getMutationMethod()),
																							     experimentSettings.getMutationSettings().getMutationRate()));

		frame3.exportToFile(FilenameUtils.concat(exportFileLocation, experimentSettings.getExperimentName()), "results.png");

		System.out.println("COMPLETED: " + experimentSettings.getExperimentName());
		System.out.println("Initial Distance: " + experimentSettings.getExperimentResults().getOriginalFittestCandidate().getRouteDistance());
		System.out.println("Best Distance: " + experimentSettings.getExperimentResults().getBestDistance());
		System.out.println("Best Route: " + experimentSettings.getExperimentResults().getCurrentFittestCandidate().toString());
		System.out.println("Average Duration: " + experimentSettings.getExperimentResults().getExperimentDurationString());
		System.out.println("------------------------------------------------------------------------");
	}
	
	public TSPExperiment conductTimedExperiment(TSPExperiment experimentSettings, TSPAlgorithm ga, TSPPopulation population) throws Exception {
		
		StopWatch stopWatch = new StopWatch();
		
		stopWatch.start();

		population = ga.evolvePopulation(population);

		for (int i = 0; i < experimentSettings.getExperimentGenerations(); i++) {
			
			population = ga.evolvePopulation(population);

			TSPRoute fittestCandiate = (TSPRoute) population.getFittestCandidate();
			
			experimentSettings.getExperimentResults().setCurrentFittestCandidate(fittestCandiate);
			experimentSettings.getExperimentResults().updateAverageFitness(population.getPopulationFitnessAverage());
			experimentSettings.getExperimentResults().updateBestFitness(fittestCandiate.getFitness());
			experimentSettings.getExperimentResults().updateBestDistance(fittestCandiate.getRouteDistance());
			
			experimentSettings.getExperimentResults().getExperimentBestDistances().add(fittestCandiate.getRouteDistance());
			experimentSettings.getExperimentResults().getExperimentAverageDistances().add(population.getPopulationDistanceAverage());
			experimentSettings.getExperimentResults().getExperimentAverageFitnesses().add(population.getPopulationFitnessAverage());
			experimentSettings.getExperimentResults().getExperimentBestFitnesses().add(population.getPopulationFitnessMax());
		}
		
		stopWatch.stop();
		
		experimentSettings.getExperimentResults().setExperimentDuration(stopWatch.getNanoTime());
		experimentSettings.getExperimentResults().setExperimentDurationString(stopWatch.toString());

		return experimentSettings;
		
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
