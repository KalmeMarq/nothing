package me.kalmemarq.common.network.packet;

import me.kalmemarq.common.entity.TextParticle;

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
	
	default void onTextParticle(TextParticlePacket packet) {
	}

	default void onAttack(AttackPacket packet) {
	}

	default void onTileUpdate(TileUpdatePacket packet) {
	}
}
