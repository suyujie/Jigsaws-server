package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

public class BoolComponent extends AbstractComponent {

	private static final long serialVersionUID = 276357236451770215L;

	private boolean value;

	public BoolComponent(IEntity owner, String name, boolean value) {
		super(owner, name);
		this.value = value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}
}
