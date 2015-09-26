package gamecore.entity;

import gamecore.cache.redis.JedisUtilJson;
import gamecore.serialize.SerializerJson;

import java.util.HashMap;
import java.util.Set;

import server.node.system.Content;

/**
 * 抽象实体，用于结构性继承。
 */
public abstract class AbstractEntity implements IEntity {

	private static final long serialVersionUID = 60123005368223440L;

	// 存储键
	private String cacheKey;
	// 实体基础组件
	private HashMap<String, IComponent> components;
	// 子实体存储键，Key：存储键，Value：存储组
	private HashMap<String, String> childrenCacheKeys;

	public AbstractEntity() {
	}

	public AbstractEntity(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	/** 更新缓存键。
	 */
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	/** 返回缓存键。
	 */
	public String getCacheKey() {
		return this.cacheKey;
	}

	/** 添加组件。
	 */
	@Override
	public void addComponent(IComponent component) {
		synchronized (this) {
			if (null == this.components) {
				this.components = new HashMap<String, IComponent>(2);
			}

			this.components.put(component.getName(), component);
		}
	}

	/** 删除组件。
	 */
	@Override
	public void removeComponent(IComponent component) {
		synchronized (this) {
			if (null == this.components) {
				return;
			}

			this.components.remove(component.getName());
		}
	}

	/** 删除组件。
	 */
	@Override
	public IComponent removeComponent(String componentName) {
		synchronized (this) {
			if (null == this.components) {
				return null;
			}

			return this.components.remove(componentName);
		}
	}

	/** 获取组件。
	 */
	@Override
	public IComponent getComponent(String componentName) {
		synchronized (this) {
			if (null == this.components) {
				return null;
			}

			return this.components.get(componentName);
		}
	}

	/** 组件是否存在。
	 */
	@Override
	public boolean containsComponent(String componentName) {
		synchronized (this) {
			if (null == this.components) {
				return false;
			}

			return this.components.containsKey(componentName);
		}
	}

	/** 是否包含该实体。
	 */
	public boolean containsEntity(String cacheKey) {
		synchronized (this) {
			if (null == this.childrenCacheKeys) {
				return false;
			}

			return this.childrenCacheKeys.containsKey(cacheKey);
		}
	}

	/** 添加子实体。
	 * @param entity
	 * @param sync 是否将实体数据同步到缓存。
	 */
	public void addEntity(AbstractEntity entity, boolean sync) {
		synchronized (this) {
			if (null == this.childrenCacheKeys) {
				this.childrenCacheKeys = new HashMap<String, String>();
			}
			// 仅保存实体的存储键
			String ck = entity.getCacheKey();
			if (!this.childrenCacheKeys.containsKey(ck)) {
				this.childrenCacheKeys.put(ck, ck);
			}
		}

		if (sync) {
			entity.synchronize();
		}
	}

	/**
	 * 返回子实体key set
	 * @return
	 */
	public Set<String> getChildrenCacheKeys() {
		synchronized (this) {
			if (null == this.childrenCacheKeys) {
				return null;
			}
			return this.childrenCacheKeys.keySet();
		}
	}

	public String toJson() {
		return SerializerJson.serialize(this);
	}

	/** 实例数据写入缓存。
	 */
	public void synchronize() {
		synchronized (this) {
			JedisUtilJson.getInstance().setForHour(this.cacheKey, this, Content.CacheTimeOutHour);
		}
	}

}
