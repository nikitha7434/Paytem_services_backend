package com.example.MajorProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TranscationServicess {
    @Autowired
    TranscationRepository transcationRepository;

    public void StartTranscationService(TrancationRequest transcationRequset) {

        Transcation transcation= Transcation.builder().fromUser(transcationRequset.getFromUser())
                        .toUser(transcationRequset.getToUser()).amount(transcationRequset.getAmount())
                        .status(String.valueOf(TranscationStatus.PENDING))
                      .transactionId(String.valueOf(UUID.randomUUID()))
                      .transactionTime(String.valueOf(new Date())).build();
        transcationRepository.save(transcation);
    }
}
