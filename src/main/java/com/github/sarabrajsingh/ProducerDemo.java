package com.github.sarabrajsingh;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Hello world!
 *
 */
public class ProducerDemo 
{    
    public static void main( String[] args )
    {
        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");

        // these properties helper Kafka figure out what you're sending to it
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        // create producer record
        ProducerRecord<String, String> record = new ProducerRecord<String,String>("first_topic", "wagwan world!");

        // send data - asynchronous execution
        for(int i = 0; i < 10000; i++){
            producer.send(record);
            producer.send(new ProducerRecord<String,String>("first_topic", Integer.toString(i)));
        }

        // flush data
        producer.flush();

        // flush and close producer connection
        producer.close();


    }
}
