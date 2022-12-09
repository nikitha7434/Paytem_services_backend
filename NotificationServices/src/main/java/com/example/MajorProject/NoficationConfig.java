package com.example.MajorProject;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class NoficationConfig {

    @Bean
    public JavaMailSender getJavaMailSender(){

        JavaMailSenderImpl mailSender =new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("paytemtest1@gmail.com");
        mailSender.setPassword("sqxiuhrguconkwwc");


        Properties pre =mailSender.getJavaMailProperties();
        pre.put("mail.transport.protocol","smtp");
        pre.put("mail.smtp.auth",true);
        pre.put("mail.smtp.starttls.enable",true);
        pre.put("mail.debug","true");
    return mailSender;
    }
    @Bean
    SimpleMailMessage simpleMailMessage(){
        return new SimpleMailMessage();
    }

    //kafka listener
    @Bean
    Properties kafkaProperties(){
        Properties properties=new Properties();
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

}

