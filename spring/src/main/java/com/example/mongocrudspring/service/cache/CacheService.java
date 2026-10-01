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
        redisTemplate.opsForValue().set(key, toJson(user), timeout, unit);
    }

    public User getCachedUser(String userId) {
        String key = "user:" + userId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached == null) {
            return null;
        }
        return fromJson(cached.toString(), User.class);
    }

    public void invalidateUserCache(String userId) {
        redisTemplate.delete("user:" + userId);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion failed", e);
        }
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion failed", e);
        }
    }
}
