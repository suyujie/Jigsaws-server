package server.node.dao;

import javolution.util.FastTable;

/**
 * DAO 工厂。
 */
public final class DaoFactory {

	private static DaoFactory instance = new DaoFactory();

	private FastTable<LogDao> logDaos;
	private FastTable<AccountDao> accountDaos;
	private FastTable<PlayerDao> playerDaos;
	private FastTable<ToturialDao> toturialDaos;
	private FastTable<FriendDao> friendDaos;
	private FastTable<GiftDao> giftDaos;
	private FastTable<FeedbackDao> feedbackDaos;
	private FastTable<NoticeDao> noticeDaos;

	private DaoFactory() {
		this.logDaos = new FastTable<LogDao>();
		this.accountDaos = new FastTable<AccountDao>();
		this.playerDaos = new FastTable<PlayerDao>();
		this.toturialDaos = new FastTable<ToturialDao>();
		this.friendDaos = new FastTable<FriendDao>();
		this.giftDaos = new FastTable<GiftDao>();
		this.feedbackDaos = new FastTable<FeedbackDao>();
		this.noticeDaos = new FastTable<NoticeDao>();
	}

	public static DaoFactory getInstance() {
		return DaoFactory.instance;
	}

	public LogDao borrowLogDao() {
		synchronized (this.logDaos) {
			if (logDaos.isEmpty()) {
				return new LogDao();
			} else {
				return logDaos.removeFirst();
			}
		}
	}

	public void returnLogDao(LogDao logDao) {
		synchronized (this.logDaos) {
			this.logDaos.add(logDao);
		}
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

	public ToturialDao borrowToturialDao() {
		synchronized (this.toturialDaos) {
			if (toturialDaos.isEmpty()) {
				return new ToturialDao();
			} else {
				return toturialDaos.removeFirst();
			}
		}
	}

	public void returnToturialDao(ToturialDao toturialDao) {
		synchronized (this.toturialDaos) {
			this.toturialDaos.add(toturialDao);
		}
	}

	public FriendDao borrowFriendDao() {
		synchronized (this.friendDaos) {
			if (friendDaos.isEmpty()) {
				return new FriendDao();
			} else {
				return friendDaos.removeFirst();
			}
		}
	}

	public void returnFriendDao(FriendDao friendDao) {
		synchronized (this.friendDaos) {
			this.friendDaos.add(friendDao);
		}
	}

	public GiftDao borrowGiftDao() {
		synchronized (this.giftDaos) {
			if (giftDaos.isEmpty()) {
				return new GiftDao();
			} else {
				return giftDaos.removeFirst();
			}
		}
	}

	public void returnGiftDao(GiftDao giftDao) {
		synchronized (this.giftDaos) {
			this.giftDaos.add(giftDao);
		}
	}

	public FeedbackDao borrowFeedbackDao() {
		synchronized (this.feedbackDaos) {
			if (feedbackDaos.isEmpty()) {
				return new FeedbackDao();
			} else {
				return feedbackDaos.removeFirst();
			}
		}
	}

	public void returnFeedbackDao(FeedbackDao feedbackDao) {
		synchronized (this.feedbackDaos) {
			this.feedbackDaos.add(feedbackDao);
		}
	}

	public NoticeDao borrowNoticeDao() {
		synchronized (this.noticeDaos) {
			if (noticeDaos.isEmpty()) {
				return new NoticeDao();
			} else {
				return noticeDaos.removeFirst();
			}
		}
	}

	public void returnNoticeDao(NoticeDao noticeDao) {
		synchronized (this.noticeDaos) {
			this.noticeDaos.add(noticeDao);
		}
	}

}
