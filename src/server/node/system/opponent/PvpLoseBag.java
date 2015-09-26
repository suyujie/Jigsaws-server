package server.node.system.opponent;

import gamecore.entity.AbstractEntity;

/**
 * pvp损失统计
 */
public class PvpLoseBag extends AbstractEntity {

	private static final long serialVersionUID = -8005516789670762275L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "pvp_lose_";

	private int partNum;//损失的能量块数量
	private int cash;//损失的cash

	public PvpLoseBag() {
	}

	public PvpLoseBag(long playerId) {
		super(PvpLoseBag.generateCacheKey(playerId));
		partNum = 0;
		cash = 0;
	}

	public int getPartNum() {
		return partNum;
	}

	public void setPartNum(int partNum) {
		this.partNum = partNum;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PvpLoseBag.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
