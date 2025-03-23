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
        log.info("{}: put {}={}", mapName, key, value);
    }

    public Optional<V> get(K key, Class<V> clazz) {
        checkMapName();
        Object value = hashOperations.get(mapName, key);

        if (value != null) {
            V result = objectMapper.convertValue(value, clazz);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    public void delete(K key) {
        checkMapName();
        hashOperations.delete(mapName, key);
        log.info("{}: delete {}", mapName, key);
    }

    public boolean exists(K key) {
        checkMapName();
        boolean status = hashOperations.hasKey(mapName, key);
        log.info("{}: exists {} = {}", mapName, key, status);
        return status;
    }

    private void checkMapName() {
        if (mapName == null) {
            throw new IllegalStateException("mapName is null");
        }
    }
}
