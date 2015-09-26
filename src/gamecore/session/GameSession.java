package gamecore.session;

import gamecore.message.GameResponse;

import java.util.Set;

/**
 * 游戏服务器通过这个接口往客户端发送任意消息（包括广播消息）。
 */
public interface GameSession {

	/**
	 * 往与该Session相关的客户端发送消息。
	 */
	public void write(GameResponse message);

	/**
	 * 给所有客户端发送消息。
	 */
	public void broadcast(GameResponse message);

	/**
	 * 往Session中存一个对象。
	 * @param key 键
	 * @param value 对象
	 * @return 以前与该键关联的对象（如果有的话），或者null（如果没有）
	 */
	public Object setAttribute(Object key, Object value);

	/**
	 * 返回Session中与key关联的对象。
	 * @param key 键
	 * @return null 如果没有对象与该键关联
	 */
	public Object getAttribute(Object key);

	/**
	 * 返回所有key。
	 */
	public Set<Object> getAttributeKeys();

	/**
	 * 主动关闭session
	 */
	public void close();
}
