package com.example.mongocrudspring.service.cache;

import com.example.mongocrudspring.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void cacheUser(User user, long timeout, TimeUnit unit) {
        String key = "user:" + user.getId();
        redisTemplate.opsForValue().set(key, convertToJson(user), timeout, unit);
    }

    public User getCachedUser(String userId) {
        String key = "user:" + userId;
        Object cachedUser = redisTemplate.opsForValue().get(key);
        if (cachedUser != null) {
            return convertFromJson(cachedUser.toString(), User.class);
        }
        return null;
    }

    public void invalidateUserCache(String userId) {
        String key = "user:" + userId;
        redisTemplate.delete(key);
    }

    public void cacheAllUsers(String key, Object data, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, convertToJson(data), timeout, unit);
    }

    public Object getCachedData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void invalidateCache(String key) {
        redisTemplate.delete(key);
    }

    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    private <T> T convertFromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }
}
