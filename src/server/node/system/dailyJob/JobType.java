package server.node.system.dailyJob;

public enum JobType {

	//	1	旗开得胜	取得一场战斗胜利。
	//	2	越战越勇	进行三场比赛。
	//	3	蓄力高手	战斗中使用10次蓄力攻击击中敌人。
	//	4	能量溢出	战斗中使用5次超必杀击中敌人。
	//	5	武器大师	使用X系武器参加一场战斗。［可租赁］(这个武器需要随一种，另外特殊限定条件见旁边说明)
	//	6	互相帮助	为好友送n点体力。
	//	7	超级杀手	使用超必杀摧毁一名敌人。
	//	8	冷血杀手	使用蓄力攻击摧毁一名敌人。
	//	9	亲友同乐	使用FB分享一次战果。
	//	11	节节高升	升级一个零件。
	//	13	竞技达人	进行三次pvp对战。
	//	14	租赁机甲	租赁机器人参加一次战斗
	//	15	机甲商人	出租的机器人被别人使用一次。
	//	16			获得一个部件。（不包括能量块）
	//	17			进行一次修理。
	//	18			参观一次其他玩家基地。(PVP进场不算，好友和log算)。
	//	19			使用一次喷灌进行涂装改色。
	//	20			在任意一场战斗中无伤胜利。
	//	21			获得两场竞技对战胜利。
	//	22			竞技对战摧毁三名XX武器的敌人。
	//	23			在一场竞技对战中一挑三胜利。

	WinOne(1), MoreFight(2), XuLiHit(3), BiShaHit(4), WeaponFight(5), GivePower(6), BishaKill(7), XuLiKill(8), FaceBookShare(9), PartLevelUp(11), PVP(13), HireRobot(14), RentRobot(
			15), PartGet(16), RepaireRobot(17), VisitPlayer(18), Paint(19), WinNoHit(20), PvpWin(21), PvpBeatWeapon(22), Pvp1vs3(23)

	;

	private int sc;

	private JobType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static JobType asEnum(int code) {
		for (JobType jobType : JobType.values()) {
			if (jobType.asCode() == code) {
				return jobType;
			}
		}
		return null;
	}

	public static JobType asEnum(String code) {
		for (JobType jobType : JobType.values()) {
			if (jobType.toString().toUpperCase().equals(code.toUpperCase())) {
				return jobType;
			}
		}
		return null;
	}

}
