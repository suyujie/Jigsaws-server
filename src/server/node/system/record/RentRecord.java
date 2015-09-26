package server.node.system.record;

import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;
import server.node.system.player.Player;

public class RentRecord {

	private long id;
	private long tenantId;
	private int changeCash;
	private long t;//秒

	private Player tenantPlayer;

	public RentRecord() {
	}

	public RentRecord(long id, long tenantId, int changeCash, long t) {
		this.id = id;
		this.tenantId = tenantId;
		this.changeCash = changeCash;
		this.t = t;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTenantId() {
		return tenantId;
	}

	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	public int getChangeCash() {
		return changeCash;
	}

	public void setChangeCash(int changeCash) {
		this.changeCash = changeCash;
	}

	public long getT() {
		return t;
	}

	public void setT(long t) {
		this.t = t;
	}

	public Player getTenantPlayer() {
		return tenantPlayer;
	}

	public void setTenantPlayer(Player tenantPlayer) {
		this.tenantPlayer = tenantPlayer;
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			bago.putLong(tenantPlayer.getId());
			bago.putString(tenantPlayer.getAccount().getNameInPlat());
			bago.putShort((short) tenantPlayer.getLevel());
			bago.putInt((int) (Clock.currentTimeSecond() - t) / 60);//分钟
			bago.putInt(changeCash);
			bago.putInt(tenantPlayer.getPlayerStatistics().getCupNum());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bago.toByteArray();
	}
}
