package org.aprestos.labs.data.kafka.streams.processor;


import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.KafkaStreams.StateListener;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.Stores;
import org.aprestos.labs.data.kafka.streams.processor.config.Config;
import org.aprestos.labs.data.kafka.streams.processor.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class App {

	private KafkaStreams streams;
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	
		TopologyBuilder builder = new TopologyBuilder();

		String sourceTopic = ( String ) Config.INSTANCE.getCommonConfig().get(Constants.ConfigParam.sourceTopic.asString());
		String source = (String) Config.INSTANCE.getCommonConfig().get(Constants.ConfigParam.source.asString());
		String sinkName = (String) Config.INSTANCE.getCommonConfig().get(Constants.ConfigParam.sink.asString());
		String sinkTopic = (String) Config.INSTANCE.getCommonConfig().get(Constants.ConfigParam.sinkTopic.asString());
		
		builder.addSource(source, sourceTopic);
		
		String[] processorNames = new String[Config.INSTANCE.getProcessorsConfig().size()]; 
		int index = 0;
		
		for(String classs: Config.INSTANCE.getProcessorsConfig().keySet()){
			
			try {
				Map<String,String> conf = (Map<String, String>) Config.INSTANCE.getProcessorsConfig().get(classs);
				Processor<Long, byte[]> processor = null;
				
				Class builderClass = this.getClass().getClassLoader().loadClass(classs);
				Constructor builderConstructor = builderClass
	    				.getConstructor(new Class[] {Map.class});
				processor = (Processor<Long, byte[]>) builderConstructor
	    				.newInstance(new Object[] {conf});
				String processorName = conf.get(Constants.ConfigParam.processorName.asString());
				
				String storeName = conf.get(Constants.ConfigParam.store.asString());

				StateStoreSupplier store = Stores.create(storeName).withLongKeys().withByteArrayValues().persistent().build();
				builder.addProcessor(processorName, (ProcessorSupplier) processor, source)
				.addStateStore(store, processorName);
				
				processorNames[index++] = processorName;

			} catch (Exception e) {
				throw new IllegalStateException("wrong config",e);
			}
			
		}
		
		builder.addSink(sinkName, sinkTopic, processorNames);
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
