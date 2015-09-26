package server.node.system.robot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import server.node.system.robotPart.PartSlotType;

public class LevelRobotMaking implements Serializable {

	private static final long serialVersionUID = -4877217356579683470L;

	private Integer level;//等级开放条件,0 表示,仓库机器人

	private String head;
	private String body;
	private String arm;
	private String leg;
	private String weapon;

	private Map<Integer, PartLevelBean> parts = new HashMap<Integer, PartLevelBean>();

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getArm() {
		return arm;
	}

	public void setArm(String arm) {
		this.arm = arm;
	}

	public String getLeg() {
		return leg;
	}

	public void setLeg(String leg) {
		this.leg = leg;
	}

	public String getWeapon() {
		return weapon;
	}

	public void setWeapon(String weapon) {
		this.weapon = weapon;
	}

	public Map<Integer, PartLevelBean> getParts() {
		return parts;
	}

	public void setParts(Map<Integer, PartLevelBean> parts) {
		this.parts = parts;
	}

	public void encapsulateBean() {

		String[] head_level = head.split("-");
		PartLevelBean headLevel = new PartLevelBean(Integer.parseInt(head_level[0]), Integer.parseInt(head_level[1]));

		String[] body_level = body.split("-");
		PartLevelBean bodyLevel = new PartLevelBean(Integer.parseInt(body_level[0]), Integer.parseInt(body_level[1]));

		String[] arm_level = arm.split("-");
		PartLevelBean armLevel = new PartLevelBean(Integer.parseInt(arm_level[0]), Integer.parseInt(arm_level[1]));

		String[] leg_level = leg.split("-");
		PartLevelBean legLevel = new PartLevelBean(Integer.parseInt(leg_level[0]), Integer.parseInt(leg_level[1]));

		String[] weapon_level = weapon.split("-");
		PartLevelBean weaponLevel = new PartLevelBean(Integer.parseInt(weapon_level[0]), Integer.parseInt(weapon_level[1]));

		this.parts.put(PartSlotType.HEAD.asCode(), headLevel);
		this.parts.put(PartSlotType.BODY.asCode(), bodyLevel);
		this.parts.put(PartSlotType.ARM.asCode(), armLevel);
		this.parts.put(PartSlotType.LEG.asCode(), legLevel);
		this.parts.put(PartSlotType.WEAPON.asCode(), weaponLevel);

	}

}