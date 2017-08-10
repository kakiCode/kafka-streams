package org.aprestos.labs.data.kafka.streams.processor.topologies;

import java.util.Map;

import org.aprestos.labs.data.common.influxdb.PointDto;
import org.aprestos.labs.data.common.influxdb.PointUtils;
import org.aprestos.labs.data.kafka.streams.processor.StreamsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TickerProcessor extends StreamsProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TickerProcessor.class);

	
	public TickerProcessor(Map<String,String> conf) {
		super(conf);
		logger.trace("<IN>");
		logger.trace("<OUT>");
	}


	@Override
	public void work(Long ts, byte[] bytes) throws Exception {
		PointDto point = PointUtils.fromBytes(bytes);
		logger.debug(String.format("received point: %s", point.toString()));
		if (null != ts)
			this.kvStore.put(ts, bytes);
		else {
			this.kvStore.put(point.getTimestamp(), bytes);
			logger.debug("no timestamp from streams");
		}
	}

	
}
