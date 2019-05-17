package com.github.sarabrajsingh;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class ProducerDemoKeys {
    public static void main(String[] args) throws InterruptedException, ExecutionException
    {
        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class); // create logger for class
        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");

        // these properties helper Kafka figure out what you're sending to it
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for(int i = 0; i < 1000; i++){

            String topic = "test-topic";
            String value = "hello world" + Integer.toString(i);
            String key = "id_" + Integer.toString(i);
            // create producer record
            ProducerRecord<String, String> record = new ProducerRecord<String,String>(topic, key, value);

            // by providing a key, we are guaranteeing that the message will always goto the same partition

            logger.info("Key:" + key);
            // id_0 is going to partition 1
            // id_1 is going to partition 0
            // id_2 is going to partition 2

            // send data - asynchronous execution
            producer.send(record, new Callback() {
            
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    // executes everything a record is sucessfully sent, or exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("recieved new metadata. \n" +
                        "Topic:" + metadata.topic() + "\n" + 
                        "Partition:" + metadata.partition() + "\n" +
                        "Offset:" + metadata.offset() + "\n" +
                        "Timestamp:" + metadata.timestamp());
                    }
                    else {
                        logger.error("error while producing", e);
                    }
                }
            }).get(); // block the .send() to make it synchronous - don't do this in production!
        }

        // flush data
        producer.flush();

        // flush and close producer connection
        producer.close();


    }
}
