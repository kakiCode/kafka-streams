package org.aprestos.labs.data.kafka.streams.processor.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum Config {
	
	INSTANCE(), ;
	
	private final Map<String,Object> entries;
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
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
	
		InputStream inputStream = null;
		try {

			int ch;
			StringBuilder sb = new StringBuilder();
			inputStream = this.getClass().getClassLoader().getResourceAsStream(Constants.APP_CONFIG_FILE);
			while((ch = inputStream.read()) != -1)
			    sb.append((char)ch);
			
			ObjectMapper m = new ObjectMapper();
			entries.putAll(m.readValue(sb.toString(), Map.class));
		} 
		finally {
			if(null != inputStream)
				try { inputStream.close(); } catch (Exception e) {
					logger.warn("trying to close the input stream", e); }
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
