package server.node.system.record;

import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;
import gamecore.util.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.npc.NpcPlayer;
import server.node.system.player.Player;

public class AttackRecord {

	private final static Logger logger = LogManager.getLogger(AttackRecord.class);

	private long id;
	private int isWin;
	private long opponentId;
	private int isNpc;
	private int winNum;
	private int changeCup;
	private int changeCash;
	private Integer lootExpId;
	private String lootChipName;
	private Integer lootBergId;
	private long t;//秒

	private NpcPlayer opponentNpcPlayer;
	private Player opponentPlayer;

	public AttackRecord() {
	}

	public AttackRecord(long id, long opponentId, int isNpc, int isWin, int winNum, int changeCup, int changeCash, Integer lootExpId, String lootChipName, Integer lootBergId,
			long t) {
		this.id = id;
		this.opponentId = opponentId;
		this.isNpc = isNpc;
		this.isWin = isWin;
		this.winNum = winNum;
		this.changeCup = changeCup;
		this.changeCash = changeCash;
		this.lootExpId = lootExpId;
		this.lootChipName = lootChipName;
		this.lootBergId = lootBergId;
		this.t = t;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOpponentId() {
		return opponentId;
	}

	public void setOpponentId(long opponentId) {
		this.opponentId = opponentId;
	}

	public int getIsNpc() {
		return isNpc;
	}

	public void setIsNpc(int isNpc) {
		this.isNpc = isNpc;
	}

	public int getIsWin() {
		return isWin;
	}

	public void setIsWin(int isWin) {
		this.isWin = isWin;
	}

	public int getWinNum() {
		return winNum;
	}

	public void setWinNum(int winNum) {
		this.winNum = winNum;
	}

	public int getChangeCup() {
		return changeCup;
	}

	public void setChangeCup(int changeCup) {
		this.changeCup = changeCup;
	}

	public int getChangeCash() {
		return changeCash;
	}

	public void setChangeCash(int changeCash) {
		this.changeCash = changeCash;
	}

	public Integer getLootExpId() {
		return lootExpId;
	}

	public void setLootExpId(Integer lootExpId) {
		this.lootExpId = lootExpId;
	}

	public String getLootChipName() {
		return lootChipName;
	}

	public void setLootChipName(String lootChipName) {
		this.lootChipName = lootChipName;
	}

	public Integer getLootBergId() {
		return lootBergId;
	}

	public void setLootBergId(Integer lootBergId) {
		this.lootBergId = lootBergId;
	}

	public long getT() {
		return t;
	}

	public void setT(long t) {
		this.t = t;
	}

	public NpcPlayer getOpponentNpcPlayer() {
		return opponentNpcPlayer;
	}

	public void setOpponentNpcPlayer(NpcPlayer opponentNpcPlayer) {
		this.opponentNpcPlayer = opponentNpcPlayer;
	}

	public Player getOpponentPlayer() {
		return opponentPlayer;
	}

	public void setOpponentPlayer(Player opponentPlayer) {
		this.opponentPlayer = opponentPlayer;
	}

	public byte[] toByteArray() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {

			if (isNpc == 1) {
				bago.putLong(opponentNpcPlayer.getId());//id
				bago.putString(opponentNpcPlayer.getName());//name
				bago.putShort((short) opponentNpcPlayer.getLevel());//level
				bago.putInt((int) (Clock.currentTimeSecond() - t) / 60);//分钟
				bago.putInt(changeCash);
				bago.putInt(Utils.randomInt(100, 1000));//npc的cup随便点
				if (isWin == 1) {
					bago.putInt(changeCup);
				} else {
					bago.putInt(-changeCup);
				}
				bago.put((byte) isWin);
				bago.putInt(winNum);
				if (lootExpId == null) {//能量块数量
					bago.putInt(0);
				} else {
					bago.putInt(1);//有的话，发数量，目前只要有，就是1
				}
				if (lootBergId == null) {//水晶id
					bago.putInt(-1);//没有的话发-1
				} else {
					bago.putInt(lootBergId);//有的话，发id
				}
				if (lootChipName == null) {//芯片名字
					bago.putString("-1");//没有的话，发-1，
				} else {
					bago.putString(lootChipName);//有的话，发名字
				}

			} else {
				bago.putLong(opponentPlayer != null ? opponentPlayer.getId() : 0);//id
				bago.putString(opponentPlayer != null ? opponentPlayer.getAccount().getNameInPlat() : "testnpc");//name
				bago.putShort((short) (opponentPlayer != null ? opponentPlayer.getLevel() : 22));//level
				bago.putInt((int) (Clock.currentTimeSecond() - t) / 60);//分钟
				bago.putInt(changeCash);
				bago.putInt(opponentPlayer != null ? opponentPlayer.getPlayerStatistics().getCupNum() : Utils.randomInt(100, 1000));//npc的cup随便点
				if (isWin == 1) {
					bago.putInt(changeCup);
				} else {
					bago.putInt(-changeCup);
				}
				bago.put((byte) isWin);
				bago.putInt(winNum);
				if (lootExpId == null) {//能量块数量
					bago.putInt(0);
				} else {
					bago.putInt(1);//有的话，发数量，目前只要有，就是1
				}
				if (lootBergId == null) {//水晶id
					bago.putInt(-1);//没有的话发-1
				} else {
					bago.putInt(lootBergId);//有的话，发id
				}
				if (lootChipName == null) {//芯片名字
					bago.putString("-1");//没有的话，发-1，
				} else {
					bago.putString(lootChipName);//有的话，发名字
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}
}
