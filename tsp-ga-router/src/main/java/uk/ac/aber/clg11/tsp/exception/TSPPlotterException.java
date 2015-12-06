package uk.ac.aber.clg11.tsp.exception;

/**
 * Custom exception representing a failure within 'TSPPlotter' whilst plotting the collection of specified results.
 * 
 * @author Connor Goddard (clg11@aber.ac.u)
 *
 */
public class TSPPlotterException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public TSPPlotterException() {
		super("A fatal error has occured in whilst plotting results.");
	}
	
	public TSPPlotterException(String message) {
		super("A fatal error has occured in whilst plotting results: " + message);
	}
	
	

}
