package gamecore.trigger;

/**
 * 主题发布者。
 */
public interface TopicPublisher {

	/**
	 * 订阅消息。
	 */
	public void subscribe(String messageName, TopicSubscriber subscriber);

	/**
	 * 退订消息。
	 */
	public void unsubscribe(String messageName, TopicSubscriber subscriber);

	/**
	 * 发布主题。
	 */
	public void publish(TopicMessage message);

	/**
	 * 清理订阅者。
	 */
	public void clearSubscribers();
}
