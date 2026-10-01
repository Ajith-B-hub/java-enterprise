package com.example.mongocrudspring.model;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;

    // getters & setters
}