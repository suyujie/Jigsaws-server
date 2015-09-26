package server.node.system.player;

import gamecore.cache.redis.RedisHelperJson;
import gamecore.entity.AbstractEntity;
import gamecore.util.Clock;
import server.node.system.Content;
import server.node.system.account.Account;
import server.node.system.session.Session;

import common.language.LangType;

/**
 * 玩家角色实体。
 */
public class Player extends AbstractEntity {

	private static final long serialVersionUID = -2178417928622203060L;

	private static final StringBuilder ckBuf = new StringBuilder();

	public final static String CKPrefix = "player_";

	private Long id;
	private int gold;
	private long cash;
	private int exp;
	private int level;
	private long protectEndTime;//护盾失效时间
	private String pushUri;//推送地址
	private int onLine;//1在线,0不在线
	private Long onLineTime;//在线时长
	private Long lastSignT;//登陆时间
	private Long signLogId;//登陆日志的id
	private byte[] security;//密码
	private int haveImg;//是否有头像
	private LangType lang;//语言

	private PlayerStatistics playerStatistics;

	private Account account;

	public Player() {
	}

	public Player(Long id) {
		super(Player.generateCacheKey(id));
		this.id = id;
	}

	public Player(Long id, int level, int exp) {
		super(Player.generateCacheKey(id));
		this.id = id;
		this.level = level;
		this.exp = exp;
	}

	public Player(Long id, int gold, long cash, int exp, int level, long protectEndTime, String pushUri, int onLine, long onLineTime, int haveImg, LangType lang,
			PlayerStatistics playerStatistics) {
		super(Player.generateCacheKey(id));
		this.id = id;
		this.gold = gold;
		this.cash = cash;
		this.exp = exp;
		this.level = level;
		this.protectEndTime = protectEndTime;
		this.pushUri = pushUri;
		this.onLine = onLine;
		this.onLineTime = onLineTime;
		this.haveImg = haveImg;
		this.lang = lang;
		this.playerStatistics = playerStatistics;
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

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public long getCash() {
		return cash;
	}

	public void setCash(long cash) {
		this.cash = cash;
	}

	public long getProtectEndTime() {
		return protectEndTime;
	}

	public void setProtectEndTime(long protectEndTime) {
		this.protectEndTime = protectEndTime;
	}

	public int getOnLine() {
		return onLine;
	}

	public void setOnLine(int onLine) {
		this.onLine = onLine;
	}

	public int getHaveImg() {
		return haveImg;
	}

	public void setHaveImg(int haveImg) {
		this.haveImg = haveImg;
	}

	public PlayerStatistics getPlayerStatistics() {
		return playerStatistics;
	}

	public void setPlayerStatistics(PlayerStatistics playerStatistics) {
		this.playerStatistics = playerStatistics;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getPushUri() {
		return pushUri;
	}

	public void setPushUri(String pushUri) {
		this.pushUri = pushUri;
	}

	public long getOnLineTime() {
		return onLineTime;
	}

	public void setOnLineTime(long onLineTime) {
		this.onLineTime = onLineTime;
	}

	public Long getLastSignT() {
		return lastSignT;
	}

	public void setLastSignT(Long lastSignT) {
		this.lastSignT = lastSignT;
	}

	public Long getSignLogId() {
		return signLogId;
	}

	public void setSignLogId(Long signLogId) {
		this.signLogId = signLogId;
	}

	public void setOnLineTime(Long onLineTime) {
		this.onLineTime = onLineTime;
	}

	public LangType getLang() {
		if (lang == null) {
			return LangType.en_US;
		}
		return lang;
	}

	public void setLang(LangType lang) {
		this.lang = lang;
	}

	public byte[] getSecurity() {
		return security;
	}

	public void setSecurity(byte[] security) {
		this.security = security;
	}

	public int getProtectTime() {
		if (protectEndTime == 0) {
			return 0;
		} else {
			long t = protectEndTime - Clock.currentTimeSecond();
			if (t > 0) {
				return (int) t;
			} else {
				return 0;
			}
		}
	}

	public boolean checkOnLine() {
		Session session = RedisHelperJson.getSession(account.getMobileId());
		if (session != null) {
			return Clock.currentTimeSecond() - session.getActiveT() < Content.HeartBeatOffLine * Content.HeartBeatTimePeriod;
		}
		return false;
	}

	public boolean checkProtect() {
		return Clock.currentTimeSecond() < getProtectEndTime();
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
