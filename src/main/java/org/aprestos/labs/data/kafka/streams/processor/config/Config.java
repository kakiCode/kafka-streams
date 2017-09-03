package org.aprestos.labs.data.kafka.streams.processor.config;

import java.util.HashMap;
import java.util.Map;

public enum Config {

	INSTANCE(),;

	private final Map<String, String> entries;

	private Config() {
		entries = new HashMap<>();
		init();
	}

	private void init() {

		for (VARIABLE cv : VARIABLE.values()) {
			if (cv.isEnvironmentProvided()) {
				String envVar = null;
				if (null == (envVar = System.getenv(cv.asString()))) {
					throw new IllegalArgumentException(
							String.format("!!! must provide environment variable %s !!!", cv.asString()));
				}
				entries.put(cv.asString(), envVar);
			}
		}
		
		String streamsProcessorName = String.format("%s-%s-%s", 
				entries.get(VARIABLE.SOURCE_TOPIC.asString())
				, entries.get(VARIABLE.PROCESSOR_CLASS.asString())
				, entries.get(VARIABLE.SINK_TOPIC.asString()));
		
		for (VARIABLE cv : VARIABLE.values()) {
			if (! cv.isEnvironmentProvided()) {
				switch (cv) {
					case PROCESSOR_NAME:
						entries.put(cv.asString(), streamsProcessorName);
						break;
					case PROCESSOR_STORE:
						entries.put(cv.asString(), String.format("%s-store", streamsProcessorName));
						break;
					case APPLICATION_ID:
						entries.put(cv.asString(), String.format("%s-app", streamsProcessorName));
						break;
					case SOURCE_NAME:
						entries.put(cv.asString(), String.format("source-%s", entries.get(VARIABLE.SOURCE_TOPIC.asString())));
						break;
					case SINK_NAME:
						entries.put(cv.asString(), String.format("sink-%s", entries.get(VARIABLE.SINK_TOPIC.asString())));
						break;
					default:
				}
			}
		}

	}

	public String getConfig(VARIABLE v) {
		checkInit();
		return entries.get(v.asString());
	}
	
	public Map<String,String> asMap(){
		checkInit();
		return new HashMap<>(entries);
	}

	private void checkInit() {
		if (entries.isEmpty())
			throw new IllegalStateException("!!! Config was not initialized !!!");
	}

}
