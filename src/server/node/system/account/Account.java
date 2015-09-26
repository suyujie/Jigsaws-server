package server.node.system.account;

import java.io.Serializable;

public class Account implements Serializable {

	private static final long serialVersionUID = -866368867961824615L;

	private String mobileId;
	private int enable;
	private String plat;
	private String idInPlat;
	private String nameInPlat;
	private Long playerId;
	private String channel;
	private String device;

	public Account() {
	}

	public Account(String mobileId, int enable, String plat, String idInPlat, String nameInPlat, Long playerId, String channel, String device) {
		this.mobileId = mobileId;
		this.enable = enable;
		this.nameInPlat = nameInPlat;
		this.idInPlat = idInPlat;
		this.plat = plat;
		this.playerId = playerId;
		this.channel = channel;
		this.device = device;
	}

	public void setPlatAdnIdInPlat(String plat, String idInPlat, String nameInPlat) {
		this.plat = plat;
		this.idInPlat = idInPlat;
		this.nameInPlat = nameInPlat;
	}

	public void resetPlatAdnIdInPlat() {
		this.plat = null;
		this.idInPlat = null;
		this.nameInPlat = null;
	}

	public String getMobileId() {
		return mobileId;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public void setMobileId(String mobileId) {
		this.mobileId = mobileId;
	}

	public String getNameInPlat() {
		return nameInPlat;
	}

	public void setNameInPlat(String nameInPlat) {
		this.nameInPlat = nameInPlat;
	}

	public String getIdInPlat() {
		return idInPlat;
	}

	public void setIdInPlat(String idInPlat) {
		this.idInPlat = idInPlat;
	}

	public String getPlat() {
		return plat;
	}

	public void setPlat(String plat) {
		this.plat = plat;
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

	public String readArea() {
		return device.substring(device.lastIndexOf("|") + 1);
	}

}
