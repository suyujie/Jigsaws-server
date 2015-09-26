package gamecore.util;

/**
 * 基础权重
 */
public final class BaseWeight {

	private int begin;
	private int end;

	private Object value;

	public BaseWeight(int begin, int end, Object value) {
		this.begin = begin;
		this.end = end;
		this.value = value;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public Object getValue() {
		return value;
	}

}
