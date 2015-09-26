package gamecore.entity;

import java.util.TreeMap;

/** 抽象组件。
 */
public abstract class AbstractComponent implements IComponent {

	private static final long serialVersionUID = -5840320885936004296L;

	protected IEntity owner;
	protected String name;

	protected TreeMap<String, AbstractComponent> children;

	public AbstractComponent(String name) {
		this.owner = null;
		this.name = name;
	}

	public AbstractComponent(IEntity owner, String name) {
		this.owner = owner;
		this.name = name;
	}

	@Override
	public IEntity getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(IEntity owner) {
		this.owner = owner;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void addChild(AbstractComponent child) {
		synchronized (this.children) {
			if (null == this.children) {
				this.children = new TreeMap<String, AbstractComponent>();
			}

			this.children.put(child.getName(), child);
		}
	}

	public AbstractComponent removeChild(String childName) {
		synchronized (this.children) {
			if (null == this.children) {
				return null;
			}

			return this.children.get(childName);
		}
	}

	public AbstractComponent removeChild(AbstractComponent child) {
		return this.removeChild(child.name);
	}

	public AbstractComponent getChild(String childName) {
		synchronized (this.children) {
			if (null == this.children) {
				return null;
			}

			return this.children.get(childName);
		}
	}

	public boolean hasChild(String childName) {
		synchronized (this.children) {
			if (null == this.children) {
				return false;
			}

			return this.children.containsKey(childName);
		}
	}

	public boolean hasChild(AbstractComponent child) {
		return this.hasChild(child.name);
	}

	public int getChildrenNum() {
		synchronized (this.children) {
			if (null == this.children) {
				return 0;
			}

			return this.children.size();
		}
	}

	/**
	 * 是否是叶子节点。
	 */
	public boolean isLeaf() {
		return (null != this.children && !this.children.isEmpty()) ? false : true;
	}
}
