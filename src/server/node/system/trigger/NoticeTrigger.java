package server.node.system.trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

/**
 * 公告触发器
 */
public final class NoticeTrigger implements Trigger, TopicSubscriber {

	private final static Logger logger = LogManager.getLogger(NoticeTrigger.class.getName());

	public NoticeTrigger() {
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
