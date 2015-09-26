package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

public class FloatComponent extends AbstractComponent {

	private static final long serialVersionUID = 4287539741163569995L;

	private float value;

	public FloatComponent(IEntity owner, String name, float value) {
		super(owner, name);
		this.value = value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getValue() {
		return this.value;
	}
}
