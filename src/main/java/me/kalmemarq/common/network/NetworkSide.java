package me.kalmemarq.common.network;

public enum NetworkSide {
	CLIENT,
	SERVER;
	
	public NetworkSide getOpposite() {
		return this == NetworkSide.CLIENT ? NetworkSide.SERVER : NetworkSide.CLIENT;
	}
}
