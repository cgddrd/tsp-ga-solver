package uk.ac.aber.clg11.tsp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.aber.clg11.tsp.TSPExperiment;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPCrossoverSettings;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPMutationSettings;
import uk.ac.aber.clg11.tsp.TSPExperiment.TSPSelectionSettings;
import uk.ac.aber.clg11.tsp.exception.ConfigSettingMissingException;

public class IOUtils {

	public IOUtils() {

	}

	public List<TSPExperiment> parseConfigFile(String configFilePath) {
		
		try {

			File fXmlFile = new File(configFilePath);
			
			if (!fXmlFile.exists()) {
				throw new FileNotFoundException("Configuration file cannot be found.");
			}
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName()); 
			
			NodeList nList = doc.getElementsByTagName("experiment");
			
			ArrayList<TSPExperiment> experiments = new ArrayList<>();
			
			for (int i = 0; i < nList.getLength(); i++) {
				
				Node currentExperimentNode = nList.item(i);
				
				if (currentExperimentNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element currentExperimentElement = (Element) currentExperimentNode;
					
					//String selectionType = currentExperimentElement.getElementsByTagName("selection_type").item(0).getTextContent();
					
					TSPExperiment experiment = new TSPExperiment();
					
					experiment.getCrossoverSettings().setCrossoverRate(20.00);
					
					// 1. Parse selection settings.
					
					NamedNodeMap selectionConfigAttributes = currentExperimentElement.getElementsByTagName("selection").item(0).getAttributes();
					
					TSPSelectionSettings sSettings = experiment.getSelectionSettings();
					
					if (selectionConfigAttributes.getNamedItem("method") == null) {
						throw new ConfigSettingMissingException("selection method");
					} else {
						
						sSettings.setSelectionMethod(selectionConfigAttributes.getNamedItem("method").getNodeValue());
						
						if (sSettings.getSelectionMethod().toLowerCase().equals("tournament")) {
							
							sSettings.setTournamentSize(selectionConfigAttributes.getNamedItem("tournament_size") != null ? 
														Integer.parseInt(selectionConfigAttributes.getNamedItem("tournament_size").getNodeValue()) : 1);
							
						}
						
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
					
					if (crossoverConfigAttributes.getNamedItem("rate") == null) {
						
						throw new ConfigSettingMissingException("crossover rate");
						
					} else {
						
						cSettings.setCrossoverRate(Double.parseDouble(crossoverConfigAttributes.getNamedItem("rate").getNodeValue()));
					
					}
					
					
					// 4. Parse experiment name (if available).
					
					if (currentExperimentElement.hasAttribute("name")) {
						experiment.setExperimentName(currentExperimentElement.getAttribute("name"));
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

}
