package gamecore.db;

import javolution.util.FastTable;
import server.node.dao.AccountDao;
import server.node.dao.BergDao;
import server.node.dao.ChipDao;
import server.node.dao.ColorDao;
import server.node.dao.DailyJobDao;
import server.node.dao.ExpPartDao;
import server.node.dao.FeedbackDao;
import server.node.dao.FriendDao;
import server.node.dao.GiftDao;
import server.node.dao.HandbookDao;
import server.node.dao.LogDao;
import server.node.dao.MissionDao;
import server.node.dao.MonthCardDao;
import server.node.dao.NoticeDao;
import server.node.dao.PartDao;
import server.node.dao.PlayerDao;
import server.node.dao.RechargePackageDao;
import server.node.dao.RecordDao;
import server.node.dao.RentOrderDao;
import server.node.dao.RobotDao;
import server.node.dao.RobotSlotDao;
import server.node.dao.TaskDao;
import server.node.dao.ToturialDao;

/**
 * DAO 工厂。
 */
public final class DaoFactory {

	private static DaoFactory instance = new DaoFactory();

	private FastTable<LogDao> logDaos;
	private FastTable<AccountDao> accountDaos;
	private FastTable<PlayerDao> playerDaos;
	private FastTable<RobotDao> robotDaos;
	private FastTable<RobotSlotDao> robotSlotDaos;
	private FastTable<PartDao> partDaos;
	private FastTable<ExpPartDao> expPartDaos;
	private FastTable<RentOrderDao> rentOrderDaos;
	private FastTable<MissionDao> missionDaos;
	private FastTable<ColorDao> colorDaos;
	private FastTable<ChipDao> chipDaos;
	private FastTable<BergDao> bergDaos;
	private FastTable<RecordDao> recordDaos;
	private FastTable<TaskDao> taskDaos;
	private FastTable<DailyJobDao> dailyJobDaos;
	private FastTable<ToturialDao> toturialDaos;
	private FastTable<FriendDao> friendDaos;
	private FastTable<GiftDao> giftDaos;
	private FastTable<FeedbackDao> feedbackDaos;
	private FastTable<NoticeDao> noticeDaos;
	private FastTable<HandbookDao> handbookDaos;
	private FastTable<MonthCardDao> monthCardDaos;
	private FastTable<RechargePackageDao> rechargePackageDaos;

