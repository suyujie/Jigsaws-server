package server.node.system.player;

import gamecore.entity.AbstractEntity;
import server.node.system.account.Account;

/**
 * 要更换的玩家档案
 */
public class PlayerChangeBean extends AbstractEntity {

	private static final long serialVersionUID = -5078571350527464521L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "player_change_";

	private Account account_old;
	private Account account_new;
	private Player player_old;
	private Player player_new;

	public PlayerChangeBean() {
	}

	public PlayerChangeBean(Account account_old, Account account_new, Player player_old, Player player_new) {
		super(generateCacheKey(player_new.getId()));
		this.account_old = account_old;
		this.account_new = account_new;
		this.player_old = player_old;
		this.player_new = player_new;
	}

	public Account getAccount_old() {
		return account_old;
	}

	public void setAccount_old(Account account_old) {
		this.account_old = account_old;
	}

	public Account getAccount_new() {
		return account_new;
	}

	public void setAccount_new(Account account_new) {
		this.account_new = account_new;
	}

	public Player getPlayer_old() {
		return player_old;
	}

	public void setPlayer_old(Player player_old) {
		this.player_old = player_old;
	}

	public Player getPlayer_new() {
		return player_new;
	}

	public void setPlayer_new(Player player_new) {
		this.player_new = player_new;
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long playerId) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(PlayerChangeBean.CKPrefix).append(playerId).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
