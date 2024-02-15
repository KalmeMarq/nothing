package me.kalmemarq.server;

import me.kalmemarq.common.ChatMessage;
import me.kalmemarq.common.entity.PlayerEntity;
import me.kalmemarq.common.network.NetworkConnection;
import me.kalmemarq.common.network.packet.ChunkDataPacket;
import me.kalmemarq.common.network.packet.DisconnectPacket;
import me.kalmemarq.common.network.packet.LevelInfoPacket;
import me.kalmemarq.common.network.packet.LoginPacket;
import me.kalmemarq.common.network.packet.MessagePacket;
import me.kalmemarq.common.network.packet.PacketListener;
import me.kalmemarq.common.network.packet.PlayPacket;
import me.kalmemarq.common.network.packet.PosPacket;
import me.kalmemarq.common.network.packet.ReadyPlayPacket;

import java.util.Map;

public class ServerNetworkHandler implements PacketListener {
	private final Server server;
	private final NetworkConnection connection;
	private int ticks;
	
	public ServerNetworkHandler(Server server, NetworkConnection connection) {
		this.server = server;
		this.connection = connection;
	}
	
	public void tick() {
		++this.ticks;
	}

	@Override
	public void onLogin(LoginPacket packet) {
		System.out.println("Bruv" + Thread.currentThread());
		if (true && this.server.playerManager.alreadyLogged(packet.getUsername())) {
			this.connection.sendPacket(new DisconnectPacket("Duplicated login"));
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
				this.connection.sendPacket(new ChunkDataPacket(x, y, this.server.level.chunks[y * this.server.level.xChunks + x].tiles));
			}
		}
		this.connection.sendPacket(new ReadyPlayPacket());
	}
}
