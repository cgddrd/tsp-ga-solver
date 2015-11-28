package uk.ac.aber.clg11.tsp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.aber.clg11.tsp.TSPExperiment;
import uk.ac.aber.clg11.tsp.TSPLocation;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPCrossoverSettings;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPMutationSettings;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPPopulationSettings;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPSelectionSettings;
import uk.ac.aber.clg11.tsp.exception.ConfigSettingMissingException;

public class IOUtils {

	public IOUtils() {

	}
	
	/**
	 * Parses the CSV data file containing randomised TSP location definitions.
	 * Modified from original source: http://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
	 * @param dataFilepath - The file-path of the input data file.
	 * @return Collection of 'TSPLocation' definitions.
	 */
	public ArrayList<TSPLocation> parseTSPLocationDataFile(String dataFilePath) {
		
		String defaultSplitChar = ",";
		return this.parseTSPLocationDataFile(dataFilePath, defaultSplitChar);
		
	}
	
	/**
	 * Parses the CSV data file containing randomised TSP location definitions.
	 * Modified from original source: http://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
	 * @param dataFilepath - The file-path of the input data file.
	 * @param splitChar - Custom character by which to split the data inputs.
	 * @return Collection of 'TSPLocation' definitions.
	 */
	public ArrayList<TSPLocation> parseTSPLocationDataFile(String dataFilePath, String splitChar) {
		
		BufferedReader br = null;
		String line = "";
		ArrayList<TSPLocation> locations = new ArrayList<>();
		
		try {

			br = new BufferedReader(new FileReader(dataFilePath));
			
			int lineNo = 0;
			
			while ((line = br.readLine()) != null) {
				
				// Make sure to skip the first line, as this contains the column titles.
				if (lineNo > 0) {
					
					String[] tspLocationData = line.split(splitChar);
					
					locations.add(new TSPLocation(Integer.parseInt(tspLocationData[1].trim()), Integer.parseInt(tspLocationData[2].trim())));
				
				}
				
				lineNo++;
				
			}
			
			return locations;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
		
	}
	
	/**
	 * Parses the XML configuration file for experiment definitions.
	 * Modified from original source: http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	 * @param configFilePath - The file-path of the input configuration file.
	 * @return Collection of 'TSEExperiment' definitions.
	 */
	public ArrayList<TSPExperiment> parseConfigFile(String configFilePath) {
		
		try {

			File fXmlFile = new File(configFilePath);
			
			if (!fXmlFile.exists()) {
				throw new FileNotFoundException("Configuration file cannot be found.");
			}
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("experiment");
			
			ArrayList<TSPExperiment> experiments = new ArrayList<>();
			
			for (int i = 0; i < nList.getLength(); i++) {
				
				TSPExperiment experiment = new TSPExperiment();
				
				Node currentExperimentNode = nList.item(i);
				
				if (currentExperimentNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element currentExperimentElement = (Element) currentExperimentNode;
					
					// 1. Parse selection settings.
					
					NamedNodeMap selectionConfigAttributes = currentExperimentElement.getElementsByTagName("selection").item(0).getAttributes();
					
					TSPSelectionSettings sSettings = experiment.getSelectionSettings();
					
					if (selectionConfigAttributes.getNamedItem("method") == null) {
						throw new ConfigSettingMissingException("selection method");
					} else {
						
						sSettings.setSelectionMethod(selectionConfigAttributes.getNamedItem("method").getNodeValue());
						
						sSettings.setTournamentSize(selectionConfigAttributes.getNamedItem("tournament_size") != null ? 
														Integer.parseInt(selectionConfigAttributes.getNamedItem("tournament_size").getNodeValue()) : -1);
										
						sSettings.setReturnSingleChild(selectionConfigAttributes.getNamedItem("return_single_child") != null ?
													   Boolean.valueOf(selectionConfigAttributes.getNamedItem("return_single_child").getNodeValue()) : false);
						
						sSettings.setUseElitism(selectionConfigAttributes.getNamedItem("use_elitism") != null ?
												Boolean.valueOf(selectionConfigAttributes.getNamedItem("use_elitism").getNodeValue()) : false);
			
					}

					
					// 2. Parse mutation settings.
					
					NamedNodeMap mutationConfigAttributes = currentExperimentElement.getElementsByTagName("mutation").item(0).getAttributes(); 
					
					TSPMutationSettings mSettings = experiment.getMutationSettings();
					
					if (mutationConfigAttributes.getNamedItem("rate") == null) {
						
						throw new ConfigSettingMissingException("mutation rate");
						
					} else {
						
						mSettings.setMutationRate(Double.parseDouble(mutationConfigAttributes.getNamedItem("rate").getNodeValue()));
						
					}
					
					
					// 3. Parse crossover settings.
					
					NamedNodeMap crossoverConfigAttributes = currentExperimentElement.getElementsByTagName("crossover").item(0).getAttributes(); 
					
					TSPCrossoverSettings cSettings = experiment.getCrossoverSettings();

					cSettings.setCrossoverMethod(crossoverConfigAttributes.getNamedItem("method").getNodeValue());
					
					if (crossoverConfigAttributes.getNamedItem("rate") == null) {
						
						throw new ConfigSettingMissingException("crossover rate");
						
					} else {
						
						cSettings.setCrossoverRate(Double.parseDouble(crossoverConfigAttributes.getNamedItem("rate").getNodeValue()));
					
					}
					
					
					// 4. Parse population settings.
					
					NamedNodeMap populationConfigAttributes = currentExperimentElement.getElementsByTagName("population").item(0).getAttributes(); 
					
					TSPPopulationSettings pSettings = experiment.getPopulationSettings();
					
					if (populationConfigAttributes.getNamedItem("size") == null) {
						
						throw new ConfigSettingMissingException("population size");
						
					} else {
						
						pSettings.setPopulationSize(Integer.parseInt(populationConfigAttributes.getNamedItem("size").getNodeValue()));
					}
					
					
					// 5. Parse experiment name (if available).
					
					if (currentExperimentElement.hasAttribute("name")) {
						experiment.setExperimentName(currentExperimentElement.getAttribute("name"));
					}
					
					// 6. Parse experiment generation number.
					
					if (currentExperimentElement.hasAttribute("gens")) {
						experiment.setExperimentGenerations(Integer.parseInt(currentExperimentElement.getAttribute("gens")));
					} else {
						throw new ConfigSettingMissingException("number of generations");
					}
					
					experiments.add(experiment);

				}
				
			}
			
			return experiments;
		
		} catch (ConfigSettingMissingException cEx) {
			System.out.println(cEx.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
	    }
		
		return null;
		
	}
	
	public void exportExperimentParametersToFile(TSPExperiment experimentSettings, String exportFilePath, String exportFileName) {
		
		File file = new File(FilenameUtils.concat(exportFilePath, exportFileName));
		
		File parent = file.getParentFile();

		if (parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Error: Unable to create directory path: " + parent);
		}
		
		try {
			FileUtils.writeStringToFile(file, experimentSettings.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
