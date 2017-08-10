package org.aprestos.labs.data.kafka.streams.processor;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.Stores;
import org.aprestos.labs.data.kafka.streams.processor.topologies.TwitterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

	private KafkaStreams streams;
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	private App() {
		logger.trace("<IN>");
		Map<String, String> env = System.getenv();

		if (null == env.get(Constants.ENV_KAFKA_CONFIG))
			throw new RuntimeException("must have a KAFKA_CONFIG env variable");

		Properties settings = new Properties();
		settings.put(StreamsConfig.APPLICATION_ID_CONFIG, Constants.APP_NAME);
		settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.get(Constants.ENV_KAFKA_CONFIG));
		settings.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
		StreamsConfig config = new StreamsConfig(settings);

		@SuppressWarnings("rawtypes")
		StateStoreSupplier store = Stores.create("twitter-processor-store").withLongKeys().withByteArrayValues().persistent().build();
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.addSource("SOURCE", "tweets")
		.addProcessor("tweets-processor", TwitterProcessor::new, "SOURCE")
		.addStateStore(store, "tweets-processor").addSink("SINK", "tweets-processed", "tweets-processor");
		
		streams = new KafkaStreams(builder, config);

		streams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable throwable) {
				logger.error("OOOPPS!", throwable);
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
					streams.close();
		    }
		});
		
		streams.setStateListener(new StateListener() {
			
			@Override
			public void onChange(State arg0, State arg1) {
				System.out.println(arg0);
			}
		});
		
		streams.start();
		logger.trace("<OUT>");
	}

	/**
	 * Says hello to the world.
	 * 
	 * @param args
	 *            The arguments of the program.
	 */
	public static void main(String[] args) {
		new App();
	}
}
