package com.example.MajorProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
public class TranscationConfig {


    @Bean
    LettuceConnectionFactory getConnection(){

        RedisStandaloneConfiguration redisStandaloneConfiguration=new RedisStandaloneConfiguration();
        LettuceConnectionFactory lettuceConnectionFactory=new LettuceConnectionFactory(redisStandaloneConfiguration);
        return lettuceConnectionFactory;
    }
    @Bean
    RedisTemplate<String,Object> redisTemplate(){
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<>();
        RedisSerializer<String> redisSerializer=new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer =new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializationRedisSerializer);
        redisTemplate.setConnectionFactory(getConnection());
        return redisTemplate;
    }

    @Bean
    ObjectMapper getObjectMapper(){

        return new ObjectMapper();
    }


    @Bean
    Properties kafkaProperties(){
        Properties properties=new Properties();

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");

        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"Govinda_group");

        return properties;
    }

    @Bean
    ConsumerFactory<String,String> getConsumerFactory(){

        return new DefaultKafkaConsumerFactory(kafkaProperties());
    }

    // this is only for consumer bcz they have to listen simultaneous .. so this property needs to be there

    @Bean
    ConcurrentKafkaListenerContainerFactory<String,String> concurrentKafkaListenerContainerFactory(){

        ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(getConsumerFactory());
        return concurrentKafkaListenerContainerFactory;
    }
    @Bean
    ProducerFactory<String,String> getProducerFactory(){
        return new DefaultKafkaProducerFactory(kafkaProperties());
    }

    @Bean
    KafkaTemplate<String,String> getKafkaTemplate(){
        return new KafkaTemplate<>(getProducerFactory());
    }

    @Bean
    RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
