package gamecore.io;

import java.nio.charset.Charset;

/**
 * GameMessage将请求和响应结合在一起，并且定义了一些常量。
 */
public interface GameMessage {

	/**
	 * GameMessage的魔数。
	 * 每个消息（无论请求还是响应）都以这两个字节开头，否则应该被视为错误，
	 * 应该强制关闭游戏Session或者抛出异常。
	 */
	public static final byte[] MAGIC_NUMBERS = { 0x23, 0x24 };

	/**
	 * 编码、解码字符串的默认字符集。
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

}
