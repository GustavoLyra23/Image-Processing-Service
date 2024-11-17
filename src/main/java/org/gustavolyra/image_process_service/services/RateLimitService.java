package org.gustavolyra.image_process_service.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {

    private static final int LIMIT = 3;
    private static final long INTERVAL_IN_SECONDS = 60;

    private final RedisTemplate<String, Object> redisTemplate;

    public RateLimitService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String userId) {
        var key = "rate_limit" + userId;
        Long count = redisTemplate.opsForValue().increment(key, 1);
        if (count != null && count == 1) {
            redisTemplate.expire(key, INTERVAL_IN_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        }
        return count != null && count <= LIMIT;
    }

}
