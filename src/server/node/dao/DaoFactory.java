package server.node.dao;

import javolution.util.FastTable;

/**
 * DAO 工厂。
 */
public final class DaoFactory {

	private static DaoFactory instance = new DaoFactory();

	private FastTable<AccountDao> accountDaos;
	private FastTable<PlayerDao> playerDaos;
	private FastTable<ImageDao> imageDaos;

	private DaoFactory() {
		this.accountDaos = new FastTable<AccountDao>();
		this.playerDaos = new FastTable<PlayerDao>();
		this.imageDaos = new FastTable<ImageDao>();
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

	public ImageDao borrowImageDao() {
		synchronized (this.imageDaos) {
			if (imageDaos.isEmpty()) {
				return new ImageDao();
			} else {
				return imageDaos.removeFirst();
			}
		}
	}

	public void returnImageDao(ImageDao imageDao) {
		synchronized (this.imageDaos) {
			this.imageDaos.add(imageDao);
		}
	}

}
