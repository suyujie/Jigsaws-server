package server.node.system.record;

import gamecore.io.ByteArrayGameOutput;
import gamecore.util.Clock;
import server.node.system.Root;
import server.node.system.player.Player;

public class DefenceRecord {

	private long id;
	private int type;//0普通战斗记录,1复仇战(被复仇,这个不让再次复仇)
	private int isWin;
	private int revengeNum;
	private long opponentId;
	private int winNum;
	private int changeCup;
	private int changeCash;
	private Integer lootExpId;
	private String lootChipName;
	private Integer lootBergId;
	private int status;
	private long t;//秒

	private Player opponentPlayer;

	public DefenceRecord() {
	}

	public DefenceRecord(long id, int type, int revengeNum, long opponentId, int isWin, int winNum, int changeCup, int changeCash, Integer lootExpId, String lootChipName,
			Integer lootBergId, int status, long t) {
		this.id = id;
		this.type = type;
		this.revengeNum = revengeNum;
		this.opponentId = opponentId;
		this.isWin = isWin;
		this.winNum = winNum;
		this.changeCup = changeCup;
		this.changeCash = changeCash;
		this.lootExpId = lootExpId;
		this.lootChipName = lootChipName;
		this.lootBergId = lootBergId;
		this.status = status;
		this.t = t;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRevengeNum() {
		return revengeNum;
	}

	public void setRevengeNum(int revengeNum) {
		this.revengeNum = revengeNum;
	}

	public long getOpponentId() {
		return opponentId;
	}

	public void setOpponentId(long opponentId) {
		this.opponentId = opponentId;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getT() {
		return t;
	}

	public void setT(long t) {
		this.t = t;
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

			bago.putLong(id);//记录id
			bago.put((byte) revengeNum);//复仇次数
			bago.put((byte) type);//防守记录  复仇类型

			bago.putLong(opponentPlayer.getId());
			bago.putString(opponentPlayer.getAccount().getNameInPlat());
			bago.putShort((short) (opponentPlayer.getLevel()));
			bago.putInt((int) (Clock.currentTimeSecond() - t) / 60);//分钟
			bago.putInt(-changeCash);
			bago.putInt(opponentPlayer.getPlayerStatistics().getCupNum());
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayNotOnLine() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			Player opponent = Root.playerSystem.getPlayer(opponentId);//对手
			bago.putString(opponent.getAccount().getNameInPlat());
			if (isWin == 1) {
				bago.putInt(changeCup);
			} else {
				bago.putInt(-changeCup);
			}
			bago.put((byte) winNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bago.toByteArray();
	}
}
