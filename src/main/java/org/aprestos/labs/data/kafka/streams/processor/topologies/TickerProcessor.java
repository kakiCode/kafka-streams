package org.aprestos.labs.data.kafka.streams.processor.topologies;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.aprestos.labs.data.common.influxdb.PointDto;
import org.aprestos.labs.data.common.influxdb.PointUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TickerProcessor implements Processor<Long, byte[]> {
	
	private ProcessorContext context;
	private KeyValueStore<Long, byte[]> kvStore;
	private static final Logger logger = LoggerFactory.getLogger(TickerProcessor.class);
	
	public TickerProcessor() {
		logger.trace("<IN>");

		logger.trace("<OUT>");
	}

	@Override
	public void close() {
		logger.trace("close<IN>");
		// close any resources managed by this processor.
        // Note: Do not close any StateStores as these are managed
        // by the library
		logger.trace("close<OUT>");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(ProcessorContext context) {
		logger.trace("init<IN>");
		// keep the processor context locally because we need it in punctuate() and commit()
        this.context = context;

        // call this processor's punctuate() method every 5000 milliseconds.
        this.context.schedule(5000);

        // retrieve the key-value store named "Counts"
        this.kvStore = (KeyValueStore<Long, byte[]>) context.getStateStore("twitter-processor-store");
        logger.trace("init<OUT>");
	}

	@Override
	public void process(Long arg0, byte[] bytes) {
		logger.trace("process<IN>");

		try {
			PointDto point = PointUtils.fromBytes(bytes);
			logger.debug(String.format("received point: %s", point.toString()));
			if(null != arg0)
				this.kvStore.put(arg0, bytes);
			else {
				this.kvStore.put(point.getTimestamp(), bytes);
				logger.debug("no timestamp from streams");
			}
			
		} catch (Exception e) {
			logger.error("oops", e);
		}
		finally {
			logger.trace("process<OUT>");
		}
	}

	@Override
	public void punctuate(long timestamp) {
		logger.trace("punctuate<IN>");
		KeyValueIterator<Long, byte[]> iter = this.kvStore.all();

        while (iter.hasNext()) {
            KeyValue<Long, byte[]> entry = iter.next();
            context.forward(null, entry.value);
        }
        iter.close();
        // commit the current processing progress
        context.commit();
        logger.trace("punctuate<OUT>");
	}
	
	public String getSourceTopic(){
		return "tweets";
	}
	
	public String getSinkTopic(){
		return "tweets-processed";
	}
	
	public String getName(){
		return "twitter-processor";
	}

}
