package server.node.system.account;

import java.io.Serializable;

public class Account implements Serializable {

	private static final long serialVersionUID = -418852006093836118L;

	private String deviceId;
	private Long playerId;
	private String channel;
	private String device;

	public Account() {
	}

	public Account(String deviceId, Long playerId, String channel, String device) {
		this.deviceId = deviceId;
		this.playerId = playerId;
		this.channel = channel;
		this.device = device;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

}
