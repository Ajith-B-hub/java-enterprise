package com.example.mongocrudspring.service.impl;

import com.example.mongocrudspring.exception.ResourceNotFoundException;
import com.example.mongocrudspring.model.User;
import com.example.mongocrudspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final UserProducer producer;

    @Autowired
    public UserServiceImpl(UserRepository repo, UserProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }

    @Override
    public User createUser(User user) {
        User saved = repo.save(user);
        producer.sendMessage("User Created: " + saved.getId());
        return saved;
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User updateUser(String id, User user) {
        User existing = getUserById(id);
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());

        User updated = repo.save(existing);
        producer.sendMessage("User Updated: " + updated.getId());
        return updated;
    }

    @Override
    public void deleteUser(String id) {
        User user = getUserById(id);
        repo.delete(user);
        producer.sendMessage("User Deleted: " + id);
    }
}
