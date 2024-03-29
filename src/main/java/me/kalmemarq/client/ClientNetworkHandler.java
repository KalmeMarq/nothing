package me.kalmemarq.client;

import me.kalmemarq.common.entity.TextParticle;
import me.kalmemarq.common.world.Chunk;
import me.kalmemarq.common.world.Level;
import me.kalmemarq.common.entity.PlayerEntity;
import me.kalmemarq.client.screen.DisconnectedMenu;
import me.kalmemarq.common.network.NetworkConnection;
import me.kalmemarq.common.network.packet.*;

public class ClientNetworkHandler implements PacketListener {
    private final Client client;
    private final NetworkConnection connection;
	private Level storageLevel;

    public ClientNetworkHandler(Client client, NetworkConnection connection) {
        this.client = client;
        this.connection = connection;
    }
	
	public void sendPacket(Packet packet) {
		this.connection.sendPacket(packet);
	}

	@Override
	public void onTileUpdate(TileUpdatePacket packet) {
		System.out.println("Hey update this shiez; " + packet.getTileX() + ":" + packet.getTileY() + ":" + packet.getId());
		System.out.println("OLD: " + this.client.level.getTileId(packet.getTileX(), packet.getTileY()));
		
		this.client.level.setTileId(packet.getTileX(), packet.getTileY(), packet.getId());
		this.client.level.setData(packet.getTileX(), packet.getTileY(), packet.getData());
		System.out.println("NEW: " + this.client.level.getTileId(packet.getTileX(), packet.getTileY()));
	}

	@Override
	public void onDisconnect(DisconnectPacket packet) {
		this.client.connectionText.set(packet.getReason());
		this.client.disconnect();
		this.client.menu = new DisconnectedMenu(this.client);
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
//		this.client.messages.add("[" + Client.DATE_FORMATTER.format(packet.getTimestamp()) + "] " + packet.getMessage());
		this.client.messages.add(packet.getMessage());
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
		this.client.player.setLevel(this.storageLevel);
		this.storageLevel = null;
		this.client.menu = null;
		this.client.discordHelper.setStatus("Playing " + (this.client.integratedServer == null ? " Multiplayer" : "Singleplayer"));
	}

	@Override
	public void onTextParticle(TextParticlePacket packet) {
		this.client.level.addEntity(new TextParticle(packet));
	}
}
