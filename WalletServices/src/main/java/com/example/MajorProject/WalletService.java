package com.example.MajorProject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WalletService {
    @Autowired
    WalletRopsitry walletRopsitry;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public final String create_wallet_topic="create_wallet";

    @KafkaListener(topics = {create_wallet_topic},groupId = "Govinda_group")
    public void createWallet(String message)throws JsonProcessingException {

        //System.out.print("sucess or fails nikitha ");

        JSONObject walletRequset = objectMapper.readValue(message,JSONObject.class);

        String userName =(String) walletRequset.get("userName");

        Wallet wallet= Wallet.builder()
                .userName(userName).balance(1000).build();

        //we are saving in database
        walletRopsitry.save(wallet);
    }
    @KafkaListener(topics = {"deposited_money"},groupId = "Govinda_group")
    public void DepositeMoney(String message) throws Exception {

        JSONObject walletRequset = objectMapper.readValue(message,JSONObject.class);
        String userName =(String) walletRequset.get("userName");
        int  amount =(Integer) walletRequset.get("amount");

        Wallet wallet =walletRopsitry.findByUserName(userName);
        if(wallet !=null){
            wallet.setBalance(wallet.getBalance()+amount);
            walletRopsitry.save(wallet);

        }else{
            throw new Exception("userName is not fount");
        }



    }

    @KafkaListener(topics = {"update_wallet"},groupId = "Govinda_group")
    public void updateWallet(String message)throws JsonProcessingException{
        JSONObject walletReqest = objectMapper.readValue(message,JSONObject.class);

        String fromuser =(String)walletReqest.get("fromUser");
        String Touser =(String)walletReqest.get("toUser");
        int amount = (Integer) walletReqest.get("amount");
        String transactionId =(String) walletReqest.get("transactionId");


        /*
        if fail (if sender balancer is not sufficent)
        send status as failed

        //otherviews

        deuct the sender money
        add the recer mony
        send status as success
         */

        Wallet senderwallet = walletRopsitry.findByUserName(fromuser);
        if (senderwallet.getBalance()>=amount){

            //happy case
            //update wallet

            Wallet fromwallet =walletRopsitry.findByUserName(fromuser);
           fromwallet.setBalance(fromwallet.getBalance() -amount);
            walletRopsitry.save(fromwallet);

            Wallet towallet =walletRopsitry.findByUserName(Touser);
            towallet.setBalance(towallet.getBalance() +amount);
           walletRopsitry.save(towallet);

            //push to kafka
            JSONObject sendmessageToTransction= new JSONObject();
            sendmessageToTransction.put("transactionId",transactionId);
            sendmessageToTransction.put("TransactionStatus","SUCCESS");

            String sendmessage =sendmessageToTransction.toString();

            kafkaTemplate.send("update_transaction",sendmessage);

        }
        else{
            //sad case

            JSONObject sendmessageToTransction= new JSONObject();
            sendmessageToTransction.put("transactionId",transactionId);
            sendmessageToTransction.put("TransactionStatus","FAILED");

            String sendmessage =sendmessageToTransction.toString();

            kafkaTemplate.send("update_transaction",sendmessage);

        }


    }
}
