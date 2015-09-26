package server.node.system.battle;

import gamecore.io.ByteArrayGameInput;

public class DamageBean {

	private int damage;
	private int attackRobotId;
	private int defendRobotId;
	private int atk;
	private int def;
	private int hp;
	private int crit;

	public DamageBean(int damage, int attackRobotId, int defendRobotId, int atk, int def, int hp, int crit) {
		super();
		this.damage = damage;
		this.attackRobotId = attackRobotId;
		this.defendRobotId = defendRobotId;
		this.atk = atk;
		this.def = def;
		this.hp = hp;
		this.crit = crit;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getAttackRobotId() {
		return attackRobotId;
	}

	public void setAttackRobotId(int attackRobotId) {
		this.attackRobotId = attackRobotId;
	}

	public int getDefendRobotId() {
		return defendRobotId;
	}

	public void setDefendRobotId(int defendRobotId) {
		this.defendRobotId = defendRobotId;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public static DamageBean read(ByteArrayGameInput arrayGameInput) {

		int damage = arrayGameInput.getInt();
		int attackRobotId = arrayGameInput.getInt();
		int defendRobotId = arrayGameInput.getInt();
		int atk = arrayGameInput.getInt();
		int def = arrayGameInput.getInt();
		int hp = arrayGameInput.getInt();
		int crit = arrayGameInput.getInt();

		DamageBean hurtCheckBean = new DamageBean(damage, attackRobotId, defendRobotId, atk, def, hp, crit);

		return hurtCheckBean;

	}
}
