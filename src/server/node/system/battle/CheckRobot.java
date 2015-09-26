package server.node.system.battle;

import gamecore.io.ByteArrayGameInput;

import java.util.ArrayList;
import java.util.List;

public class CheckRobot {

	private long playerId;
	private int slot;
	private int atk;
	private int def;
	private int hp;
	private int crit;
	private List<CheckBuffer> checkBuffers;

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public void setCheckBuffers(List<CheckBuffer> checkBuffers) {
		this.checkBuffers = checkBuffers;
	}

	public int getSlot() {
		return slot;
	}

	public int getAtk() {
		return atk;
	}

	public int getDef() {
		return def;
	}

	public int getHp() {
		return hp;
	}

	public int getCrit() {
		return crit;
	}

	public List<CheckBuffer> getCheckBuffers() {
		return checkBuffers;
	}

	public CheckRobot(long playerId, int slot, int atk, int def, int hp, int crit, List<CheckBuffer> checkBuffers) {
		super();
		this.playerId = playerId;
		this.slot = slot;
		this.atk = atk;
		this.def = def;
		this.hp = hp;
		this.crit = crit;
		this.checkBuffers = checkBuffers;
	}

	public static CheckRobot read(ByteArrayGameInput arrayGameInput) {

		long playerId = arrayGameInput.getLong();

		int slot = arrayGameInput.getInt();
		int atk = arrayGameInput.getInt();
		int def = arrayGameInput.getInt();
		int hp = arrayGameInput.getInt();
		int crit = arrayGameInput.getInt();

		List<CheckBuffer> checkBuffers = new ArrayList<CheckBuffer>();
		int bufferNum = arrayGameInput.getInt();
		for (int i = 0; i < bufferNum; i++) {
			CheckBuffer checkBuffer = CheckBuffer.read(arrayGameInput);
			checkBuffers.add(checkBuffer);
		}

		CheckRobot checkRobot = new CheckRobot(playerId, slot, atk, def, hp, crit, checkBuffers);

		return checkRobot;

	}
}
