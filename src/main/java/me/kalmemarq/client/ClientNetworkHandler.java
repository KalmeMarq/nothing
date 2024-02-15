package me.kalmemarq.client;

import me.kalmemarq.world.Chunk;
import me.kalmemarq.world.Level;
import me.kalmemarq.entity.PlayerEntity;
import me.kalmemarq.client.screen.DisconnectedScreen;
import me.kalmemarq.network.NetworkConnection;
import me.kalmemarq.network.packet.*;

class ClientNetworkHandler implements PacketListener {
    private final Client client;
    private final NetworkConnection connection;
	private Level storageLevel;

    public ClientNetworkHandler(Client client, NetworkConnection connection) {
        this.client = client;
        this.connection = connection;
    }

	@Override
	public void onDisconnect(DisconnectPacket packet) {
		this.client.connectionText.set(packet.getReason());
		this.client.disconnect();
		this.client.screen = new DisconnectedScreen(this.client);
	}

	@Override
	public void onRemovePlayer(RemovePlayerPacket packet) {
		this.client.playerList.remove(packet.getUsername());
	}

	@Override
	public void onPos(PosPacket packet) {
		PlayerEntity p = this.client.playerList.get(packet.getUsername());
		if (p == null) {
			p = new PlayerEntity();
			p.color = packet.getColor();
			this.client.playerList.put(packet.getUsername(), p);
		}
		p.prevX = p.x;
		p.prevY = p.y;
		p.x = packet.getX();
		p.y = packet.getY();
		p.dir = packet.getDir();
	}

	@Override
	public void onPlay(PlayPacket packet) {
		this.client.player = new PlayerEntity();
		this.client.player.x = packet.getX();
		this.client.player.y = packet.getY();
		this.client.player.prevX = this.client.player.x;
		this.client.player.prevY = this.client.player.y;
		this.client.player.color = packet.getColor();
		this.client.player.dir = packet.getDir();
		this.connection.sendPacket(new ReadyPlayPacket());
	}

	@Override
	public void onMessage(MessagePacket packet) {
		this.client.messages.add("[" + Client.DATE_FORMATTER.format(packet.getTimestamp()) + "] " + packet.getMessage());
	}

	@Override
    public void onWorldData(WorldDataPacket packet) {
		Level level = new Level(packet.getWidth(), packet.getHeight());
//		level.load(packet.getTiles());
		this.storageLevel = level;
    }
	
	@Override
	public void onChunkData(ChunkDataPacket packet) {
		this.storageLevel.chunks[packet.getY() * this.storageLevel.xChunks + packet.getX()] = new Chunk(packet.getX(), packet.getY(), packet.getTiles());
	}

	@Override
	public void onLevelInfo(LevelInfoPacket packet) {
		Level level = new Level(packet.getWidth(), packet.getHeight());
		this.storageLevel = level;
	}

	@Override
	public void onReadyPlay(ReadyPlayPacket packet) {
		this.client.level = this.storageLevel;
		this.client.screen = null;
	}
}
