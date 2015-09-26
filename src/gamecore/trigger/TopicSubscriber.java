package gamecore.trigger;

/** 主题订阅者。
 */
public interface TopicSubscriber {

	/** 收到订阅事件回调。
	 */
	public void onMessage(TopicPublisher publisher, TopicMessage message);
}
