package gamecore.entity;

import java.io.Serializable;

/** 实体。
 */
public interface IEntity extends Serializable {

	/** 添加组件。
	 */
	public void addComponent(IComponent component);

	/** 移除组件。
	 */
	public void removeComponent(IComponent component);

	/** 移除组件。
	 */
	public IComponent removeComponent(String componentName);

	/** 获取组件。
	 */
	public IComponent getComponent(String componentName);

	/** 是否包含该组件。
	 */
	public boolean containsComponent(String componentName);
}