	private DaoFactory() {
		this.logDaos = new FastTable<LogDao>();
		this.accountDaos = new FastTable<AccountDao>();
		this.playerDaos = new FastTable<PlayerDao>();
		this.robotDaos = new FastTable<RobotDao>();
		this.robotSlotDaos = new FastTable<RobotSlotDao>();
		this.partDaos = new FastTable<PartDao>();
		this.expPartDaos = new FastTable<ExpPartDao>();
		this.rentOrderDaos = new FastTable<RentOrderDao>();
		this.missionDaos = new FastTable<MissionDao>();
		this.colorDaos = new FastTable<ColorDao>();
		this.chipDaos = new FastTable<ChipDao>();
		this.bergDaos = new FastTable<BergDao>();
		this.recordDaos = new FastTable<RecordDao>();
		this.taskDaos = new FastTable<TaskDao>();
		this.dailyJobDaos = new FastTable<DailyJobDao>();
		this.toturialDaos = new FastTable<ToturialDao>();
		this.friendDaos = new FastTable<FriendDao>();
		this.giftDaos = new FastTable<GiftDao>();
		this.feedbackDaos = new FastTable<FeedbackDao>();
		this.noticeDaos = new FastTable<NoticeDao>();
		this.handbookDaos = new FastTable<HandbookDao>();
		this.monthCardDaos = new FastTable<MonthCardDao>();
		this.rechargePackageDaos = new FastTable<RechargePackageDao>();
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

	public RobotDao borrowRobotDao() {
		synchronized (this.robotDaos) {
			if (robotDaos.isEmpty()) {
				return new RobotDao();
			} else {
				return robotDaos.removeFirst();
			}
		}
	}

	public void returnRobotDao(RobotDao robotDao) {
		synchronized (this.robotDaos) {
			this.robotDaos.add(robotDao);
		}
	}

	public RobotSlotDao borrowRobotSlotDao() {
		synchronized (this.robotSlotDaos) {
			if (robotSlotDaos.isEmpty()) {
				return new RobotSlotDao();
			} else {
				return robotSlotDaos.removeFirst();
			}
		}
	}

	public void returnRobotSlotDao(RobotSlotDao robotSlotDao) {
		synchronized (this.robotSlotDaos) {
			this.robotSlotDaos.add(robotSlotDao);
		}
	}

	public PartDao borrowPartDao() {
		synchronized (this.partDaos) {
			if (partDaos.isEmpty()) {
				return new PartDao();
			} else {
				return partDaos.removeFirst();
			}
		}
	}

	public void returnPartDao(PartDao partDao) {
		synchronized (this.partDaos) {
			this.partDaos.add(partDao);
		}
	}

	public ExpPartDao borrowExpPartDao() {
		synchronized (this.expPartDaos) {
			if (expPartDaos.isEmpty()) {
				return new ExpPartDao();
			} else {
				return expPartDaos.removeFirst();
			}
		}
	}

	public void returnExpPartDao(ExpPartDao expPartDao) {
		synchronized (this.expPartDaos) {
			this.expPartDaos.add(expPartDao);
		}
	}

	public RentOrderDao borrowRentOrderDao() {
		synchronized (this.rentOrderDaos) {
			if (rentOrderDaos.isEmpty()) {
				return new RentOrderDao();
			} else {
				return rentOrderDaos.removeFirst();
			}
		}
	}

	public void returnRentOrderDao(RentOrderDao rentOrderDao) {
		synchronized (this.rentOrderDaos) {
			this.rentOrderDaos.add(rentOrderDao);
		}
	}

	public MissionDao borrowMissionDao() {
		synchronized (this.missionDaos) {
			if (missionDaos.isEmpty()) {
				return new MissionDao();
			} else {
				return missionDaos.removeFirst();
			}
		}
	}

	public void returnMissionDao(MissionDao missionDao) {
		synchronized (this.missionDaos) {
			this.missionDaos.add(missionDao);
		}
	}

	public ColorDao borrowColorDao() {
		synchronized (this.colorDaos) {
			if (colorDaos.isEmpty()) {
				return new ColorDao();
			} else {
				return colorDaos.removeFirst();
			}
		}
	}

	public void returnColorDao(ColorDao colorDao) {
		synchronized (this.colorDaos) {
			this.colorDaos.add(colorDao);
		}
	}

	public ChipDao borrowChipDao() {
		synchronized (this.chipDaos) {
			if (chipDaos.isEmpty()) {
				return new ChipDao();
			} else {
				return chipDaos.removeFirst();
			}
		}
	}

	public void returnChipDao(ChipDao chipDao) {
		synchronized (this.chipDaos) {
			this.chipDaos.add(chipDao);
		}
	}

	public BergDao borrowBergDao() {
		synchronized (this.bergDaos) {
			if (bergDaos.isEmpty()) {
				return new BergDao();
			} else {
				return bergDaos.removeFirst();
			}
		}
	}

	public void returnBergDao(BergDao bergDao) {
		synchronized (this.bergDaos) {
			this.bergDaos.add(bergDao);
		}
	}

	public RecordDao borrowRecordDao() {
		synchronized (this.recordDaos) {
			if (recordDaos.isEmpty()) {
				return new RecordDao();
			} else {
				return recordDaos.removeFirst();
			}
		}
	}

	public void returnRecordDao(RecordDao recordDao) {
		synchronized (this.recordDaos) {
			this.recordDaos.add(recordDao);
		}
	}

	public TaskDao borrowTaskDao() {
		synchronized (this.taskDaos) {
			if (taskDaos.isEmpty()) {
				return new TaskDao();
			} else {
				return taskDaos.removeFirst();
			}
		}
	}

	public void returnTaskDao(TaskDao taskDao) {
		synchronized (this.taskDaos) {
			this.taskDaos.add(taskDao);
		}
	}

	public DailyJobDao borrowDailyJobDao() {
		synchronized (this.dailyJobDaos) {
			if (dailyJobDaos.isEmpty()) {
				return new DailyJobDao();
			} else {
				return dailyJobDaos.removeFirst();
			}
		}
	}

	public void returnDailyJobDao(DailyJobDao dailyJobDao) {
		synchronized (this.dailyJobDaos) {
			this.dailyJobDaos.add(dailyJobDao);
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

	public HandbookDao borrowHandbookDao() {
		synchronized (this.handbookDaos) {
			if (handbookDaos.isEmpty()) {
				return new HandbookDao();
			} else {
				return handbookDaos.removeFirst();
			}
		}
	}

	public void returnHandbookDao(HandbookDao handbookDao) {
		synchronized (this.handbookDaos) {
			this.handbookDaos.add(handbookDao);
		}
	}

	public MonthCardDao borrowMonthCardDao() {
		synchronized (this.monthCardDaos) {
			if (monthCardDaos.isEmpty()) {
				return new MonthCardDao();
			} else {
				return monthCardDaos.removeFirst();
			}
		}
	}

	public void returnMonthCardDao(MonthCardDao monthCardDao) {
		synchronized (this.monthCardDaos) {
			this.monthCardDaos.add(monthCardDao);
		}
	}

	public RechargePackageDao borrowRechargePackageDao() {
		synchronized (this.rechargePackageDaos) {
			if (rechargePackageDaos.isEmpty()) {
				return new RechargePackageDao();
			} else {
				return rechargePackageDaos.removeFirst();
			}
		}
	}

	public void returnRechargePackageDao(RechargePackageDao rechargePackageDao) {
		synchronized (this.rechargePackageDaos) {
			this.rechargePackageDaos.add(rechargePackageDao);
		}
	}
}
