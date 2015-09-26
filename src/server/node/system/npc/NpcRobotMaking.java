package server.node.system.npc;

import java.io.Serializable;
import java.util.HashMap;

import server.node.system.Root;
import server.node.system.robotPart.Part;
import server.node.system.robotPart.PartSlotType;

public class NpcRobotMaking implements Serializable {

	private static final long serialVersionUID = -8205662617710972658L;

	private String head;
	private String body;
	private String arm;
	private String leg;
	private String weapon;

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

	public NpcRobot createNpcRobots() {

		//		<head>1-1</head>
		String[] head_id_level = head.split("-");
		int head_id = Integer.parseInt(head_id_level[0]);
		int head_level = Integer.parseInt(head_id_level[1]);

		//	<body>1-1</body>
		String[] body_id_level = body.split("-");
		int body_id = Integer.parseInt(body_id_level[0]);
		int body_level = Integer.parseInt(body_id_level[1]);

		//	<arm>1-1</arm>
		String[] arm_id_level = arm.split("-");
		int arm_id = Integer.parseInt(arm_id_level[0]);
		int arm_level = Integer.parseInt(arm_id_level[1]);

		//	<leg>1-1</leg>
		String[] leg_id_level = leg.split("-");
		int leg_id = Integer.parseInt(leg_id_level[0]);
		int leg_level = Integer.parseInt(leg_id_level[1]);

		//	<weapon>1-1</weapon>
		String[] weapon_id_level = weapon.split("-");
		int weapon_id = Integer.parseInt(weapon_id_level[0]);
		int weapon_level = Integer.parseInt(weapon_id_level[1]);

		//构造部件
		HashMap<Integer, Part> parts = new HashMap<>();

		parts.put(PartSlotType.HEAD.asCode(), Root.partSystem.createPart(0L, PartSlotType.HEAD.asCode(), head_id, head_level, 0, 0));
		parts.put(PartSlotType.ARM.asCode(), Root.partSystem.createPart(0L, PartSlotType.ARM.asCode(), arm_id, arm_level, 0, 0));
		parts.put(PartSlotType.LEG.asCode(), Root.partSystem.createPart(0L, PartSlotType.LEG.asCode(), leg_id, leg_level, 0, 0));
		parts.put(PartSlotType.BODY.asCode(), Root.partSystem.createPart(0L, PartSlotType.BODY.asCode(), body_id, body_level, 0, 0));
		parts.put(PartSlotType.WEAPON.asCode(), Root.partSystem.createPart(0L, PartSlotType.WEAPON.asCode(), weapon_id, weapon_level, 0, 0));

		//构造机器人
		NpcRobot npcRobot = new NpcRobot(parts);

		return npcRobot;
	}

}