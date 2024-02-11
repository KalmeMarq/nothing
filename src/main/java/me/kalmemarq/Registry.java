package me.kalmemarq;

import java.util.HashMap;
import java.util.Map;

public class Registry<T> {
    private final Map<Identifier, T> keyToValue = new HashMap<>();
    private final Map<T, Identifier> valueToKey = new HashMap<>();

    public void set(Identifier key, T value) {
        this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
    }

    public T getValue(Identifier key) {
        return this.keyToValue.get(key);
    }

    public Identifier getIdentifier(T key) {
        return this.valueToKey.get(key);
    }
}
