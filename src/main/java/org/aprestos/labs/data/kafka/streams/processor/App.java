package org.aprestos.labs.data.kafka.streams.processor;


import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.Stores;
import org.aprestos.labs.data.kafka.streams.processor.config.Config;
import org.aprestos.labs.data.kafka.streams.processor.config.VARIABLE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

	private KafkaStreams streams;
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private App() {
		logger.trace("<IN>");

		try {
			Properties settings = new Properties();
			settings.put(StreamsConfig.APPLICATION_ID_CONFIG, Config.INSTANCE.getConfig(VARIABLE.APPLICATION_ID));
			settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Config.INSTANCE.getConfig(VARIABLE.KAFKA_CONFIG));
			settings.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
			settings.put("max.poll.records", "20");
			
			StreamsConfig config = new StreamsConfig(settings);
			
			Processor<Long, byte[]> processor = null;
			
			String processorClass = Config.INSTANCE.getConfig(VARIABLE.PROCESSOR_CLASS);
			String sourceTopic = Config.INSTANCE.getConfig(VARIABLE.SOURCE_TOPIC);
			String sinkTopic = Config.INSTANCE.getConfig(VARIABLE.SINK_TOPIC);
			
			
			Class builderClass = this.getClass().getClassLoader().loadClass(processorClass);
			Constructor builderConstructor = builderClass
					.getConstructor(Map.class);
			processor = (Processor<Long, byte[]>) builderConstructor
					.newInstance(Config.INSTANCE.asMap());
			StateStoreSupplier store = Stores.create(Config.INSTANCE.getConfig(VARIABLE.PROCESSOR_STORE)).withLongKeys().withByteArrayValues().persistent().build();
			
			TopologyBuilder builder = new TopologyBuilder();
			builder.addSource(Config.INSTANCE.getConfig(VARIABLE.SOURCE_NAME), sourceTopic)
				.addProcessor(Config.INSTANCE.getConfig(VARIABLE.PROCESSOR_NAME), (ProcessorSupplier) processor, Config.INSTANCE.getConfig(VARIABLE.SOURCE_NAME))
				.addStateStore(store, Config.INSTANCE.getConfig(VARIABLE.PROCESSOR_NAME))
				.addSink(Config.INSTANCE.getConfig(VARIABLE.SINK_NAME), sinkTopic, Config.INSTANCE.getConfig(VARIABLE.PROCESSOR_NAME));
			streams = new KafkaStreams(builder, config);

			streams.setUncaughtExceptionHandler( (thread,throwable) -> logger.error("OOOPPS!", thread, throwable) );
			streams.setStateListener((a1,a2) -> logger.debug(String.format("state changed: %s -> %s", a1, a2)) );
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
			    public void run() {
						streams.close();
			    }
			});
			
			streams.start();
			
			logger.info("started streams.... processorClass: {} sourceTopic: {} sinkTopic: {}", processorClass, sourceTopic, sinkTopic);
			
		} catch (Exception e) {
			logger.error("!!! could not start app !!!", e);
			throw new IllegalStateException(e);
		} 
		
		logger.trace("<OUT>");
	}

	public static void main(String[] args) {
		new App();
	}
}
