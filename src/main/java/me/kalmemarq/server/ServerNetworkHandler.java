package me.kalmemarq.server;

import me.kalmemarq.common.ChatMessage;
import me.kalmemarq.common.entity.PlayerEntity;
import me.kalmemarq.common.entity.TextParticle;
import me.kalmemarq.common.network.NetworkConnection;
import me.kalmemarq.common.network.packet.*;
import me.kalmemarq.common.world.Chunk;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

public class ServerNetworkHandler implements PacketListener {
	private final Server server;
	private final NetworkConnection connection;
	private int ticks;
	private Queue<Chunk> pendingChunks = new ArrayDeque<>();
	
	public ServerNetworkHandler(Server server, NetworkConnection connection) {
		this.server = server;
		this.connection = connection;
	}
	
	public void tick() {
		++this.ticks;
		
		if (this.ticks % 34000 == 2 && !this.pendingChunks.isEmpty()) {
			Chunk chunk = this.pendingChunks.poll();
			this.connection.sendPacket(new ChunkDataPacket(chunk.x, chunk.y, this.server.level.chunks[chunk.y * this.server.level.xChunks + chunk.x].tiles));
		
			if (this.pendingChunks.isEmpty()) {
				this.connection.sendPacket(new ReadyPlayPacket());
			}
		}
	}

	@Override
	public void onAttack(AttackPacket packet) {
		TextParticle textParticle = new TextParticle(packet.getTileX() * 16, packet.getTileY() * 16, 60, packet.getDamage() + "", 0xFF0000);
		this.broadcastPacket(new TextParticlePacket(textParticle.text, (int) textParticle.x, (int) textParticle.y, textParticle.lifetime, textParticle.xa, textParticle.ya, textParticle.za, textParticle.color));
		int data = this.server.level.getData(packet.getTileX(), packet.getTileY());
		data += packet.getDamage();
		this.server.level.setData(packet.getTileX(), packet.getTileY(), data);

		if (data > 20) {
			this.server.level.setTileId(packet.getTileX(), packet.getTileY(), 1);
			this.server.level.setData(packet.getTileX(), packet.getTileY(), 0);
			
			this.broadcastPacket(new TileUpdatePacket(packet.getTileX(), packet.getTileY(), this.server.level.getTileId(packet.getTileX(), packet.getTileY()), this.server.level.getData(packet.getTileX(), packet.getTileY())));
		}
	}
	
	private void broadcastPacket(Packet packet) {
		for (NetworkConnection connection : this.server.playerManager.connectionMap.values()) {
			connection.sendPacket(packet);
		}
	}

	@Override
	public void onLogin(LoginPacket packet) {
		if (this.server.playerManager.alreadyLogged(packet.getUsername())) {
			this.connection.sendPacket(new DisconnectPacket("Duplicated login"));
			return;
		}
		
		if (packet.getProtocolVersion() != Server.PROTOCOL_VERSION) {
			this.connection.sendPacket(new DisconnectPacket("Client is not compatible"));
			return;
		}
		
		this.server.printMessage(packet.getUsername() + " logged in");

		PlayerEntity p = this.server.playerManager.loadPlayer(packet.getUsername(), packet.getColor());

		this.connection.sendPacket(new PlayPacket(packet.getUsername(), p.x, p.y, p.color, p.dir));
		this.server.playerManager.addPlayer(packet.getUsername(), p, this.connection);
		
		for (Map.Entry<String, NetworkConnection> coo : this.server.playerManager.getConnectionMap().entrySet()) {
			if (!coo.getKey().equals(packet.getUsername())) {
				coo.getValue().sendPacket(new PosPacket(packet.getUsername(), p.x, p.y, p.color, p.dir));

				PlayerEntity a = this.server.playerManager.playerMap.get(coo.getKey());
				if (a != null) {
					this.connection.sendPacket(new PosPacket(coo.getKey(), a.x, a.y, a.color, p.dir));
				}
			}
		}
	}

	@Override
	public void onMessage(MessagePacket packet) {
		var msg = "<" + this.server.playerManager.connectionMapN.get(this.connection) + "> " + packet.getMessage();
		this.server.messages.add(new ChatMessage(msg, packet.getTimestamp()));
		for (NetworkConnection connection1 : this.server.connections) {
			connection1.sendPacket(new MessagePacket(msg, packet.getTimestamp()));
		}
		this.server.printMessage(msg);
	}

	@Override
	public void onPos(PosPacket packet) {
		if (this.server.playerManager.playerMap.containsKey(packet.getUsername())) {
			PlayerEntity p = this.server.playerManager.playerMap.get(packet.getUsername());
			p.x = packet.getX();
			p.y = packet.getY();
			p.dir = packet.getDir();

			for (NetworkConnection c : this.server.playerManager.connectionMapN.keySet()) {
				if (c == this.connection) continue;
				c.sendPacket(new PosPacket(packet.getUsername(), p.x, p.y, p.color, p.dir));
			}
		} else {
			this.connection.sendPacket(new DisconnectPacket("Who the fuck are you bruv?"));
			this.connection.disconnect();
		}
	}

	@Override
	public void onReadyPlay(ReadyPlayPacket packet) {
		this.connection.sendPacket(new LevelInfoPacket(this.server.level.width, this.server.level.height));
		
		for (int y = 0; y < this.server.level.yChunks; ++y) {
			for (int x = 0; x < this.server.level.xChunks; ++x) {
				this.pendingChunks.add(this.server.level.chunks[y * this.server.level.xChunks + x]);
			}
		}
	}
}
