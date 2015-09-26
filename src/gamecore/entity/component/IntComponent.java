package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

public class IntComponent extends AbstractComponent {

	private static final long serialVersionUID = -5751899951003384669L;

	private int value;

	public IntComponent(IEntity owner, String name, int value) {
		super(owner, name);
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
