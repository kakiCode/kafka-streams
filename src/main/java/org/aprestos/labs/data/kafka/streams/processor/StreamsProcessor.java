package org.aprestos.labs.data.kafka.streams.processor;

import java.util.Map;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.aprestos.labs.data.kafka.streams.processor.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamsProcessor implements Processor<Long, byte[]>, ProcessorSupplier<Long, byte[]> {
	
	protected ProcessorContext context;
	protected KeyValueStore<Long, byte[]> kvStore;
	private static final Logger logger = LoggerFactory.getLogger(StreamsProcessor.class);
	protected final Map<String,String> config;
	
	public StreamsProcessor(Map<String,String> conf) {
		logger.trace("<IN>");
		config = conf;
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

        // call this processor's punctuate() method every x milliseconds.
        this.context.schedule(Long.parseLong(config.get(Constants.ConfigParam.intervalInMillis.asString())));

        // retrieve the key-value store named "Counts"
        this.kvStore = (KeyValueStore<Long, byte[]>) context.getStateStore(config.get(Constants.ConfigParam.store.asString()));
        logger.trace("init<OUT>");
	}

	@Override
	public void process(Long ts, byte[] bytes) {
		logger.trace("process<IN>");

		try {
			work(ts, bytes);
		} catch (Exception e) {
			logger.error("oops", e);
		}
		finally {
			logger.trace("process<OUT>");
		}
	}
	
	/**
	 * does the actual processing on the data,
	 * and should save it back to the store in the end
	 * as in:
	 * <pre>
	 * {@code
	 * 	PointDto point = PointUtils.fromBytes(bytes);
	 * 	logger.debug(String.format("received point: %s", point.toString()));
	 *	if(null != ts)
	 *		this.kvStore.put(ts, bytes);
	 *	else {
	 *		this.kvStore.put(point.getTimestamp(), bytes);
	 *		logger.debug("no timestamp from streams");
	 *	}
	 * }
	 * </pre>
	 * @param ts timestamp
	 * @param bytes serialized data
	 */
	public abstract void work(Long ts, byte[] bytes) throws Exception;
	
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
		return config.get(Constants.ConfigParam.sourceTopic.asString());
	}
	
	public String getSinkTopic(){
		return config.get(Constants.ConfigParam.sinkTopic.asString());
	}
	
	public String getName(){
		return config.get(Constants.ConfigParam.processorName.asString());
	}

	@Override
	public Processor<Long, byte[]> get() {
		return this;
	}

}
