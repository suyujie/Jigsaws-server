package server.node.system.battle;

import gamecore.io.ByteArrayGameInput;

import java.util.ArrayList;

public class BattleCheckBean {

	private ArrayList<CheckRobot> attackerRobots = new ArrayList<CheckRobot>();
	private ArrayList<CheckRobot> defenderRobots = new ArrayList<CheckRobot>();

	public BattleCheckBean(ArrayList<CheckRobot> attackerRobots, ArrayList<CheckRobot> defenderRobots) {
		super();
		this.attackerRobots = attackerRobots;
		this.defenderRobots = defenderRobots;
	}

	public ArrayList<CheckRobot> getAttackerRobots() {
		return attackerRobots;
	}

	public ArrayList<CheckRobot> getDefenderRobots() {
		return defenderRobots;
	}

	public static BattleCheckBean read(ByteArrayGameInput arrayGameInput) {

		//attacker
		int attackerRobotNum = arrayGameInput.getInt();//攻击者 机器人数量
		ArrayList<CheckRobot> attackerRobots = new ArrayList<CheckRobot>();
		for (int i = 0; i < attackerRobotNum; i++) {
			CheckRobot checkRobot = CheckRobot.read(arrayGameInput);
			attackerRobots.add(checkRobot);
		}

		//defender
		int defenderRobotNum = arrayGameInput.getInt();
		ArrayList<CheckRobot> defenderRobots = new ArrayList<CheckRobot>();
		for (int i = 0; i < defenderRobotNum; i++) {
			CheckRobot checkRobot = CheckRobot.read(arrayGameInput);
			defenderRobots.add(checkRobot);
		}

		BattleCheckBean battleCheckBean = new BattleCheckBean(attackerRobots, defenderRobots);

		return battleCheckBean;

	}
}
