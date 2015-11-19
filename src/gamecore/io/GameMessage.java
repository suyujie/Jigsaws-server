package gamecore.io;

import java.nio.charset.Charset;

/**
 * GameMessage将请求和响应结合在一起，并且定义了一些常量。
 */
public interface GameMessage {

	/**
	 * 编码、解码字符串的默认字符集。
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

}
