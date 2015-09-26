package server.node.system.npc;

import gamecore.io.ByteArrayGameOutput;

import java.io.Serializable;
import java.util.HashMap;

import server.node.system.Root;
import server.node.system.robot.FightProperty;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;
import server.node.system.robotPart.QualityLoadData;

public class NpcRobot implements Serializable {

	private static final long serialVersionUID = 1502303190147791404L;
	private Long id;
	private Integer slot;
	private int score;

	private HashMap<Integer, Part> parts;

	public NpcRobot(HashMap<Integer, Part> parts) {
		this.parts = parts;
	}

	public NpcRobot() {
	}

	public NpcRobot(Long id, Integer slot, HashMap<Integer, Part> parts) {
		this.id = id;
		this.slot = slot;
		this.parts = parts;
		refreshFightProperty();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public HashMap<Integer, Part> getParts() {
		return parts;
	}

	public void setParts(HashMap<Integer, Part> parts) {
		this.parts = parts;
	}

	//刷新战斗属性
	public FightProperty refreshFightProperty() {

		int score = 0;
		int atk = 0;
		int def = 0;
		int hp = 0;
		int crit = 0;

		boolean sameQualityType = true;
		PartQualityType qualityType = null;
		boolean sameSuitType = true;
		String suitMakingName = null;

		for (Part part : parts.values()) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
			score += partMaking.getScore(part.getLevel());
			atk += partMaking.getAtk(part.getLevel());
			def += partMaking.getDef(part.getLevel());
			hp += partMaking.getHp(part.getLevel());
			crit += partMaking.getCrl(part.getLevel());

			//套装不算武器
			if (part.getPartSlotType() != PartSlotType.WEAPON.asCode()) {
				//是否同材质
				if (sameQualityType) {
					if (qualityType == null) {
						qualityType = partMaking.getPartQualityType();
					} else {
						if (qualityType != partMaking.getPartQualityType()) {
							sameQualityType = false;
							qualityType = null;
						}
					}
				}
				//是否同套装
				if (sameSuitType) {
					if (suitMakingName == null) {
						suitMakingName = partMaking.getName();
					} else {
						if (!suitMakingName.equals(partMaking.getName())) {
							sameSuitType = false;
							suitMakingName = null;
						}
					}
				}
			}

		}

		//套装分数加成,材质分数加成,这俩叠加
		//材质套对总分影响为：当前分数*（1+材质套的加成比例*2）+5
		//外形套对总分的影响为：当前分数*1.2+10

		if (sameQualityType) {
			//材质套的加成比例
			int qualityAddRate = QualityLoadData.getInstance().getQualityMaking(qualityType.asCode()).getSameQualityNum();
			score = score * (100 + qualityAddRate * 2) / 100 + 5;
		}
		if (sameSuitType) {
			score = (score * 110) / 100 + 5;
		}

		FightProperty fightProperty = new FightProperty(score, atk, def, hp, crit, sameQualityType, sameSuitType, qualityType, suitMakingName);

		//buff加成
		Root.buffSystem.buffAddition(this, fightProperty);

		this.score = fightProperty.getScore();

		return fightProperty;

	}

	//战斗属性加成
	public FightProperty additionFightProperty(int level, FightProperty fightProperty) {

		FightProperty addFightProperty = new FightProperty(0, 0, 0, 0);

		//A=Int（主城等级/11）*25+Int（主城等级/13）*5+Int（主城等级/15）*5+Int（主城等级/20）*5，A最大为40，不能再增长了。
		Integer hpAdd = level / 11 * 25 + level / 13 * 5 + level / 15 * 5 + level / 20 * 5;
		if (hpAdd > 40) {
			hpAdd = 40;
		}
		addFightProperty.setHp(fightProperty.getHp() * hpAdd / 100);

		return addFightProperty;

	}

	public byte[] toByteArrayAsDefender() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		for (Part part : parts.values()) {
			if (null != part) {
				bago.putBytesNoLength(part.toByteArrayAsDefender());
			}
		}
		//水晶
		bago.putInt(0);
		return bago.toByteArray();
	}

}
