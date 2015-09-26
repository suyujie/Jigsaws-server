package gamecore.db;

public enum DBOperator {

	Write("write"),

	Read("read"),

	Log("log");

	private String code;

	private DBOperator(String code) {
		this.code = code;
	}

	public String asString() {
		return this.code;
	}
}
