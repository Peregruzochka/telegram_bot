package ru.pereguzochka.telegram_bot.redis.redis_repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Optional;


@Slf4j
public abstract class RedisRepository<K extends Serializable, V extends Serializable> {

    private final HashOperations<String, K, V> hashOperations;
    private final String mapName = initMapName();

    protected abstract String initMapName();

    protected RedisRepository(RedisTemplate<String, Serializable> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void put(K key, V value) {
        checkMapName();
        hashOperations.put(mapName, key, value);
        log.info("{}: put {}={}", mapName, key, value);
    }

    public Optional<V> get(K key) {
        checkMapName();
        Optional<V> result = Optional.ofNullable(hashOperations.get(mapName, key));
        log.info("{}: get {}", mapName, key);
        return result;
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
