package uk.ac.aber.clg11.tsp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TSPExperiment { 
	
	private String experimentName = "TSPExperiment_" + new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
	
	private TSPMutationSettings mutationSettings = new TSPMutationSettings();
	private TSPCrossoverSettings crossoverSettings = new TSPCrossoverSettings();
	private TSPSelectionSettings selectionSettings = new TSPSelectionSettings();
	
	public TSPExperiment() {
		
	}
	
	public TSPExperiment(String experimentName) {
		setExperimentName(experimentName);
	}
	
	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}
	
	public String getExperimentName() {
		return experimentName;
	}

	public TSPMutationSettings getMutationSettings() {
		return mutationSettings;
	}

	public TSPCrossoverSettings getCrossoverSettings() {
		return crossoverSettings;
	}
	
	public TSPSelectionSettings getSelectionSettings() {
		return selectionSettings;
	}

	public class TSPMutationSettings {
		
		private Double mutationRate;
		
		private TSPMutationSettings() {
			
		}
		
		public void setMutationRate(Double mutationRate) {
			this.mutationRate = mutationRate;
		}

		public Double getMutationRate() {
			return mutationRate;
		}
		
	}
	
	public class TSPCrossoverSettings {
		
		private Double crossoverRate;
		private Boolean returnSingleChild = false;
		
		private TSPCrossoverSettings() {
			
		}

		public Double getCrossoverRate() {
			return crossoverRate;
		}

		public void setCrossoverRate(Double crossoverRate) {
			this.crossoverRate = crossoverRate;
		}
		
	}
	
	public class TSPSelectionSettings {
		
		private String selectionMethod;
		private Integer tournamentSize;
		private Boolean useElitism = true;
		private Boolean returnSingleChild = false;
		
		private TSPSelectionSettings() {
			
		}

		public String getSelectionMethod() {
			return selectionMethod;
		}

		public void setSelectionMethod(String selectionMethod) {
			this.selectionMethod = selectionMethod.toLowerCase();
		}

		public Integer getTournamentSize() {
			return tournamentSize;
		}

		public void setTournamentSize(Integer tournamentSize) {
			this.tournamentSize = tournamentSize;
		}

		public Boolean getUseElitism() {
			return useElitism;
		}

		public void setUseElitism(Boolean useElitism) {
			this.useElitism = useElitism;
		}

		public Boolean getReturnSingleChild() {
			return returnSingleChild;
		}

		public void setReturnSingleChild(Boolean returnSingleChild) {
			this.returnSingleChild = returnSingleChild;
		}
		
	}
	
}



