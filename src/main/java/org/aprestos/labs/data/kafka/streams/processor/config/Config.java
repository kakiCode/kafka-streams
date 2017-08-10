package org.aprestos.labs.data.kafka.streams.processor.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum Config {
	
	INSTANCE(), ;
	
	private final Map<String,Object> entries;
	
	private Config() {
		try {
			entries = new HashMap<String,Object>();
			init();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void init() throws IOException {
		
		BufferedReader reader = null;
		try {
			StringBuffer buff = new StringBuffer();
			reader = new BufferedReader(new FileReader(this.getClass().getClassLoader().getResource(Constants.APP_CONFIG_FILE).getFile()));
			String line = null;
			while( null != (line = reader.readLine()) )
				buff.append(line);
			
			ObjectMapper m = new ObjectMapper();
			entries.putAll(m.readValue(buff.toString(), Map.class));
		} 
		finally {
			if(null != reader)
				reader.close();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getProcessorsConfig(){
		checkInit();
		return(Map<String,Object>)entries.get(Constants.ConfigParam.processors.asString());
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getCommonConfig(){
		checkInit();
		return(Map<String,Object>)entries.get(Constants.ConfigParam.common.asString());
	}
	
	public List<String> getKeys(){
		checkInit();
		return new ArrayList<String>(entries.keySet());
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,String> getEntries(String key){
		checkInit();
		return (Map<String,String>) entries.get(key);
	}
	
	private void checkInit(){
		if(entries.isEmpty())
			throw new IllegalStateException("!!! Config was not initialized !!!");
	}
	
	
}
