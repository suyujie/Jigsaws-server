package gamecore.entity.component;

import gamecore.entity.AbstractComponent;
import gamecore.entity.IEntity;

import java.util.List;
import java.util.Vector;

/** 
 * 列表组件。
 */
public class ListComponent<T> extends AbstractComponent {

	private static final long serialVersionUID = 4749456568514812129L;

	private Vector<T> list;

	public ListComponent(IEntity owner, String name) {
		super(owner, name);
		this.list = new Vector<T>();
	}

	public void add(int index, T element) {
		this.list.add(index, element);
	}

	public boolean add(T element) {
		return this.list.add(element);
	}

	public boolean remove(T element) {
		return this.list.remove(element);
	}

	public T remove(int index) {
		return this.list.remove(index);
	}

	public T get(int index) {
		return this.list.get(index);
	}

	public List<T> values() {
		return this.list;
	}

	public int size() {
		return this.list.size();
	}

	public void clear() {
		this.list.clear();
	}

	public boolean contains(T element) {
		return this.list.contains(element);
	}

}
