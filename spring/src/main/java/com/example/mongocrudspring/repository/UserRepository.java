package com.example.mongocrudspring.repository;

import com.example.mongocrudspring.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
