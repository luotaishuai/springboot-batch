package com.example.batch.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author anonymity
 * @create 2019-08-16 11:17
 **/
@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
}
