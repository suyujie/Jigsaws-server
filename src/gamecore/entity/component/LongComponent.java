package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

public class LongComponent extends AbstractComponent {

	private static final long serialVersionUID = 4640412925408552350L;

	private long value;

	public LongComponent(IEntity owner, String name, long value) {
		super(owner, name);
		this.value = value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getValue() {
		return this.value;
	}
}
