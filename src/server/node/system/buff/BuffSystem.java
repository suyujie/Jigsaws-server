package server.node.system.buff;

import gamecore.system.AbstractSystem;
import gamecore.util.Utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.npc.NpcRobot;
import server.node.system.robot.FightProperty;
import server.node.system.robot.Robot;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartSlotType;
import server.node.system.robotPart.QualityLoadData;

/**
 * buff系统。
 */
public final class BuffSystem extends AbstractSystem {

	private static Logger logger = LogManager.getLogger(BuffSystem.class.getName());

	public BuffSystem() {
	}

	@Override
	public boolean startup() {
		// 读取制作数据
		return BuffLoadData.getInstance().readData();
	}

	@Override
	public void shutdown() {
	}

	/**
	 * 说明	
		id	唯一0开始
		type	//效果类型
				1	攻击力增加。
				2	防御力增加。
				3	血量增加。
				4	会心增加。
		useType	//0:基础属性非战斗 1:战斗,2:战斗全队
		values	
		ifType	效果生效条件 编号,0是无条件,1长柄,2重击,3双持,4远程,5盾系,(战斗中肯定无法达成这个条件)
		cont	文字说明只填索引buff1,buff2
		contType	0:不接内容 1:接数字 2:接数字+%
		ifnum	buff的第二个数值。1-value
	 */

	public void buffAddition(Robot robot, FightProperty fightProperty) {
		sameQualityAddition(fightProperty);
		suitBufferAddition(robot, fightProperty);
	}

	//材质加成
	private void sameQualityAddition(FightProperty fightProperty) {
		if (fightProperty.isSameQualityType()) {
			int qualityAddition = QualityLoadData.getInstance().getQualityMaking(fightProperty.getQualityType().asCode()).getSameQualityNum();
			fightProperty.atk = fightProperty.atk * (100 + qualityAddition) / 100 + 1;
			fightProperty.def = fightProperty.def * (100 + qualityAddition) / 100 + 1;
			fightProperty.hp = fightProperty.hp * (100 + qualityAddition) / 100 + 1;
			fightProperty.crit = fightProperty.crit * (100 + qualityAddition) / 100 + 1;
		}
	}

	//套装,只有 战斗  额外增加 钱 增加 经验的buff
	private void suitBufferAddition(Robot robot, FightProperty fightProperty) {
		if (fightProperty.isSameSuitType()) {
			BuffMaking buffMaking = BuffLoadData.getInstance().getSuitBuffMaking(fightProperty.getSuitMakingName());
			if (buffMaking != null) {
				BuffAndValue buffAndValue = new BuffAndValue(buffMaking.getId(), buffMaking.getValues());
				fightProperty.addBuffAndValue(buffAndValue);
			}
		}

	}

	public void buffAddition(NpcRobot npcRobot, FightProperty fightProperty) {
		sameQualityAddition(fightProperty);
		//suitBufferAddition(npcRobot, fightProperty);
	}

	//生成出buff value
	public List<Integer> calculateBuffValue(PartMaking partMaking) {

		if (partMaking.getPartSlotType() == PartSlotType.WEAPON) {

			List<Integer> values = new ArrayList<Integer>();

			if (partMaking.getBufferIdTable() != null && partMaking.getBufferIdTable().size() > 0) {
				for (int i = 0; i < partMaking.getBufferIdTable().size(); i++) {
					Integer bufferId = partMaking.getBufferIdTable().get(i);
					BuffMaking buffMaking = BuffLoadData.getInstance().getMaking(bufferId);
					if (buffMaking != null && buffMaking.getMinvalues() != null && buffMaking.getValues() != null) {
						values.add(Utils.randomInt(buffMaking.getMinvalues(), buffMaking.getValues()));
					} else {
						values.add(0);
					}
				}
			} else {
				logger.error("no bufferIds");
			}

			return values;
		}

		return null;
	}

	//CASH_UP的buff cash 提高多少
	public int cashUpBuff(int cash, List<Robot> robots, Robot hireRobot, NpcRobot npcRobot) {
		int addCashRate = 0;
		for (Robot robot : robots) {
			if (robot != null) {
				FightProperty fightProperty = robot.refreshFightProperty();
				if (fightProperty.getBuffAndValues() != null && fightProperty.getBuffAndValues().size() > 0) {
					for (BuffAndValue buffAndValue : fightProperty.getBuffAndValues()) {
						if (buffAndValue != null) {
							if (buffAndValue.getBuffId().intValue() == BuffType.CASH_UP.asCode()) {
								addCashRate = addCashRate > buffAndValue.getValue() ? addCashRate : buffAndValue.getValue();
							}
						}
					}
				}
			}
		}
		return cash * addCashRate / 100;
	}

	//EXP_UP的buff exp 提高多少
	public int expUpBuff(int exp, List<Robot> robots, Robot hireRobot, NpcRobot npcRobot) {
		int addExpRate = 0;
		for (Robot robot : robots) {
			if (robot != null) {
				FightProperty fightProperty = robot.refreshFightProperty();
				if (fightProperty.getBuffAndValues() != null && fightProperty.getBuffAndValues().size() > 0) {
					for (BuffAndValue buffAndValue : fightProperty.getBuffAndValues()) {
						if (buffAndValue != null) {
							if (buffAndValue.getBuffId().intValue() == BuffType.EXP_UP.asCode()) {
								addExpRate = addExpRate > buffAndValue.getValue() ? addExpRate : buffAndValue.getValue();
							}
						}
					}
				}
			}
		}
		return exp * addExpRate / 100;
	}

}
