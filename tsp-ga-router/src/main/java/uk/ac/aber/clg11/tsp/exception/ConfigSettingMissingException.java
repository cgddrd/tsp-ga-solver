package uk.ac.aber.clg11.tsp.exception;

public class ConfigSettingMissingException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String configOptionName;

	public ConfigSettingMissingException() {
		super("Unable to locate configuration setting for required option.");
	}
	
	public ConfigSettingMissingException(String configOptionName) {
		super("Unable to locate configuration setting for required option: " + configOptionName);
		this.configOptionName = configOptionName;
	}
	
	public ConfigSettingMissingException(String configOptionName, String message) {
		super(message);
		this.configOptionName = configOptionName;
	}
	
	

}
