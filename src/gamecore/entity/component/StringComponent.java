package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

public class StringComponent extends AbstractComponent {

	private static final long serialVersionUID = -2309000102485805920L;

	private String value;

	public StringComponent(IEntity owner, String name, String value) {
		super(owner, name);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
