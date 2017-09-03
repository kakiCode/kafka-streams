package org.aprestos.labs.data.kafkaconsumer;


import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Unit test for simple App.
 */
public class AppTest {


    
    @Test
	public void test_010_jsonCasts() throws JsonParseException, JsonMappingException, IOException {
		String str = "{ \"org.aprestos.labs.data.kafka.streams.processor.topologies.TickerProcessor\": { \"sasa\":  \"sasaas\", \"secret\": \"89789789\" }"
				+ ", \"org.aprestos.labs.data.kafka.streams.processor.topologies.TickerProcessor2\": { \"sasa\":  \"sasaas\", \"secret\": \"89789789\" }}";
		ObjectMapper m = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String,Object> o = m.readValue(str, Map.class);
		for(Map.Entry<String, Object> e: o.entrySet()){
			System.out.println(e.getKey());
			System.out.println(e.getValue());
		}
	
		Assert.assertNotNull(o);
	}


}
