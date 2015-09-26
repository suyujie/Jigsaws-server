package server.node.system.notice;

/** 
 * 通知公告类型
 */
public enum NoticeType {

	//	1：新任务更新（特殊的）
	//	2：不在线挨打（（特殊的））
	//	3：送钻石的的通知
	//	4：普通通知。

	NEW_DAILYJOB(1), DEFENCE(2), GET_GOLD(3), NORMAL(4);

	private int sc;

	private NoticeType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static NoticeType asEnum(int code) {
		for (NoticeType partType : NoticeType.values()) {
			if (partType.asCode() == code) {
				return partType;
			}
		}
		return null;
	}

}
