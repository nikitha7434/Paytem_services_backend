package com.example.MajorProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SimpleMailMessage simpleMailMessage;
    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = {"send_email"},groupId = "Govinda_group")
    public void sendMailMessage(String message) throws JsonProcessingException {
        //Decoding the message To jsonObject
        //User email  message
        JSONObject emailRequset = objectMapper.readValue(message, JSONObject.class);

        // get the mail and message from
        String email = (String) emailRequset.get("email");
        String messageBody = (String) emailRequset.get("message");

        simpleMailMessage.setTo(email);
        simpleMailMessage.setText(messageBody);

        simpleMailMessage.setSubject("Transaction Mail");
        simpleMailMessage.setFrom("paytemtest1@gmail.com");
        javaMailSender.send(simpleMailMessage);
    }

    }
