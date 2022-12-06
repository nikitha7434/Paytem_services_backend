package com.example.MajorProject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transcation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String transactionId= UUID.randomUUID().toString();

    @Column
    private String fromUser;
    @Column
    private String toUser;
    @Column
    private int amount;
    @Column
    private String status;
    @Column
    private String transactionTime;

}
