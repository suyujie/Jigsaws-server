package gamecore.system;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicSubscriber;
import javolution.lang.Predicate;
import javolution.util.FastMap;
import javolution.util.FastTable;

/**
 * 抽象系统。
 */
public abstract class AbstractSystem implements Systematic {

	public boolean run;

	// Key：事件名称
	private FastMap<String, FastTable<TopicSubscriber>> subscriber;

	public AbstractSystem() {
		this.subscriber = new FastMap<String, FastTable<TopicSubscriber>>();
	}

	/** 判断是否已订阅指定名称的消息。
	 */
	public boolean isSubscribed(String name) {
		return this.subscriber.containsKey(name);
	}

	@Override
	public void subscribe(String name, TopicSubscriber subscriber) {
		synchronized (this) {
			FastTable<TopicSubscriber> list = this.subscriber.get(name);
			if (null != list) {
				list.add(subscriber);
			} else {
				list = new FastTable<TopicSubscriber>();
				list.add(subscriber);
				this.subscriber.put(name, list);
			}
		}
	}

	@Override
	public void unsubscribe(String name, TopicSubscriber subscriber) {
		synchronized (this) {
			FastTable<TopicSubscriber> list = this.subscriber.get(name);
			if (null != list) {
				list.remove(subscriber);
				if (list.isEmpty()) {
					this.subscriber.remove(name);
				}
			}
		}
	}

	@Override
	public void publish(final TopicMessage message) {
		String name = message.getName();
		FastTable<TopicSubscriber> list = null;
		synchronized (this) {
			list = this.subscriber.get(name);
		}
		// 遍历所有订阅者
		if (null != list) {
			final AbstractSystem publisher = this;
			list.doWhile(new Predicate<TopicSubscriber>() {
				@Override
				public Boolean evaluate(TopicSubscriber ts) {
					// 遍历
					ts.onMessage(publisher, message);
					return Boolean.TRUE;
				}
			});
		}
	}

	@Override
	public void clearSubscribers() {
		this.subscriber.clear();
	}

	@Override
	public boolean isRun() {
		return run;
	}
}
