package server.node.system.evaluate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gamecore.system.AbstractSystem;
import gamecore.system.SystemResult;
import server.node.system.Root;
import server.node.system.jigsaw.Jigsaw;
import server.node.system.jigsaw.JigsawMessage;
import server.node.system.player.Player;

public class EvaluateSystem extends AbstractSystem {

	private static final Logger logger = LogManager.getLogger(EvaluateSystem.class.getName());

	@Override
	public boolean startup() {
		System.out.println("EvaluateSystem start..");

		System.out.println("EvaluateSystem start..ok");

		return true;
	}

	@Override
	public void shutdown() {
	}

	public SystemResult EvaluateJigsaw(Player player, Long jigsawId, EvaluateType type) {

		SystemResult result = new SystemResult();

		if (jigsawId < 10000) {// 官方拼图，不评价
			this.publish(new JigsawMessage(JigsawMessage.Evaluate, player, jigsawId, type));
			return result;
		}

		Jigsaw jigsaw = Root.jigsawSystem.getJigsaw(jigsawId);

		if (jigsaw == null) {
			result.setCode(gamecore.system.ErrorCode.PARAM_ERROR);
			return result;
		}

		if (type == EvaluateType.GOOD) {
			jigsaw.setGood(jigsaw.getGood() + 1);
		}
		if (type == EvaluateType.BAD) {
			jigsaw.setBad(jigsaw.getBad() + 1);
		}

		jigsaw.synchronize();

		Root.jigsawSystem.updateDB(jigsaw);

		this.publish(new JigsawMessage(JigsawMessage.Evaluate, player, jigsaw, type));

		return result;

	}

}
