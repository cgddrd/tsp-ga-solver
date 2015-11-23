package uk.ac.aber.clg11.tsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

public class TSPGenerator {

	public static void main(String[] args) {

		Options options = new Options();

		options.addOption(Option.builder("f").longOpt("exportfile").hasArg().argName("fileName")
				.desc("Filepath of target export file.").build());

		options.addOption(Option.builder("p").longOpt("points").hasArg().argName("noOfPoints")
				.desc("Number of TSP coordinates to be generated.").build());

		options.addOption(Option.builder("g").longOpt("gridsize").hasArg().argName("gridSize")
				.desc("Size of the TSP grid space.").build());

		options.addOption(new Option("help", "Print this help message."));

		CommandLineParser parser = new DefaultParser();

		try {

			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help") || !line.hasOption("points")) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("TSEGenerator", options);

			} else {

				int noOfPoints = Integer.parseInt(line.getOptionValue("points"));

				String fileName = line.hasOption("f") && (!line.getOptionValue("f").equals("null"))
						? line.getOptionValue("f") : "." + File.separator + "TSPCoordsExport.csv";
				
			    // Check if the user has specified the correct '.csv' file type, and if not, correct this automatically.
				fileName = checkExportFileExtension(fileName);
						
				int gridSize = line.hasOption("g") && (Integer.parseInt(line.getOptionValue("g")) > 0)
						? Integer.parseInt(line.getOptionValue("g")) : 200;

				TSPCoord[] tspCoords = generateTSPPoints(noOfPoints, gridSize);

				if (tspCoords != null) {

					if (writeCSVFile(fileName, tspCoords)) {
						System.out.println("Success: TSP coordinates generated successfully.");
					} else {
						System.out.println(
								"Error: Unable to write CSV file. Are you sure writer permissions are available?");
					}

				} else {

					System.out.println("Error: No coordinates to write to file.");

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
	
	public static String checkExportFileExtension(String filePath) {
		
		if (!FilenameUtils.getExtension(filePath).equals("csv")) {
			
			String newFilePath = FilenameUtils.removeExtension(filePath);
			newFilePath = newFilePath.concat(".csv");
			
			return newFilePath;
		}
		
		return filePath;
		
	}

	public static TSPCoord[] generateTSPPoints(int noOfPoints, int gridSize) {

		Random randGen = new Random();

		TSPCoord[] points = new TSPCoord[noOfPoints];

		for (int i = 0; i < noOfPoints; i++) {

			points[i] = new TSPCoord("City-" + (i + 1), randGen.nextInt(gridSize - 0 + 1) + 0,
					randGen.nextInt(gridSize - 0 + 1) + 0);

		}

		return points;

	}

	public static boolean writeCSVFile(String exportFilePath, TSPCoord[] tspCoordinates) {

		try {

			File file = new File(exportFilePath);

			File parent = file.getParentFile();

			if (parent != null && !parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Error: Unable to create directory path: " + parent);
			}

			FileWriter writer = new FileWriter(file);

			writer.append("CoordName, X, Y\n");

			for (TSPCoord currentCoord : tspCoordinates) {

				writer.append(String.format("%s, %d, %d\n", currentCoord.getName(), currentCoord.getX(),
						currentCoord.getY()));
			}

			writer.flush();
			writer.close();

			return true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

	}

}
