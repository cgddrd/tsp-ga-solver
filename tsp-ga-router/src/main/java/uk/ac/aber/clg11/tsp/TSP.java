package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;

import uk.ac.aber.clg11.tsp.utils.IOUtils;

public class TSP {
	
	private Options options = new Options();
	private CommandLineParser parser = new DefaultParser();

	public static void main(String[] args) throws Exception {
		
		
		
//		ArrayList<TSPLocation> cities = new ArrayList<TSPLocation>();
//        
//		TSPLocation city = new TSPLocation(60, 200);
//		cities.add(city);
//        TSPLocation city2 = new TSPLocation(180, 200);
//        cities.add(city2);
//        TSPLocation city3 = new TSPLocation(80, 40);
//        cities.add(city3);
//        TSPLocation city4 = new TSPLocation(140, 180);
//        cities.add(city4);
//        TSPLocation city5 = new TSPLocation(20, 80);
//        cities.add(city5);
//        TSPLocation city6 = new TSPLocation(100, 160);
//        cities.add(city6);
//        TSPLocation city7 = new TSPLocation(200, 160);
//        cities.add(city7);
//        TSPLocation city8 = new TSPLocation(140, 140);
//        cities.add(city8);
//        TSPLocation city9 = new TSPLocation(40, 120);
//        cities.add(city9);
//        TSPLocation city10 = new TSPLocation(100, 120);
//        cities.add(city10);
//        TSPLocation city11 = new TSPLocation(180, 100);
//        cities.add(city11);
//        TSPLocation city12 = new TSPLocation(60, 80);
//        cities.add(city12);
//        TSPLocation city13 = new TSPLocation(120, 80);
//        cities.add(city13);
//        TSPLocation city14 = new TSPLocation(180, 60);
//        cities.add(city14);
//        TSPLocation city15 = new TSPLocation(20, 40);
//        cities.add(city15);
//        TSPLocation city16 = new TSPLocation(100, 40);
//        cities.add(city16);
//        TSPLocation city17 = new TSPLocation(200, 40);
//        cities.add(city17);
//        TSPLocation city18 = new TSPLocation(20, 20);
//        cities.add(city18);
//        TSPLocation city19 = new TSPLocation(60, 20);
//        cities.add(city19);
//        TSPLocation city20 = new TSPLocation(160, 20);
//        cities.add(city20);
//  
//        TSPAlgorithm ga = new TSPAlgorithm(0.015, 0.95, 10, false, true);
//        
//        TSPPlotter frame = new TSPPlotter.TSPPlotterBuilder().setAxisMaxRangeSettings(200, 200).buildTSPPlotter();
//        
//        frame.setTitle("start");
//        
//        TSPPopulation pop2 = new TSPPopulation(50, true, cities);
//        
//        System.out.println("\n");
//        
//        System.out.println("Initial fitness2: " + pop2.getFittestCandidate().getFitness());
//        
//        frame.updateData(pop2.getFittestCandidate().getGenes());
//        frame.generatePlot();
//        
//        pop2 = ga.evolvePopulation2(pop2);
//        
//        ArrayList<Double> generationFitnesses = new ArrayList<>();
//        ArrayList<Integer> generationDistances = new ArrayList<>();
//        ArrayList<Integer> generations = new ArrayList<>();
//        
//        // Evolve over 100 generations.
//         for (int i = 0; i < 100; i++) {
//        	 pop2 = ga.evolvePopulation2(pop2);
//        	 TSPRoute test = (TSPRoute) pop2.getFittestCandidate();
//        	 generationDistances.add(test.getRouteDistance());
//        	 generationFitnesses.add(pop2.getFittestCandidate().getFitness());
//        	 generations.add(i+1);
//        }
//         
//        System.out.println("Finished2");
//        System.out.println("Final fitness2: " + pop2.getFittestCandidate().getFitness());
//        
//        TSPPlotter frame2 = new TSPPlotter.TSPPlotterBuilder().setAxisMaxRangeSettings(200, 200).buildTSPPlotter();
//        
//        frame2.updateData(pop2.getFittestCandidate().getGenes());
//        frame2.generatePlot();
//        
//        frame2.setTitle("end");
//        
//        System.out.println("Solution:");
//        System.out.println(((TSPRoute) pop2.getFittestCandidate()).getRouteLocations());
//        System.out.println(pop2.getPopulationSize());
//
//        TSPPlotter frame3 = new TSPPlotter.TSPPlotterBuilder().setLineColour(new Color(255, 0, 0)).buildTSPPlotter();
//
//        frame3.updateData2(generations, generationDistances);
//        frame3.generatePlot(true, false);
		
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
	
	public void runTSPGASolver(String[] args) {
		
		setupCLIParser();

		IOUtils test = new IOUtils();
		
		try {
			
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			
			if (line.hasOption("help") /*|| (!line.hasOption("datafile") || !line.hasOption("configfile") || !line.hasOption("exportlocation")) */) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("TSEGenerator", options);

			} else {
				
				String configFilePath = line.getOptionValue("cf");
				
				ArrayList<TSPExperiment> blah = (ArrayList<TSPExperiment>) test.parseConfigFile(configFilePath);
				
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
