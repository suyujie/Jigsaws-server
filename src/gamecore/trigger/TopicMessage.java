package gamecore.trigger;

/** 主题消息。
 */
public abstract class TopicMessage {

	private String name;

	public TopicMessage(String name) {
		this.name = name;
	}

	public final String getName() {
		return this.name;
	}
}
