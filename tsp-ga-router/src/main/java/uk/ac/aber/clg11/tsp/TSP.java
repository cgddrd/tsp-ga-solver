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
		//tsp.runTSPGASolver(args);
		
		ArrayList<TSPLocation> test = new ArrayList<>();
		
		TSPLocation city = new TSPLocation(60, 200);
        test.add(city);
        TSPLocation city2 = new TSPLocation(180, 200);
        test.add(city2);
        TSPLocation city3 = new TSPLocation(80, 180);
        test.add(city3);
        TSPLocation city4 = new TSPLocation(140, 180);
        test.add(city4);
        TSPLocation city5 = new TSPLocation(20, 160);
        test.add(city5);
        TSPLocation city6 = new TSPLocation(100, 160);
        test.add(city6);
        TSPLocation city7 = new TSPLocation(200, 160);
        test.add(city7);
        TSPLocation city8 = new TSPLocation(140, 140);
        test.add(city8);
        TSPLocation city9 = new TSPLocation(40, 120);
        test.add(city9);
        TSPLocation city10 = new TSPLocation(100, 120);
        test.add(city10);
        TSPLocation city11 = new TSPLocation(180, 100);
        test.add(city11);
        TSPLocation city12 = new TSPLocation(60, 80);
        test.add(city12);
        TSPLocation city13 = new TSPLocation(120, 80);
        test.add(city13);
        TSPLocation city14 = new TSPLocation(180, 60);
        test.add(city14);
        TSPLocation city15 = new TSPLocation(20, 40);
        test.add(city15);
        TSPLocation city16 = new TSPLocation(100, 40);
        test.add(city16);
        TSPLocation city17 = new TSPLocation(200, 40);
        test.add(city17);
        TSPLocation city18 = new TSPLocation(20, 20);
        test.add(city18);
        TSPLocation city19 = new TSPLocation(60, 20);
        test.add(city19);
        TSPLocation city20 = new TSPLocation(160, 20);
        test.add(city20);
        
        tsp.performTSPExperiment(null, test, null);
        

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

	public void performTSPExperiment(TSPExperiment experimentSettings, ArrayList<TSPLocation> locations,
			String exportFileLocation) throws Exception {

		TSPPopulation population = new TSPPopulation(50, locations, true);
		
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
