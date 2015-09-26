package gamecore.io;

import java.util.Date;

/**
 * 这个接口抽象游戏输入流。服务器通过这个接口读取客户端发送的消息。
 */
public interface GameInput {

	/**
	 * 返回剩余字节数。
	 */
	public int remaining();

	/**
	 * 从流中读取一个boolean值。
	 */
	public boolean getBoolean();

	/**
	 * 从流中读取一个byte值。
	 */
	public byte get();

	/**
	 * 从流中读取一个short值。
	 */
	public short getShort();

	/**
	 * 从流中读取一个int值。
	 */
	public int getInt();

	/**
	 * 从流中读取一个long值。
	 */
	public long getLong();

	/**
	 * 从流中读char。
	 */
	public char getChar();

	/**
	 * 从流中读float。
	 */
	public float getFloat();

	/**
	 * 从流中读double。
	 */
	public double getDouble();

	/**
	 * 从流中读取一个字节数组。
	 */
	public byte[] getBytes();

	/**
	 * 从流中读取一个int数组。
	 */
	public int[] getInts();

	/**
	 * 从流中读取一个字节数组。
	 */
	public byte[] getBytesNoLength();

	/**
	 * 从流中读取一个字符串。
	 */
	public String getString();

	/**
	 * 从流中读日期。
	 */
	public Date getDate();
	
	/**
	 * 从流中读取剩下的所有,只读,不截取
	 */
	public byte[] getCopyRemainBytes() ;
}
