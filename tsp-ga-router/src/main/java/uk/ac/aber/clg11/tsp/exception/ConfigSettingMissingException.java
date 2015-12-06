package uk.ac.aber.clg11.tsp.exception;

/**
 * Custom exception representing a failure to locate or extract a specified configuration option from the defined XML configuration file.
 * 
 * @author Connor Goddard (clg11@aber.ac.u)
 *
 */
public class ConfigSettingMissingException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ConfigSettingMissingException() {
		super("Unable to locate configuration setting for required option.");
	}
	
	public ConfigSettingMissingException(String configOptionName) {
		super("Unable to locate configuration setting for required option: " + configOptionName + ".");
	}

}
