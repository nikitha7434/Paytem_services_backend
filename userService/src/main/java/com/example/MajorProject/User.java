package com.example.MajorProject;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements Serializable {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

@Column(unique = true)
private String userName;

@Column
private String email;

@Column
private String name;

@Column
private int age;

@Column
private String mobile;

@Column
private String city;

}
