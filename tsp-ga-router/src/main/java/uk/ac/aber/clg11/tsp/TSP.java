package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;

import uk.ac.aber.clg11.tsp.utils.IOUtils;

public class TSP {

	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();
	private IOUtils io = new IOUtils();
	private StopWatch stopWatch = new StopWatch();

	public static void main(String[] args) throws Exception {

		TSP tsp = new TSP();
		tsp.runTSPGASolver(args);

		// ArrayList<Integer> parent1 = new
		// ArrayList<>(Arrays.asList(8,4,7,3,6,2,5,1,9,0));
		// ArrayList<Integer> parent2 = new
		// ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));

		// tsp.performCycleCrossover(parent1, parent2);

	}

	private void setupCLIParser() {

		options.addOption(Option.builder("df").longOpt("datafile").hasArg().argName("dataFile")
				.desc("Filepath of input data file.").build());

		options.addOption(Option.builder("cf").longOpt("configfile").hasArg().argName("configFile")
				.desc("Filepath of the input configuration file.").build());

		options.addOption(Option.builder("e").longOpt("exportlocation").hasArg().argName("exportLocation")
				.desc("Filepath of the target export location.").build());

		options.addOption(new Option("help", "Print this help message."));

	}

	private void performTSPExperiment(TSPExperiment experimentSettings, ArrayList<TSPLocation> locations,
			String exportFileLocation) throws Exception {

		TSPPlotter plotter = new TSPPlotter.TSPPlotterBuilder().setDisplayGUI(false).setAxisMaxRangeSettings(200, 200)
				.buildTSPPlotter();

		ArrayList<Double> generationFitnesses = new ArrayList<>();
		ArrayList<Integer> generationDistances = new ArrayList<>();
		ArrayList<Double> generationAverages = new ArrayList<>();
		ArrayList<Integer> generations = new ArrayList<>();

		TSPAlgorithm ga = new TSPAlgorithm(experimentSettings.getMutationSettings().getMutationRate(),
				experimentSettings.getCrossoverSettings().getCrossoverRate(),
				experimentSettings.getSelectionSettings().getSelectionMethod(),
				experimentSettings.getCrossoverSettings().getCrossoverMethod(),
				experimentSettings.getSelectionSettings().getTournamentSize(),
				experimentSettings.getSelectionSettings().getReturnSingleChild(),
				experimentSettings.getSelectionSettings().getUseElitism());

		TSPPopulation population = new TSPPopulation(experimentSettings.getPopulationSettings().getPopulationSize(),
				true, locations);
		
		TSPRoute fittest = (TSPRoute) population.getFittestCandidate();
		System.out.println("Initial Distance: " + fittest.getRouteDistance());

		// Plot the initial best TSP solution.
		plotter.updateData(population.getFittestCandidate().getGenes());
		plotter.generatePlot();

		String experimentFolderName = experimentSettings.getExperimentName();

		plotter.exportToFile(FilenameUtils.concat(exportFileLocation, experimentFolderName), "start.png");
		
		stopWatch.start();
		
		population = ga.evolvePopulation(population);

		for (int i = 0; i < experimentSettings.getExperimentGenerations(); i++) {

			population = ga.evolvePopulation(population);

			generationAverages.add(population.getPopulationFitnessAverage());
			
			TSPRoute test = (TSPRoute) population.getFittestCandidate();
			generationDistances.add(test.getRouteDistance());
			generationFitnesses.add(test.getFitness());
			generations.add(i + 1);
		
			experimentSettings.getExperimentResults().updateAverageFitness(population.getPopulationFitnessAverage());
			experimentSettings.getExperimentResults().updateBestFitness(test.getFitness());
			experimentSettings.getExperimentResults().updateBestDistance(test.getRouteDistance());

		}
		
		fittest = (TSPRoute) population.getFittestCandidate();
		System.out.println("Final Distance: " + fittest.getRouteDistance());
		
		stopWatch.stop();
		
		experimentSettings.getExperimentResults().setExperimentDuration(stopWatch.getNanoTime());
		experimentSettings.getExperimentResults().setExperimentDurationString(stopWatch.toString());
		
		stopWatch.reset();
		
		TSPPlotter frame3 = new TSPPlotter.TSPPlotterBuilder().setDisplayGUI(false).setLineColour(new Color(255, 0, 0))
				.buildTSPPlotter();

		frame3.updateData(generations, generationAverages);

		frame3.generatePlot(true, false);

		frame3.exportToFile(FilenameUtils.concat(exportFileLocation, experimentFolderName), "results.png");

		// Plot the final best TSP solution.
		plotter.updateData(population.getFittestCandidate().getGenes());
		plotter.generatePlot();
		plotter.exportToFile(FilenameUtils.concat(exportFileLocation, experimentFolderName), "end.png");

		io.exportExperimentParametersToFile(experimentSettings,
				FilenameUtils.concat(exportFileLocation, experimentFolderName), "experiment.txt");

	}

