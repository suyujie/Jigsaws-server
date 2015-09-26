package gamecore.entity;

import java.io.Serializable;

/** 实体组件。
 */
public interface IComponent extends Serializable {

	/** 返回组件名。
	 */
	public String getName();

	/** 返回所属实体。
	 */
	public IEntity getOwner();

	/** 设置所属实体。
	 */
	public void setOwner(IEntity entity);
}
