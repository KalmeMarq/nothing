package me.kalmemarq;

import java.util.HashMap;
import java.util.Map;

public class Registry<T> {
    private final Map<Identifier, T> keyToValue = new HashMap<>();
    private final Map<T, Identifier> valueToKey = new HashMap<>();
	private final Map<Integer, Identifier> rawIdToKey = new HashMap<>();
	private final Map<Integer, T> rawIdToValue = new HashMap<>();
	private int lastRawId = -1;
	
	public void set(int rawId, Identifier key, T value) {
		this.rawIdToKey.put(rawId, key);
		this.rawIdToValue.put(rawId, value);
		this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
		this.lastRawId = rawId;
	}

    public void set(Identifier key, T value) {
        this.keyToValue.put(key, value);
		this.valueToKey.put(value, key);
		this.lastRawId += 1;
		this.rawIdToKey.put(this.lastRawId, key);
		this.rawIdToValue.put(this.lastRawId, value);
    }
	
	public T getValueByRawId(int rawId) {
		return this.rawIdToValue.get(rawId);
	}

	public Identifier getIdentifierByRawId(int rawId) {
		return this.rawIdToKey.get(rawId);
	}

    public T getValue(Identifier key) {
        return this.keyToValue.get(key);
    }

    public Identifier getIdentifier(T key) {
        return this.valueToKey.get(key);
    }
}
