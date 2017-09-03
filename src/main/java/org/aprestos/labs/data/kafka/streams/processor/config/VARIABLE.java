package org.aprestos.labs.data.kafka.streams.processor.config;

public enum VARIABLE {
	SOURCE_TOPIC("SOURCE_TOPIC", true), SINK_TOPIC("SINK_TOPIC", true), 
	PROCESSOR_CLASS("PROCESSOR_CLASS", true), INTERVAL_IN_MILLIS("INTERVAL_IN_MILLIS", true)
	, KAFKA_CONFIG("KAFKA_CONFIG", true), PROCESSOR_STORE("PROCESSOR_STORE", false) 
	, PROCESSOR_NAME("PROCESSOR_NAME", false), APPLICATION_ID("APPLICATION_ID", false)
	, SINK_NAME("SINK_NAME", false), SOURCE_NAME("SOURCE_NAME", false)
	;
	
	private String name;
	private boolean environmentProvided;
	
	private VARIABLE(String name, boolean environmentProvided) {
		this.name = name;
		this.environmentProvided = environmentProvided;
	}
	
	public String asString() {
		return name;
	}
	
	public boolean isEnvironmentProvided() {
		return environmentProvided;
	}
	
	public static VARIABLE fromString(String name) {
		VARIABLE result = null;
		for(VARIABLE cv:VARIABLE.values()) {
			if( cv.name.equals(name) ) {
				result = cv;
				break;
			}
		}
		return result;
	}
}