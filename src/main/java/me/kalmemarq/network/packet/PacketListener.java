package me.kalmemarq.network.packet;

public interface PacketListener {
    default void onMessage(MessagePacket packet) {
    }

    default void onDisconnect(DisconnectPacket packet) {
    }

    default void onLogin(LoginPacket packet) {
    }

    default void onPlay(PlayPacket packet) {
    }

    default void onPos(PosPacket packet) {
    }

    default void onRemovePlayer(RemovePlayerPacket packet) {
    }

    default void onRequestPreviousMessages(RequestPreviousMessagesPacket packet) {
    }

    default void onWorldData(WorldDataPacket packet) {
    }

	default void onReadyPlay(ReadyPlayPacket packet) {
	}
	
	default void onChunkData(ChunkDataPacket packet) {
	}
	
	default void onLevelInfo(LevelInfoPacket packet) {
	}
}
