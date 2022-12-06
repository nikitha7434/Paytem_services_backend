package com.example.MajorProject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrancationRequest {
    private String fromUser;
    private String toUser;
    private int amount;
}
