package server.node.system.notice;

import java.util.HashMap;

import common.language.LangType;

public class PublicNotice {

	private int id;
	private int noticeType;
	private HashMap<LangType, String> title;
	private HashMap<LangType, String> cont;
	private String picUrl;
	private int gold;
	private int isReadClose;
	private long beginTime;
	private long endTime;

	public PublicNotice(int id, int noticeType, HashMap<LangType, String> title, HashMap<LangType, String> cont, String picUrl, int gold, int isReadClose, long beginTime,
			long endTime) {
		this.id = id;
		this.noticeType = noticeType;
		this.title = title;
		this.cont = cont;
		this.picUrl = picUrl;
		this.gold = gold;
		this.isReadClose = isReadClose;
		this.beginTime = beginTime;
		this.endTime = endTime;
	}

	public int getId() {
		return id;
	}

	public int getNoticeType() {
		return noticeType;
	}

	public String getTitle(LangType lang) {
		return title.get(lang);
	}

	public String getCont(LangType lang) {
		return cont.get(lang);
	}

	public String getPicUrl() {
		return picUrl;
	}

	public int getGold() {
		return gold;
	}

	public int getIsReadClose() {
		return isReadClose;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

}
