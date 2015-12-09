package server.node.system.player;

import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import server.node.system.Content;
import server.node.system.RedisHelperJson;
import server.node.system.account.Account;
import server.node.system.session.Session;

/**
 * 玩家角色实体。
 */
public class Player extends AbstractEntity {

	private static final long serialVersionUID = -2178417928622203060L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "player_";

	private Long id;
	private int exp;
	private int level;

	private Account account;

	private PlayerStatistics statistics;

	public Player() {
	}

	public Player(Long id) {
		super(Player.generateCacheKey(id));
		this.id = id;
	}

	public Player(Long id, int exp, int level) {
		super(Player.generateCacheKey(id));
		this.id = id;
		this.exp = exp;
		this.level = level;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public PlayerStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(PlayerStatistics statistics) {
		this.statistics = statistics;
	}

	public boolean checkOnLine() {
		Session session = RedisHelperJson.getSession(account.getDeviceId());
		if (session != null) {
			return Clock.currentTimeSecond() - session.getActiveT() < Content.SessionTimeOutSec;
		}
		return false;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(Player.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
