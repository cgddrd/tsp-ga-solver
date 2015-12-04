package uk.ac.aber.clg11.tsp.exception;

public class TSPPlotterException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TSPPlotterException() {
		super("A fatal error has occured in whilst plotting results.");
	}
	
	public TSPPlotterException(String message) {
		super("A fatal error has occured in whilst plotting results: " + message);
	}
	
	

}
