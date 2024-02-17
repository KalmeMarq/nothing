package me.kalmemarq.client;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import me.kalmemarq.common.logging.LogManager;
import me.kalmemarq.common.logging.Logger;

import java.time.OffsetDateTime;

public class DiscordHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final long APP_ID = 1208348650640638012L;
	private final Client client;
	private IPCClient ipcClient;
	private OffsetDateTime startTime;
	
	public DiscordHelper(Client client) {
		this.client = client;
	}
	
	public void connect() {
		this.ipcClient = new IPCClient(1208348650640638012L);
		this.ipcClient.setListener(new IPCListener(){
			@Override
			public void onReady(IPCClient client)
			{
				if (startTime == null) startTime = OffsetDateTime.now();
				RichPresence.Builder builder = new RichPresence.Builder();
				builder.setState("Playing")
					.setStartTimestamp(startTime)
					.setLargeImage("dicon", "dicon");
				ipcClient.sendRichPresence(builder.build());
			}
		});
        try {
            this.ipcClient.connect();
        } catch (NoDiscordClientException e) {
			LOGGER.info("Could not connect to discord: ", e);
        }
    }
	
	public void setStatus(String status) {
		if (this.ipcClient != null) {
			RichPresence.Builder builder = new RichPresence.Builder();
			builder.setState(status)
				.setStartTimestamp(startTime)
				.setLargeImage("dicon", "dicon");
			this.ipcClient.sendRichPresence(builder.build());
			
			this.ipcClient.sendRichPresence(builder.build());
		}
	}
	
	public void disconnect() {
		LOGGER.info("Disconnecting discord client...");
		if (this.ipcClient != null) this.ipcClient.close();
	}
}
