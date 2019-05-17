package com.github.sarabrajsingh;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoAssignSeek {

    public static void main(String[] args) {
        
        Logger logger = LoggerFactory.getLogger(ConsumerDemo.class.getName());

        String topic = "test-topic";
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "my-fourth-application";

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // assign and seek are mostly used to replay data or fetch a specific message
        TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
        long offsetToReadFrom = 15L;
        consumer.assign(Arrays.asList(partitionToReadFrom));

        // seek
        consumer.seek(partitionToReadFrom, offsetToReadFrom);

        int numberOfMessagesToRead = 5;
        boolean isKeepReading = true;
        int counter = 0;

        // poll for new data
        while(isKeepReading){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100)); //new in Kakfa 2.0.0

            for(ConsumerRecord<String, String> record : records){
                counter++;
                logger.info("Key:" + record.key() + " Value: " + record.value());
                logger.info("Partition:" + record.partition());
                logger.info("Offset:" + record.offset());

                if(counter >= numberOfMessagesToRead) {
                    isKeepReading = false;
                    break;
                }
            } 
        }

        logger.info("exiting the application");
    }
}