package com.example.mongocrudspring.service.impl;

import com.example.mongocrudspring.exception.ResourceNotFoundException;
import com.example.mongocrudspring.model.User;
import com.example.mongocrudspring.repository.UserRepository;
import com.example.mongocrudspring.service.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final UserProducer producer;
    private final CacheService cacheService;

    private static final long CACHE_TIMEOUT = 10;
    private static final TimeUnit CACHE_TIME_UNIT = TimeUnit.MINUTES;

    @Autowired
    public UserServiceImpl(UserRepository repo, UserProducer producer, CacheService cacheService) {
        this.repo = repo;
        this.producer = producer;
        this.cacheService = cacheService;
    }

    @Override
    public User createUser(User user) {
        User saved = repo.save(user);
        producer.sendMessage("User Created: " + saved.getId());
        cacheService.cacheUser(saved, CACHE_TIMEOUT, CACHE_TIME_UNIT);
        return saved;
    }

    @Override
    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public User getUserById(String id) {
        User cachedUser = cacheService.getCachedUser(id);
        if (cachedUser != null) {
            System.out.println("User found in Redis cache: " + id);
            return cachedUser;
        }

        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        cacheService.cacheUser(user, CACHE_TIMEOUT, CACHE_TIME_UNIT);
        return user;
    }

    @Override
    public User updateUser(String id, User user) {
        User existing = getUserById(id);
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());

        User updated = repo.save(existing);
        producer.sendMessage("User Updated: " + updated.getId());

        cacheService.invalidateUserCache(id);
        cacheService.cacheUser(updated, CACHE_TIMEOUT, CACHE_TIME_UNIT);

        return updated;
    }

    @Override
    public void deleteUser(String id) {
        User user = getUserById(id);
        repo.delete(user);
        producer.sendMessage("User Deleted: " + id);
        cacheService.invalidateUserCache(id);
    }
}
