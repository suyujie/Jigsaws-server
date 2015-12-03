package server.node.system.trigger;

import gamecore.system.AbstractSystem;
import gamecore.trigger.Trigger;
import javolution.util.FastTable;
import server.node.system.trigger.trigger.LogTrigger;
import server.node.system.trigger.trigger.PlayerStatisticsTrigger;
import server.node.system.trigger.trigger.SessionTrigger;

/**
 * 触发器系统。
 */
public class TriggerSystem extends AbstractSystem {

	private FastTable<Trigger> triggers = new FastTable<Trigger>();

	public TriggerSystem() {
	}

	@Override
	public boolean startup() {
		System.out.println("TriggerSystem start....");
		boolean b = buildTriggers();
		System.out.println("TriggerSystem start....OK");
		return b;
	}

	@Override
	public void shutdown() {
	}

	private boolean buildTriggers() {

		triggers.add(new SessionTrigger());
		triggers.add(new LogTrigger());
		triggers.add(new PlayerStatisticsTrigger());

		for (Trigger t : triggers) {
			t.start();
		}

		return true;
	}

}
