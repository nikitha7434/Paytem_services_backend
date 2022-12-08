package com.example.MajorProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class TranscationServicess {

    @Autowired
    TranscationRepository transcationRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    public final String Redis_prefix_key="Transaction::";

    public void StartTranlaationService(TrancationRequest transcationRequset) {

        Transcation transcation= Transcation.builder().fromUser(transcationRequset.getFromUser())
                        .toUser(transcationRequset.getToUser()).amount(transcationRequset.getAmount())
                        .status(String.valueOf(TranscationStatus.PENDING))
                      .transactionId(String.valueOf(UUID.randomUUID()))
                      .transactionTime(String.valueOf(new Date())).build();

        // save in database
        transcationRepository.save(transcation);

        //save in cache
        saveIncache(transcation);

        //ToDo step send a message to wallet with help of help to update a wallet
        JSONObject walletRequest =new JSONObject();
        walletRequest.put("fromUser",transcationRequset.getFromUser());
        walletRequest.put("toUser",transcationRequset.getToUser());
        walletRequest.put("amount",transcationRequset.getAmount());
        walletRequest.put("transactionId",transcation.getTransactionId());

        String message =walletRequest.toString();
        kafkaTemplate.send("update_wallet",message);
    }

    public void saveIncache(Transcation transcation) {

        // save in from user in chache
        Map map = objectMapper.convertValue(transcation, Map.class);
        redisTemplate.opsForHash().putAll(Redis_prefix_key+transcation.getFromUser(),map);
        redisTemplate.expire(Redis_prefix_key+transcation.getFromUser(), Duration.ofHours(12));

        // save in to user in cache
       // map = objectMapper.convertValue(transcation, Map.class);
        redisTemplate.opsForHash().putAll(Redis_prefix_key+transcation.getToUser(),map);
        redisTemplate.expire(Redis_prefix_key+transcation.getToUser(), Duration.ofHours(12));


    }

    @KafkaListener(topics = {"update_transaction"},groupId = "Govinda_group")
    public void updateTranslation(String message) throws JsonProcessingException{

        //Decode the message
        JSONObject transactionRequset = objectMapper.readValue(message, JSONObject.class);
        String transcationstatus =(String)transactionRequset.get("TransactionStatus");
        String  transactionId =(String) transactionRequset.get("transactionId");
        Transcation t =transcationRepository.findByTransactionId(transactionId);
        t.setStatus(transcationstatus);
        transcationRepository.save(t);

        //save in cache
        saveIncache(t);

        // pending
      SendNoteficationTOusers(t);


    }

    private void SendNoteficationTOusers(Transcation t) {

        String fromUser = t.getFromUser();
        String toUser = t.getToUser();
        String transationId = t.getTransactionId();

        URI url = URI.create("http://localhost:8076/user?userName=" + fromUser);
        HttpEntity httpEntity = new HttpEntity(new HttpHeaders());

        JSONObject fromUserObject = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
        String senderName = (String) fromUserObject.get("name");
        String senderEmail = (String) fromUserObject.get("email");

        url = URI.create("http://localhost:8076/user?userName=" + toUser);
        httpEntity = new HttpEntity(new HttpHeaders());

        JSONObject toUserObject = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class).getBody();
        String toUserName = (String) toUserObject.get("name");
        String toUserEmail = (String) toUserObject.get("email");


        JSONObject EmailReqest = new JSONObject();
        EmailReqest.put("email", senderEmail);
        String senderBOdy = String.format("Hi %s the transcation with transactionId %s has been %s of Rs %d",
                senderName, transationId, t.getStatus(), t.getAmount());

        EmailReqest.put("message", senderBOdy);
        String message = EmailReqest.toString();

        //send it to kafka
        kafkaTemplate.send("send_email", message);


        if (t.getStatus().equals("FAILED")) {

            return;
        }
        //email the reciver also
EmailReqest.put("email",toUserEmail);
        String reciverMessageBody = String.format("hi %s the transaction with transactionId %s has been %s of Rs %d",
                toUserName, transationId, t.getStatus(), t.getAmount());
        message = EmailReqest.toString();
        kafkaTemplate.send("send_email", message);
    }
}
