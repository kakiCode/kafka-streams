package org.aprestos.labs.data.kafka.streams.processor.config;

public interface Constants {
	static String APP_NAME = "kafka-streams-processor"
			, ENV_KAFKA_CONFIG = "KAFKA_CONFIG"
			, ENV_ZOOKEEPER_CONFIG = "ZOOKEEPER_CONFIG"
			, APP_CONFIG_FILE = "config.json"
			;
	
	static enum ConfigParam {
			common("common")
			, processors("processors")
			, store("store")
			, sourceTopic("sourceTopic") , source("source")
			, processorName("processorName")
			, sink("sink"), sinkTopic("sinkTopic")
			, intervalInMillis("intervalInMillis");
			
			private ConfigParam(String name){
				this.name = name;
			}
			
			private String name;
			
			public String asString(){
				return name;
			}
			
			public static ConfigParam fromString(String type){
				ConfigParam result = null;
				for(ConfigParam t:ConfigParam.values()){
					if(t.asString().equals(type)){
						result = t;
						break;
					}
				}
				return result;
			}
			
	}
	
}
