package server.node.dao;

import javolution.util.FastTable;

/**
 * DAO 工厂。
 */
public final class DaoFactory {

	private static DaoFactory instance = new DaoFactory();

	private FastTable<AccountDao> accountDaos;
	private FastTable<PlayerDao> playerDaos;
	private FastTable<JigsawDao> jigsawDaos;

	private DaoFactory() {
		this.accountDaos = new FastTable<AccountDao>();
		this.playerDaos = new FastTable<PlayerDao>();
		this.jigsawDaos = new FastTable<JigsawDao>();
	}

	public static DaoFactory getInstance() {
		return DaoFactory.instance;
	}

	public AccountDao borrowAccountDao() {
		synchronized (this.accountDaos) {
			if (accountDaos.isEmpty()) {
				return new AccountDao();
			} else {
				return accountDaos.removeFirst();
			}
		}
	}

	public void returnAccountDao(AccountDao accountDao) {
		synchronized (this.accountDaos) {
			this.accountDaos.add(accountDao);
		}
	}

	public PlayerDao borrowPlayerDao() {
		synchronized (this.playerDaos) {
			if (playerDaos.isEmpty()) {
				return new PlayerDao();
			} else {
				return playerDaos.removeFirst();
			}
		}
	}

	public void returnPlayerDao(PlayerDao playerDao) {
		synchronized (this.playerDaos) {
			this.playerDaos.add(playerDao);
		}
	}

	public JigsawDao borrowJigsawDao() {
		synchronized (this.jigsawDaos) {
			if (jigsawDaos.isEmpty()) {
				return new JigsawDao();
			} else {
				return jigsawDaos.removeFirst();
			}
		}
	}

	public void returnJigsawDao(JigsawDao jigsawDao) {
		synchronized (this.jigsawDaos) {
			this.jigsawDaos.add(jigsawDao);
		}
	}

}
