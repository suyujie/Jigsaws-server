package server.node.system.notice;

import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import gamecore.util.Clock;
import gamecore.util.NetAccessTool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpMethod;

import server.node.dao.DaoFactory;
import server.node.dao.NoticeDao;
import server.node.system.ConfigManager;
import server.node.system.RedisHelperJson;
import server.node.system.Root;
import server.node.system.player.GoldType;
import server.node.system.player.Player;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.language.LangType;

/**
 * 公告消息系统
 */
public final class NoticeSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(NoticeSystem.class.getName());

	private static Map<Integer, PublicNotice> publicNotices = new HashMap<Integer, PublicNotice>();

	@Override
	public boolean startup() {
		System.out.println("NoticeSystem start....");

		//读取公共信息,去web读取json信息来获得
		//boolean b = readPublicNotices();

		System.out.println("NoticeSystem start....OK");

		return true;
	}

	@Override
	public void shutdown() {
	}

	//读取公共信息,去web读取json信息来获得
	public boolean readPublicNotices() {

		//清理掉原来的
		publicNotices.clear();

		String url = new StringBuffer("http://").append(ConfigManager.getInstance().managerAccessUrl).append("/robot_web/forNode/active_notice").toString();

		String result = NetAccessTool.accessServer(url, null, HttpMethod.GET);

		if (result == null || result.length() == 0) {
			logger.error("access manager_server error");
			return false;
		}

		JSONObject resultJson = JSONObject.parseObject(result);

		JSONArray array = resultJson.getJSONArray("activeNotices");

		for (int i = 0; i < array.size(); i++) {

			JSONObject json = array.getJSONObject(i);

			int id = json.getIntValue("id");
			long beginTime = json.getLongValue("beginTime");
			long endTime = json.getLongValue("endTime");
			int gold = json.getIntValue("gold");
			String picUrl = json.getString("picUrl");
			int readClose = json.getIntValue("readClose");
			int noticeType = json.getIntValue("noticeType");

			String title_en_US = json.getString("title_en_US");
			String title_es_ES = json.getString("title_es_ES");
			String title_zh_CN = json.getString("title_zh_CN");
			HashMap<LangType, String> title = new HashMap<LangType, String>();
			title.put(LangType.en_US, title_en_US);
			title.put(LangType.es_ES, title_es_ES);
			title.put(LangType.zh_CN, title_zh_CN);
			String cont_en_US = json.getString("cont_en_US");
			String cont_es_ES = json.getString("cont_es_ES");
			String cont_zh_CN = json.getString("cont_zh_CN");
			HashMap<LangType, String> cont = new HashMap<LangType, String>();
			cont.put(LangType.en_US, cont_en_US);
			cont.put(LangType.es_ES, cont_es_ES);
			cont.put(LangType.zh_CN, cont_zh_CN);

			PublicNotice publicNotice = new PublicNotice(id, noticeType, title, cont, picUrl, gold, readClose, beginTime, endTime);

			publicNotices.put(id, publicNotice);

		}

		return true;
	}

	public NoticeBag getNoticeBag(Player player) throws SQLException {
		NoticeBag noticeBag = RedisHelperJson.getNoticeBag(player.getId());
		if (noticeBag == null) {
			noticeBag = readNoticeBagFromDB(player);
			noticeBag.synchronize();
		}

		boolean deleteId = false;

		if (noticeBag.getReadedIds() != null) {
			Iterator<Integer> readedIds = noticeBag.getReadedIds().iterator();
			while (readedIds.hasNext()) {
				Integer id = readedIds.next();
				if (!publicNotices.containsKey(id)) {//没有,表示已经过期的,可以删掉了
					readedIds.remove();
					deleteId = true;
				}
			}
		}

		if (noticeBag.getSendedIds() != null) {
			Iterator<Integer> sendedIds = noticeBag.getSendedIds().iterator();
			while (sendedIds.hasNext()) {
				Integer id = sendedIds.next();
				if (!publicNotices.containsKey(id)) {//没有,表示已经过期的,可以删掉了
					sendedIds.remove();
					deleteId = true;
				}
			}
		}

		if (deleteId) {//有删掉的,需要更新缓存和服务器
			noticeBag.synchronize();
			updateDB(player, noticeBag);
		}

		return noticeBag;
	}

	//数据库读取
	private NoticeBag readNoticeBagFromDB(Player player) throws SQLException {
		NoticeBag noticeBag = null;

		NoticeDao noticeDao = DaoFactory.getInstance().borrowNoticeDao();
		Map<String, Object> map = noticeDao.readNoticeBag(player);
		DaoFactory.getInstance().returnNoticeDao(noticeDao);

		if (map != null) {
			try {
				String readed_ids = (String) map.get("readed_ids");
				List<Integer> readIds = new ArrayList<Integer>();

				if (readed_ids != null && readed_ids.length() > 0) {
					for (String s : readed_ids.split("-")) {
						if (s != null && s.length() > 0) {
							readIds.add(Integer.parseInt(s));
						}
					}
				}

				String sended_ids = (String) map.get("sended_ids");
				List<Integer> sendIds = new ArrayList<Integer>();

				if (sended_ids != null && sended_ids.length() > 0) {
					for (String s : sended_ids.split("-")) {
						if (s != null && s.length() > 0) {
							sendIds.add(Integer.parseInt(s));
						}
					}
				}

				noticeBag = new NoticeBag(player.getId(), readIds, sendIds);

			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			noticeBag = initNoticeBag(player);
		}

		return noticeBag;
	}

	//初始化
	private NoticeBag initNoticeBag(Player player) {
		NoticeBag noticeBag = new NoticeBag(player.getId());

		NoticeDao noticeDao = DaoFactory.getInstance().borrowNoticeDao();
		noticeDao.saveNoticeBag(player, noticeBag);
		DaoFactory.getInstance().returnNoticeDao(noticeDao);

		noticeBag.synchronize();

		return noticeBag;
	}

	//新任务更新消息
	public void addPrivateNoticeNewJob(Player player) throws SQLException {
		NoticeBag noticeBag = getNoticeBag(player);
		noticeBag.setNewPrivateJob(NoticeType.NEW_DAILYJOB);
		noticeBag.synchronize();
	}

	//不在线挨打消息
	public void addPrivateNoticeBeBeat(Player player) throws SQLException {
		NoticeBag noticeBag = getNoticeBag(player);
		noticeBag.setNewPrivateJob(NoticeType.DEFENCE);
		noticeBag.synchronize();
	}

	//获取可显示的public notice
	public List<PublicNotice> getPublicNotices(Player player, NoticeBag noticeBag) throws SQLException {

		if (noticeBag == null) {
			noticeBag = getNoticeBag(player);
		}

		List<PublicNotice> list = new ArrayList<PublicNotice>();

		for (PublicNotice publicNotice : publicNotices.values()) {
			long nowT = Clock.currentTimeMillis();
			if (nowT >= publicNotice.getBeginTime() && nowT <= publicNotice.getEndTime()) {//有效期内的
				//非阅后即焚   或者  阅后即焚没有读过
				if (publicNotice.getIsReadClose() == 0 || (publicNotice.getIsReadClose() == 1 && !noticeBag.getReadedIds().contains(publicNotice.getId()))) {
					list.add(publicNotice);
				}
			}

		}

		return list;
	}

	//获取可显示的private notice
	public List<NoticeType> getPrivateNotices(Player player, NoticeBag noticeBag) throws SQLException {

		if (noticeBag == null) {
			noticeBag = getNoticeBag(player);
		}

		List<NoticeType> list = new ArrayList<NoticeType>();

		if (noticeBag != null && noticeBag.getPrivateNotices() != null) {
			if (noticeBag.getPrivateNotices().get(NoticeType.DEFENCE) != null && noticeBag.getPrivateNotices().get(NoticeType.DEFENCE) == 1) {
				list.add(NoticeType.DEFENCE);
			}

			if (noticeBag.getPrivateNotices().get(NoticeType.NEW_DAILYJOB) != null && noticeBag.getPrivateNotices().get(NoticeType.NEW_DAILYJOB) == 1) {
				list.add(NoticeType.NEW_DAILYJOB);
			}
		}

		return list;
	}

	//已发送过的消息
	public void sendPublicNotices(Player player, NoticeBag noticeBag, List<Integer> noticeIds) throws SQLException {

		if (noticeBag == null) {
			noticeBag = getNoticeBag(player);
		}

		boolean change = false;

		for (Integer id : noticeIds) {
			if (!noticeBag.getSendedIds().contains(id)) {
				noticeBag.getSendedIds().add(id);
				change = true;
			}
		}

		if (change) {
			noticeBag.synchronize();
			updateDB(player, noticeBag);
		}

	}

	//已发送过的消息
	public void sendPrivateNotices(Player player, NoticeBag noticeBag, List<NoticeType> noticeTypes) throws SQLException {

		if (noticeBag == null) {
			noticeBag = getNoticeBag(player);
		}

		boolean change = false;

		for (NoticeType noticeType : noticeTypes) {
			noticeBag.getPrivateNotices().put(noticeType, 2);
			change = true;
		}

		if (change) {
			noticeBag.synchronize();
		}

	}

	//读消息
	public SystemResult readNotice(Player player, int noticeId) throws SQLException {
		SystemResult result = new SystemResult();
		NoticeBag noticeBag = getNoticeBag(player);

		if (noticeId <= 2) {//这时候noticeId 实际上是noticeType,只有私有消息才这样做

			if (noticeId == NoticeType.NEW_DAILYJOB.asCode()) {//新任务
				noticeBag.getPrivateNotices().put(NoticeType.NEW_DAILYJOB, 3);
			}
			if (noticeId == NoticeType.DEFENCE.asCode()) {//被打
				noticeBag.getPrivateNotices().put(NoticeType.DEFENCE, 3);
			}
			noticeBag.synchronize();

		} else {//公有消息,得看id了
			PublicNotice publicNotice = null;
			for (PublicNotice pn : publicNotices.values()) {
				if (pn.getId() == noticeId) {
					publicNotice = pn;
				}
			}

			if (publicNotice != null) {
				//未读
				if (!noticeBag.getReadedIds().contains(noticeId)) {

					if (publicNotice.getGold() > 0) {//有gold
						Root.playerSystem.changeGold(player, publicNotice.getGold(), GoldType.NOTICE_GIVE, true);
					}
					//标记已读
					noticeBag.getReadedIds().add(noticeId);

					noticeBag.synchronize();
					updateDB(player, noticeBag);

				}
			}
		}

		return result;

	}

	private void updateDB(Player player, NoticeBag noticeBag) {
		NoticeDao noticeDao = DaoFactory.getInstance().borrowNoticeDao();
		noticeDao.updateNoticeBag(player, noticeBag);
		DaoFactory.getInstance().returnNoticeDao(noticeDao);
	}
}
