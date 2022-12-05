package com.example.MajorProject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequset {
    private String userName;
    private String email;
    private  int age;
    private String name;
    private String mobile;
    private String city;
}
