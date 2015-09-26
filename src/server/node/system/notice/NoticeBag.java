package server.node.system.notice;

import gamecore.entity.AbstractEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 公告消息包
 * 私有notice只有两个,一个是被打,一个是新公告,这两个不需要存入数据库
 * 公有notice,存入 已读过,已发送过的id号
 */
public class NoticeBag extends AbstractEntity {

	private static final long serialVersionUID = -7678138620424109154L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "noticebag_";

	//0 没有, 1 有,2 已发送,3 已读.
	private HashMap<NoticeType, Integer> privateNotices = new HashMap<NoticeType, Integer>();

	private List<Integer> readedIds = new ArrayList<Integer>();//已经阅读过的公有消息
	private List<Integer> sendedIds = new ArrayList<Integer>();//给客户端发送过公有消息

	public NoticeBag() {
	}

	public NoticeBag(Long playerId) {
		super(NoticeBag.generateCacheKey(playerId));//玩家playerId当作存储键值
	}

	public NoticeBag(Long playerId, List<Integer> readedIds, List<Integer> sendedIds) {
		super(NoticeBag.generateCacheKey(playerId));//玩家playerId当作存储键值
		this.readedIds = readedIds;
		this.sendedIds = sendedIds;
	}

	public void setNewPrivateJob(NoticeType noticeType) {
		this.privateNotices.put(noticeType, 1);
	}

	public List<Integer> getReadedIds() {
		return readedIds;
	}

	public List<Integer> getSendedIds() {
		return sendedIds;
	}

	public void addReadId(Integer id) {
		if (!readedIds.contains(id)) {
			readedIds.add(id);
		}
	}

	public void addSendId(Integer id) {
		if (!sendedIds.contains(id)) {
			sendedIds.add(id);
		}
	}

	public HashMap<NoticeType, Integer> getPrivateNotices() {
		return privateNotices;
	}

	public void setPrivateNotices(HashMap<NoticeType, Integer> privateNotices) {
		this.privateNotices = privateNotices;
	}

	public void readNotice(int noticeId, boolean sync) {
		if (!readedIds.contains(noticeId)) {
			readedIds.add(noticeId);
			if (sync) {
				this.synchronize();
			}
		}
	}

	public String getReadIdsStr() {
		StringBuffer sb = new StringBuffer();
		for (Integer readedId : readedIds) {
			if (readedId != null) {
				sb.append(readedId).append("-");
			}
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public String getSendIdsStr() {
		StringBuffer sb = new StringBuffer();
		for (Integer sendId : sendedIds) {
			if (sendId != null) {
				sb.append(sendId).append("-");
			}
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 生成存储键。
	 */
	public static String generateCacheKey(Long id) {
		synchronized (ckBuf) {
			String ret = ckBuf.append(NoticeBag.CKPrefix).append(id).toString();
			ckBuf.delete(0, ckBuf.length());
			return ret;
		}
	}

}
