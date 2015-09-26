package server.node.system.battle;

import gamecore.io.ByteArrayGameInput;

import java.util.ArrayList;
import java.util.List;

public class DamageCheckBean {

	private int hitNum;
	private List<DamageBean> damageBeans;

	public DamageCheckBean(int hitNum, List<DamageBean> damageBeans) {
		super();
		this.hitNum = hitNum;
		this.damageBeans = damageBeans;
	}

	public int getHitNum() {
		return hitNum;
	}

	public void setHitNum(int hitNum) {
		this.hitNum = hitNum;
	}

	public List<DamageBean> getDamageBeans() {
		return damageBeans;
	}

	public void setDamageBeans(List<DamageBean> damageBeans) {
		this.damageBeans = damageBeans;
	}

	public static DamageCheckBean read(ByteArrayGameInput arrayGameInput) {

		int hitNum = arrayGameInput.getInt();
		List<DamageBean> damageBeans = new ArrayList<DamageBean>();
		for (int i = 0; i < hitNum; i++) {
			damageBeans.add(DamageBean.read(arrayGameInput));
		}

		DamageCheckBean hurtCheckBean = new DamageCheckBean(hitNum, damageBeans);

		return hurtCheckBean;

	}
}
