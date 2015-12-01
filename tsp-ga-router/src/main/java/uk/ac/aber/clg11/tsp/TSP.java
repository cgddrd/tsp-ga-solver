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

	public static void main(String[] args) throws Exception {

		TSP tsp = new TSP();
		tsp.runTSPGASolver(args);

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

		TSPPopulation population = new TSPPopulation(100, locations, true);
		
		System.out.println("Initial distance:" + population.getFittestCandidate().getRouteDistance());
		
		population = TSPAlgorithm2.evolvePopulation(population);

		for (int i = 0; i < 100; i++) {
			population = TSPAlgorithm2.evolvePopulation(population);
		}
		
		System.out.println("Final distance:" + population.getFittestCandidate().getRouteDistance());
		System.out.println(population.getFittestCandidate().toString());

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
}
