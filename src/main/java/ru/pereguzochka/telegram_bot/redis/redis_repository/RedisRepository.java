package ru.pereguzochka.telegram_bot.redis.redis_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Optional;


@Slf4j
public abstract class RedisRepository<K extends Serializable, V extends Serializable> {

    private final HashOperations<String, K, V> hashOperations;
    private final ObjectMapper objectMapper;
    private final String mapName = initMapName();

    protected abstract String initMapName();

    protected RedisRepository(RedisTemplate<String, Serializable> redisTemplate, ObjectMapper objectMapper) {
        this.hashOperations = redisTemplate.opsForHash();
        this.objectMapper = objectMapper;
    }

    public void put(K key, V value) {
        checkMapName();
        hashOperations.put(mapName, key, value);
        log.debug("{}: put {}={}", mapName, key, value);
    }

    public Optional<V> get(K key, Class<V> clazz) {
        checkMapName();
        Object value = hashOperations.get(mapName, key);

        if (value != null) {
            V result = objectMapper.convertValue(value, clazz);
            log.debug("{}: get {}={}", mapName, key, result);
            return Optional.of(result);
        }
        Optional<V> result = Optional.empty();
        log.debug("{}: get {}={}", mapName, key, result);
        return result;
    }

    public void delete(K key) {
        checkMapName();
        hashOperations.delete(mapName, key);
        log.debug("{}: delete {}", mapName, key);
    }

    public boolean exists(K key) {
        checkMapName();
        boolean status = hashOperations.hasKey(mapName, key);
        log.debug("{}: exists {} = {}", mapName, key, status);
        return status;
    }

    protected void setDefault(V value) {
        checkMapName();
        hashOperations.keys(mapName).forEach(k -> hashOperations.put(mapName, k, value));
        log.debug("{}: setDefault {}", mapName, value);
    }

    private void checkMapName() {
        if (mapName == null) {
            throw new IllegalStateException("mapName is null");
        }
    }
}
