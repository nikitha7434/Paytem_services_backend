package com.example.MajorProject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscationRepository extends JpaRepository<Transcation,Integer> {

    Transcation findByTransactionId(String transactionId);
}

