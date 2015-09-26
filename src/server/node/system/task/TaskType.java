package server.node.system.task;

/**
 * task类型
 */
public enum TaskType {

	HomeLevel(0), //		0	运筹帷幄			升级主城至N级
	StarNum(1), //			1	邪恶终结者			在单人模式取得N颗星
	PartNum(2), //			2	收藏家				获得过N个零件
	PartQuality2Num(3), //	3	秘银爱好者			获得过N个银制零件
	PartQuality3Num(4), //	4	黄金爱好者			获得过N个金制零件
	PartQuality4Num(5), //	5	钛爱好者			获得过N个钛制零件
	PvpWinLevel(6), //		6	以小博大			多人游戏中，击败过主城N级的玩家
	PartLevel(7), //		7	改装大师			最高将零件改装至N级
	PartQuality0Level(8), //	8	铁之魂				最高将铁质零件改装至N级
	PartQuality1Level(9), //	9	钢之魂				最高将铜质零件改装至N级
	PartQuality2Level(10), //	10	银之魂				最高将银质零件改装至N级
	PartQuality3Level(11), //	11	金之魂				最高将金质零件改装至N级
	PartQuality4Level(12), //	12	钛之魂				最高将钛质零件改装至N级
	CupNum(13), //			13	常胜将军			最高获得了N个勋章
	AttackWinNum(14), //	14	战无不胜			多人游戏中，进攻对手获得N场胜利
	DefenceWinNum(15), //	15	坚不可摧			多人游戏中，成功防御N波进攻
	PvpBeatRobotNum(16), //	16	毁灭者				多人游戏中，摧毁N个敌方机器人
	PvpBeatRobotWeapon3Num(17), //	17	双持炼狱			多人游戏中，摧毁N个双持机器人
	PvpBeatRobotWeapon1Num(18), //	18	长柄炼狱			多人游戏中，摧毁N个长柄机器人
	PvpBeatRobotWeapon5Num(19), //	19	盾剑炼狱			多人游戏中，摧毁N个盾剑机器人
	PvpBeatRobotWeapon4Num(20), //	20	枪械炼狱			多人游戏中，摧毁N个枪械机器人
	PvpBeatRobotWeapon2Num(21), //	21	重击炼狱			多人游戏中，摧毁N个重击机器人
	PvpBeatRobotSuitNum(22),	 //	22	美貌终结者			多人游戏中，消灭N个外形套装机器人
	PvpBeatRobotQualityNum(23), //	23	健身终结者			多人游戏中，消灭N个材质套装机器人
	PvpBeatRobotQuality2Num(24), //	24	秘银终结者			多人游戏中，消灭N个银质套装机器人
	PvpBeatRobotQuality3Num(25), //	25	黄金终结者			多人游戏中，消灭N个金质套装机器人
	PvpBeatRobotQuality4Num(26), //	26	钛终结者			多人游戏中，消灭N个钛质套装机器人
	HitNum(27), 			//	27	杀戮机器			使用N次大招击中敌人
	PvpGetCash(28), 		//	28	淘金者				盗取N金币
	RentRobotNum(29), 		//	29	造物者				成功出租N个机器人
	HireRobotNum(30),		//	30	机械总动员			租赁N个机器人
	FriendNum(31), 			//	31	正义联盟			好友数量
	GainCashNum(32), 		//	32	黄金矿主			在单人游戏的关卡矿点里收获N金钱
	RobotQualityNum(33), 	//	33	健身爱好者			拼出N种不同的材质套
	WinWithWeapon3Num(34), //	34	双持之魂			出战包含双持武器时，获得N场胜利
	WinWithWeapon2Num(35), //	35	重击之魂			出战包含重击武器时，获得N场胜利
	WinWithWeapon5Num(36), //	36	盾剑之魂			出战包含盾剑武器时，获得N场胜利
	WinWithWeapon4Num(37), //	37	枪械之魂			出战包含枪械武器时，获得N场胜利
	WinWithWeapon1Num(38), //	38	长柄之魂			出战包含长柄武器时，获得N场胜利
	PaintNum(39), 			//	39	五彩缤纷			进行过N次色彩喷涂
	PaintPartQuality0Num(40), //	40	绚丽的铁			共对铁质零件进行过N次色彩喷涂
	PaintPartQuality1Num(41), //	41	绚丽的铜			共对铜质零件进行过N次色彩喷涂
	PaintPartQuality2Num(42), //	42	绚丽的银			共对银质零件进行过N次色彩喷涂
	PaintPartQuality3Num(43), //	43	绚丽的金			共对金质零件进行过N次色彩喷涂
	PaintPartQuality4Num(44), //	44	绚丽的钛			共对钛质零件进行过N次色彩喷涂
	GivePowerNum(45),			//	45	友情至上			对好友赠送N次体力

	;

	private int sc;

	private TaskType(int code) {
		this.sc = code;
	}

	public int asCode() {
		return this.sc;
	}

	public static TaskType asEnum(int code) {
		for (TaskType taskType : TaskType.values()) {
			if (taskType.asCode() == code) {
				return taskType;
			}
		}
		return null;
	}

	public static TaskType asEnum(String code) {
		for (TaskType taskType : TaskType.values()) {
			if (taskType.toString().toUpperCase().equals(code.toUpperCase())) {
				return taskType;
			}
		}
		return null;
	}

}
