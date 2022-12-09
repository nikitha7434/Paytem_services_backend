package com.example.MajorProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranscationContorller {

    @Autowired
    TranscationServicess transcationServicesss;


    @PostMapping("/create")
    public ResponseEntity createTranscation(@RequestBody()TrancationRequest transcationRequset){

        transcationServicesss.StartTranlaationService(transcationRequset);
        return new ResponseEntity<>("Sucess", HttpStatus.OK);

    }

    @PostMapping("/deposite")
    public ResponseEntity DepositeMoney(@RequestParam("userName")String userName,
                                        @RequestParam("amount")int amount){

        transcationServicesss.DepositeMoney(userName,amount);
        return new ResponseEntity<>("Successs added money int your account ",HttpStatus.OK);

    }

}
