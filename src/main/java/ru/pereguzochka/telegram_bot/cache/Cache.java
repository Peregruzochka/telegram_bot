package ru.pereguzochka.telegram_bot.cache;

import java.util.HashMap;
import java.util.Map;

public abstract class Cache<K, V> {
    private final Map<K, V> cache = new HashMap<K, V>();

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public boolean contains(K key) {
        return cache.containsKey(key);
    }
}
