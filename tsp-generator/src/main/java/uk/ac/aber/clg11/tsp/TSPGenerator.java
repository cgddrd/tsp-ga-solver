package uk.ac.aber.clg11.tsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TSPGenerator {

	public static void main(String[] args) {

		if (args.length < 1) {

			System.out.println("Error: User must supply number of TSP coordinates to generate. Exiting application.");

		} else {
			
			try {

				int noOfPoints = Integer.parseInt(args[0]);
				
				//Object.equals() also checks for nulls at the same time.
				String fileName = (args.length >= 2) && (args[1] != null) && (!args[1].equals("null")) ? args[1]
						: "." + File.separator + "TSPCoordsExport.csv";

				int gridSize = (args.length >= 3) && (args[2] != null) && (Integer.parseInt(args[2]) > 0) ? Integer.parseInt(args[2]) : 200;

				TSPCoord[] tspCoords = generateTSPPoints(noOfPoints, gridSize);

				if (tspCoords != null) {

					if (writeCSVFile(fileName, tspCoords)) {
						System.out.println("Success: TSP coordinates generated successfully.");
					} else {
						System.out.println("Error: Unable to write CSV file. Are you sure writer permissions are available?");
					}
					

				} else {

					System.out.println("Error: No coordinates to write to file.");

				}

			} catch (NumberFormatException nFE) {
				System.out.println("Error: First argument for number of TSP coordinates must be a number.");
			} catch (Exception ex) {
				System.out.println("Error occured while running TSP generator.");
				ex.printStackTrace();
			}
		}
		
		System.out.println("Application exited.");

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

			System.out.println(exportFilePath);

			if (!parent.exists() && !parent.mkdirs()) {
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
