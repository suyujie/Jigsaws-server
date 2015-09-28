package server.node.system.trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

/**
 * 向导 触发器
 */
public final class ToturialTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(ToturialTrigger.class.getName());

	public ToturialTrigger() {
	}

	public boolean start() {
		return true;
	}

	public void stop() {
	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {

	}
}
