package server.node.system.battle;

import gamecore.io.ByteArrayGameInput;

class CheckBuffer {

	private int id;
	private int value;
	private int secondValue;
	private int bufferSourse;

	public CheckBuffer(int id, int value, int secondValue, int bufferSourse) {
		super();
		this.id = id;
		this.value = value;
		this.secondValue = secondValue;
		this.bufferSourse = bufferSourse;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

	public int getSecondValue() {
		return secondValue;
	}

	public int getBufferSourse() {
		return bufferSourse;
	}

	public static CheckBuffer read(ByteArrayGameInput arrayGameInput) {

		int id = (int) arrayGameInput.getShort();
		int value = arrayGameInput.getInt();
		int secondValue = arrayGameInput.getInt();
		int bufferSourse = (int) arrayGameInput.get();

		CheckBuffer checkBuffer = new CheckBuffer(id, value, secondValue, bufferSourse);
		return checkBuffer;

	}

}
