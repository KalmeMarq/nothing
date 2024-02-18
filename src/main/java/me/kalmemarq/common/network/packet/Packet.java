package me.kalmemarq.common.network.packet;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class Packet {
    public static Map<Class<? extends Packet>, Integer> CLASS_TO_ID = Map.ofEntries(
            Map.entry(MessagePacket.class, 0x1),
		Map.entry(RequestPreviousMessagesPacket.class, 0x2),
		Map.entry(LoginPacket.class, 0x3),
		Map.entry(DisconnectPacket.class, 0x4),
		Map.entry(PosPacket.class, 0x5),
		Map.entry(RemovePlayerPacket.class, 0x6),
		Map.entry(WorldDataPacket.class, 0x7),
		Map.entry(PlayPacket.class, 0x8),
		Map.entry(ReadyPlayPacket.class, 0x9),
		Map.entry(ChunkDataPacket.class, 0xA),
		Map.entry(LevelInfoPacket.class, 0xB),
		Map.entry(TextParticlePacket.class, 0xC),
		Map.entry(AttackPacket.class, 0xD),
		Map.entry(TileUpdatePacket.class, 0xE)
    );

    public static Map<Integer, Class<? extends Packet>> ID_TO_CLASS = CLASS_TO_ID.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    abstract public void write(PacketByteBuf buffer);

    abstract public void read(PacketByteBuf buffer);

    public void apply(PacketListener listener) {
    }
}
