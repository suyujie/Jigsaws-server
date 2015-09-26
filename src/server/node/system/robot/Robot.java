package server.node.system.robot;

import gamecore.io.ByteArrayGameOutput;
import gamecore.serialize.SerializerJson;
import gamecore.util.Clock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Content;
import server.node.system.Root;
import server.node.system.berg.BergLoadData;
import server.node.system.berg.BergMaking;
import server.node.system.player.Player;
import server.node.system.rent.RentOrder;
import server.node.system.rent.RentOrderBag;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartLoadData;
import server.node.system.robotPart.PartMaking;
import server.node.system.robotPart.PartQualityType;
import server.node.system.robotPart.PartSlotType;
import server.node.system.robotPart.QualityLoadData;

public class Robot {
	private static Logger logger = LogManager.getLogger(Robot.class.getName());

	private Long id;
	private RobotType robotType;
	private Integer slot;
	private HashMap<Integer, Part> parts = new HashMap<Integer, Part>();
	private HashMap<Integer, Integer> bergIds = new HashMap<Integer, Integer>();

	public Robot() {
	}

	public Robot(Long id, RobotType robotType, Integer slot, HashMap<Integer, Part> parts, HashMap<Integer, Integer> bergIds) {
		this.id = id;
		this.robotType = robotType;
		this.slot = slot;
		this.parts = parts;
		this.bergIds = bergIds;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RobotType getRobotType() {
		return robotType;
	}

	public void setRobotType(RobotType robotType) {
		this.robotType = robotType;
	}

	public Integer getSlot() {
		return slot;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public HashMap<Integer, Part> getParts() {
		return parts;
	}

	public void setParts(HashMap<Integer, Part> parts) {
		this.parts = parts;
	}

	public HashMap<Integer, Integer> getBergIds() {
		return bergIds;
	}

	public void setBergIds(HashMap<Integer, Integer> bergIds) {
		this.bergIds = bergIds;
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

		int partNumWithOutWeapon = 0;

		for (Part part : parts.values()) {
			PartMaking partMaking = PartLoadData.getInstance().getMaking(part.getPartSlotType(), part.getMakingId());
			score += partMaking.getScore(part.getLevel());
			atk += partMaking.getAtk(part.getLevel());
			def += partMaking.getDef(part.getLevel());
			hp += partMaking.getHp(part.getLevel());
			crit += partMaking.getCrl(part.getLevel());

			// 材质套装,外形套装,都不包含武器
			if (part.getPartSlotType() != PartSlotType.WEAPON.asCode()) {
				partNumWithOutWeapon++;
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
						suitMakingName = partMaking.getSuitName();
					} else {
						if (!suitMakingName.equals(partMaking.getSuitName())) {
							sameSuitType = false;
							suitMakingName = null;
						}
					}
				}
			}

		}

		//水晶加成
		if (bergIds != null && !bergIds.isEmpty()) {
			for (Integer bergId : bergIds.values()) {
				BergMaking bergMaking = BergLoadData.getInstance().getBergMaking(bergId);
				if (bergMaking != null) {
					score += bergMaking.getFraction();
					switch (bergMaking.getBergType()) {
					case ATK:
						atk += bergMaking.getValue();
						break;
					case DEF:
						def += bergMaking.getValue();
						break;
					case CRT:
						crit += bergMaking.getValue();
						break;
					case HP:
						hp += bergMaking.getValue();
						break;
					default:
						break;
					}
				}
			}
		}

		//不是完整的机器人,套装 材质  都不算
		if (partNumWithOutWeapon < 4) {
			sameQualityType = false;
			sameSuitType = false;
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

		//套装  head weapon 材质 的buff加成
		Root.buffSystem.buffAddition(this, fightProperty);

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

	public void putPart(Part part) {
		parts.put(part.getPartSlotType(), part);
	}

	public void putBerg(Integer bergSlot, Integer bergId) {
		bergIds.put(bergSlot, bergId);
	}

	public void removeBerg(Integer bergSlot) {
		bergIds.remove(bergSlot);
	}

	public Part readPart(Integer slot) {
		return parts.get(slot);
	}

	public List<Part> readParts() {
		List<Part> partList = new ArrayList<>();

		for (Part part : parts.values()) {
			if (part != null) {
				partList.add(part);
			}
		}

		return partList;
	}

	public int readPaidNum() {
		int num = 0;
		for (Part part : parts.values()) {
			if (part != null && part.getPartSlotType() != PartSlotType.WEAPON.asCode()) {//武器不涂装
				num++;
			}
		}
		return num;
	}

	public byte[] toByteArrayAsBattle() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();

		bago.putInt(slot);
		//循环发送每个机器人上的组件信息（肯定是5个）
		for (Part part : parts.values()) {
			if (null != part) {
				bago.putBytesNoLength(part.toByteArray());
			}
		}

		//水晶
		if (bergIds != null && !bergIds.isEmpty()) {
			bago.putInt(bergIds.size());
			for (Map.Entry<Integer, Integer> entry : bergIds.entrySet()) {
				bago.putInt(entry.getKey());
				bago.putInt(entry.getValue());
			}
		} else {
			bago.putInt(0);
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayAsStorage(Player player, RentOrderBag rentOrderBag) throws SQLException {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		RentOrder rentOrder = rentOrderBag.getRentOrder(player, slot);

		bago.putInt(slot);

		if (rentOrder != null) {

			//出租到时的剩余时间(秒)
			int hireEndNeedTime = (int) (rentOrder.getEndTime() - Clock.currentTimeSecond());

			bago.putInt(hireEndNeedTime > 0 ? hireEndNeedTime : 0);

			if (rentOrder.getTenantId() != null) {
				Player hirePlayer = Root.playerSystem.getPlayer(rentOrder.getTenantId());
				//出租获的钱(实际能到的钱）
				bago.putInt((int) (rentOrder.getCash() * (1 - Content.orderAgencyCost)));
				//谁租的人的名字
				bago.putString(hirePlayer == null ? "" : hirePlayer.getAccount().getNameInPlat());
				//谁租的人等级
				bago.putInt(hirePlayer == null ? 0 : hirePlayer.getLevel());
			} else {
				//出租获的钱(实际能到的钱）
				bago.putInt(0);
				//谁租的人的名字
				bago.putString("");
				//谁租的人等级
				bago.putInt(0);
			}

		} else {
			//出租到时的剩余时间(秒)
			int hireEndNeedTime = 0;
			bago.putInt(hireEndNeedTime > 0 ? hireEndNeedTime : 0);
			//出租获的钱(实际能到的钱）
			bago.putInt(0);
			//谁租的人的名字
			bago.putString("");
			//谁租的人等级
			bago.putInt(0);
		}

		//循环发送每个机器人上的组件
		bago.putInt(parts.size());
		for (Part part : parts.values()) {
			if (null != part) {
				bago.putBytesNoLength(part.toByteArray());
			}
		}

		//水晶
		if (bergIds != null && !bergIds.isEmpty()) {
			bago.putInt(bergIds.size());
			for (Map.Entry<Integer, Integer> entry : bergIds.entrySet()) {
				bago.putInt(entry.getKey());
				bago.putInt(entry.getValue());
			}
		} else {
			bago.putInt(0);
		}

		return bago.toByteArray();
	}

	public byte[] toByteArrayAsDefender() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//循环发送每个机器人上的组件信息（肯定是5个）
			for (Part part : parts.values()) {
				if (null != part) {
					bago.putBytesNoLength(part.toByteArrayAsDefender());
				}
			}

			//水晶
			if (bergIds != null && !bergIds.isEmpty()) {
				bago.putInt(bergIds.size());
				for (Map.Entry<Integer, Integer> entry : bergIds.entrySet()) {
					bago.putInt(entry.getKey());
					bago.putInt(entry.getValue());
				}
			} else {
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public byte[] toByteArrayAsNewRobot() {
		ByteArrayGameOutput bago = new ByteArrayGameOutput();
		try {
			//机器人位置0开始
			bago.putInt(slot);
			//循环发送每个机器人上的组件信息（肯定是5个）
			for (Part part : parts.values()) {
				if (null != part) {
					bago.putBytesNoLength(part.toByteArrayAsNew());
				}
			}
			//水晶
			if (bergIds != null && !bergIds.isEmpty()) {
				bago.putInt(bergIds.size());
				for (Map.Entry<Integer, Integer> entry : bergIds.entrySet()) {
					bago.putInt(entry.getKey());
					bago.putInt(entry.getValue());
				}
			} else {
				bago.putInt(0);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return bago.toByteArray();
	}

	public String partsJson() {
		return SerializerJson.serialize(parts);
	}

	public String bergsJson() {
		return SerializerJson.serialize(bergIds);
	}

}
