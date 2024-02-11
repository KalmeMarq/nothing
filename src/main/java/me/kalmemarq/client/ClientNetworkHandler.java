package me.kalmemarq.client;

import me.kalmemarq.network.NetworkConnection;
import me.kalmemarq.network.packet.DisconnectPacket;
import me.kalmemarq.network.packet.LoginPacket;
import me.kalmemarq.network.packet.MessagePacket;
import me.kalmemarq.network.packet.PacketListener;
import me.kalmemarq.network.packet.PlayPacket;
import me.kalmemarq.network.packet.PosPacket;
import me.kalmemarq.network.packet.WorldDataPacket;

class ClientNetworkHandler implements PacketListener {
    private final Client client;
    private final NetworkConnection connection;

    public ClientNetworkHandler(Client client, NetworkConnection connection) {
        this.client = client;
        this.connection = connection;
    }

    @Override
    public void onMessage(MessagePacket packet) {
    }

    @Override
    public void onDisconnect(DisconnectPacket packet) {
    }

    @Override
    public void onPos(PosPacket packet) {
    }

    @Override
    public void onLogin(LoginPacket packet) {
    }

    @Override
    public void onPlay(PlayPacket packet) {
    }

    @Override
    public void onWorldDataPacket(WorldDataPacket packet) {
        System.out.println("[");
        for (byte v : packet.getTiles()) {
            System.out.println(v + ", ");
        }
        System.out.println("]");
    }
}
