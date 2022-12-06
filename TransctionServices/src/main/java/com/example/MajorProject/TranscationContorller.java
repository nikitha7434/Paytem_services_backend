package com.example.MajorProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranscationContorller {
    @Autowired
    TranscationServicess transcationSerices;
    @PostMapping("/create")
    public ResponseEntity createTranscation(@RequestBody()TrancationRequest transcationRequset){

        transcationSerices.StartTranscationService(transcationRequset);
        return new ResponseEntity<>("Sucess", HttpStatus.OK);

    }
}
