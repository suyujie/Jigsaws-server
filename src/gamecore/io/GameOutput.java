package gamecore.io;

import java.util.Date;

/**
 * 这个接口抽象游戏输出流。服务器通过这个接口向客户端发送消息。
 */
public interface GameOutput {

	public int size();

	public byte[] toByteArray();

	/**
	 * 往流中写一个boolean值。
	 */
	public void putBoolean(boolean b);

	/**
	 * 往流中写一个int值。
	 */
	public void put(byte b);

	/**
	 * 往流中写一个short值。
	 */
	public void putShort(short s);

	/**
	 * 往流中写一个int值。
	 */
	public void putInt(int i);

	/**
	 * 往流中写一个long值。
	 */
	public void putLong(long l);

	/**
	 * 往流中写一个char值。
	 */
	public void putChar(char value);

	/**
	 * 往流中写一个float值。
	 */
	public void putFloat(float value);

	/**
	 * 往流中写一个double值。
	 */
	public void putDouble(double value);

	/**
	 * 往流中写一个int数组。
	 */
	public void putInts(int[] ints);

	/**
	 * 往流中写一个int数组。
	 */
	public void putIntsNoLength(int[] ints);

	/**
	 * 往流中写一个字节数组。
	 */
	public void putBytes(byte[] bytes);

	/**
	 * 往流中写一个字节数组。不补充长度
	 */
	public void putBytesNoLength(byte[] bytes);

	/**
	 * 往流中写一个字符串。
	 */
	public void putString(String s);

	/**
	 * 往流中写日期。
	 */
	public void putDate(Date date);

}