	public void runTSPGASolver(String[] args) {

		setupCLIParser();

		try {

			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help") /*
										 * || (!line.hasOption("datafile") ||
										 * !line.hasOption("configfile") ||
										 * !line.hasOption("exportlocation"))
										 */) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("TSEGenerator", options);

			} else {

				String configFilePath = line.getOptionValue("cf");
				String dataFilePath = line.getOptionValue("df");
				String exportLocation = line.getOptionValue("e");

				ArrayList<TSPExperiment> experiments = io.parseConfigFile(configFilePath);

				ArrayList<TSPLocation> locations = io.parseTSPLocationDataFile(dataFilePath);

				for (TSPExperiment experiment : experiments) {

					experiment.setNoOfLocations(locations.size());
					performTSPExperiment(experiment, locations, exportLocation);

				}

			}

		} catch (ParseException exp) {
			System.err.println("Error: Parsing failed - Reason: " + exp.getMessage());
		} catch (NumberFormatException nFEx) {
			System.err.println("Error: Unable to parse integer CLI argument.");
			nFEx.printStackTrace();
		} catch (Exception ex) {
			System.err.println("Error occured while running TSP generator.");
			ex.printStackTrace();
		}

	}

	public ArrayList<Integer> performCycleCrossover(ArrayList<Integer> parent1, ArrayList<Integer> parent2)
			throws Exception {

		if (parent1.size() == parent2.size()) {

			int AlleleCount = parent1.size();

			boolean[] flags = new boolean[AlleleCount];

			Double_Index_Value_Pair TempPair = new Double_Index_Value_Pair();

			System.out.println("Parent 1: " + parent1.toString());
			System.out.println("Parent 2: " + parent2.toString());

			// 1. Generate a hashtable for parent 2's index.
			Hashtable HT1 = new Hashtable();

			for (int i = 0; i < AlleleCount; i++) {

				TempPair = new Double_Index_Value_Pair();
				TempPair.value2 = parent2.get(i);
				TempPair.index2 = i;
				TempPair.value1 = parent1.get(i);
				HT1.put(parent1.get(i), TempPair);

			}

			// 2. Generate the cycles.

			ArrayList<ArrayList<Double_Index_Value_Pair>> cycles = new ArrayList<>();

			int cycleStart = 0;

			for (int i = 0; i < AlleleCount; i++) {

				ArrayList<Double_Index_Value_Pair> tempCycle = new ArrayList<>();

				if (!flags[i]) {

					cycleStart = i;

					// Look for the VALUE of the current element from Parent 2
					// inside Parent 1 via the hashtable lookup.
					TempPair = (Double_Index_Value_Pair) HT1.get(parent2.get(i));

					// Add this to the array of values for this particular
					// cycle, and make sure that we cannot add it to a future
					// cycle.
					tempCycle.add(TempPair);
					flags[TempPair.index2] = true;

					// The current cycle should continue UNTIL we return back to
					// the ORIGINAL INDEX that the cycle started at.
					while (TempPair.index2 != cycleStart) {

						// Follow the cycle along to the next element by finding
						// the VALUE of the next element in the chain from
						// Parent 2 inside Parent 1.
						TempPair = (Double_Index_Value_Pair) HT1.get(parent2.get(TempPair.index2));

						// Add this to the array of values for this particular
						// cycle, and make sure that we cannot add it to a
						// future cycle.
						tempCycle.add(TempPair);
						flags[TempPair.index2] = true;

					}

					cycles.add(tempCycle);
				}

			}

			// 3. Copy alternate cycles to children

			int[] child1 = new int[AlleleCount];
			int[] child2 = new int[AlleleCount];

			int counter = 0;

			for (ArrayList<Double_Index_Value_Pair> c : cycles) {

				for (Double_Index_Value_Pair tempPair : c) {

					if (counter % 2 == 0) {

						child1[tempPair.index2] = tempPair.value1;
						child2[tempPair.index2] = tempPair.value2;

					} else {

						child1[tempPair.index2] = tempPair.value2;
						child2[tempPair.index2] = tempPair.value1;

					}
				}

				counter++;
			}

			ArrayList<Integer> children = new ArrayList<>();

			System.out.println("Child 1: " + Arrays.toString(child1));
			System.out.println("Child 2: " + Arrays.toString(child2));

			return children;

		}

		throw new Exception("oh dear");

	}

	public class Double_Index_Value_Pair {

		private int value2;

		public int getValue2() {
			return value2;
		}

		public void setValue2(int value2) {
			this.value2 = value2;
		}

		public int getIndex2() {
			return index2;
		}

		public void setIndex2(int index2) {
			this.index2 = index2;
		}

		public int getValue1() {
			return value1;
		}

		public void setValue1(int value1) {
			this.value1 = value1;
		}

		private int index2;
		private int value1;

		public Double_Index_Value_Pair() {

		}
	}
}
