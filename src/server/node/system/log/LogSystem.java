package server.node.system.log;

import gamecore.system.AbstractSystem;
import gamecore.util.Clock;
import gamecore.util.Utils;
import server.node.dao.DaoFactory;
import server.node.dao.LogDao;
import server.node.system.player.Player;

public class LogSystem extends AbstractSystem {

	public LogSystem() {
		super();
	}

	@Override
	public boolean startup() {
		System.out.println("LogSystem start....");

		System.out.println("LogSystem start....OK");
		return true;
	}

	@Override
	public void shutdown() {
	}

	private long getLogId() {
		return Utils.getOneLongId();
	}

	public void addSignLog(Player player, boolean sync) {

		Long id = getLogId();
		SignLog signLog = new SignLog(id, player.getId(), player.getLevel(), player.getGold(), player.getCash());

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addSignLog(player, signLog);
		DaoFactory.getInstance().returnLogDao(logDao);

		player.setSignLogId(id);
		player.setLastSignT(Clock.currentTimeSecond());

		if (sync) {
			player.synchronize();
		}

	}

	public void updateSignLog(Player player, boolean sync) {

		SignLog signLog = new SignLog(player.getSignLogId(), player.getLevel(), player.getGold(), player.getCash(), 0);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.updateSignLog(player, signLog);
		DaoFactory.getInstance().returnLogDao(logDao);

		player.setSignLogId(null);
		player.setLastSignT(null);

		if (sync) {
			player.synchronize();
		}
	}

	public void addCashLog(Player player, long beforeCash, long changeCash, long afterCash, int changeTyte) {

		CashLog cashLog = new CashLog(getLogId(), player.getId(), beforeCash, changeCash, afterCash, changeTyte);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addCashLog(player, cashLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

	public void addGoldLog(Player player, long beforeGold, long changeGold, long afterGold, int changeTyte) {

		GoldLog goldLog = new GoldLog(getLogId(), player.getId(), beforeGold, changeGold, afterGold, changeTyte);

		LogDao logDao = DaoFactory.getInstance().borrowLogDao();
		logDao.addGoldLog(player, goldLog);
		DaoFactory.getInstance().returnLogDao(logDao);
	}

}
