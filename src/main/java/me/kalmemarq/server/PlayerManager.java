package me.kalmemarq.server;

import me.kalmemarq.entity.PlayerEntity;
import me.kalmemarq.network.NetworkConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
	private final Map<String, PlayerEntity> savedPlayerMap = new ConcurrentHashMap<>();
	public final Map<String, PlayerEntity> playerMap = new ConcurrentHashMap<>();
	public final Map<String, NetworkConnection> connectionMap = new ConcurrentHashMap<>();
	public final Map<NetworkConnection, String> connectionMapN = new ConcurrentHashMap<>();
	
	public PlayerManager() {
	}
	
	public boolean alreadyLogged(String username) {
		return this.playerMap.containsKey(username);
	}
	
	public void addPlayer(String username, PlayerEntity player, NetworkConnection connection) {
		this.playerMap.put(username, player);
		this.connectionMap.put(username, connection);
		this.connectionMapN.put(connection, username);
	}

	public Map<String, NetworkConnection> getConnectionMap() {
		return this.connectionMap;
	}

	public Map<NetworkConnection, String> getConnectionMapN() {
		return this.connectionMapN;
	}

	public Map<String, PlayerEntity> getPlayerMap() {
		return this.playerMap;
	}

	public PlayerEntity loadPlayer(String username, int preferredColor) {
		if (this.savedPlayerMap.containsKey(username)) {
			return this.savedPlayerMap.get(username);
		}

		PlayerEntity p = new PlayerEntity();
		p.color = preferredColor;
		return p;
	}

	public void removePlayer(String username) {
		this.savePlayer(username);
	}
	
	public void savePlayer(String username) {
		this.savedPlayerMap.put(username, this.playerMap.remove(username));
		this.connectionMapN.remove(this.connectionMap.remove(username));
	}
}
