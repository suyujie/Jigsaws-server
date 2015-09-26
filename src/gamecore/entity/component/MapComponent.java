package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 映射组件。
 */
public class MapComponent<KT, VT> extends AbstractComponent {

	private static final long serialVersionUID = -3069043546049745346L;

	private ConcurrentHashMap<KT, VT> map;

	public MapComponent(IEntity owner, String name) {
		super(owner, name);
		this.map = new ConcurrentHashMap<KT, VT>();
	}

	public void put(KT key, VT value) {
		this.map.put(key, value);
	}

	public VT get(KT key) {
		return this.map.get(key);
	}

	public VT remove(KT key) {
		return this.map.remove(key);
	}

	public int size() {
		return this.map.size();
	}

	public Collection<VT> values() {
		return this.map.values();
	}

	public Set<KT> keySet() {
		return this.map.keySet();
	}

	public boolean containsKey(KT key) {
		return this.map.containsKey(key);
	}
}
