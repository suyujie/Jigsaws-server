package server.node.system.trigger;

import gamecore.trigger.TopicMessage;
import gamecore.trigger.TopicPublisher;
import gamecore.trigger.TopicSubscriber;
import gamecore.trigger.Trigger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import server.node.system.Root;
import server.node.system.battle.PveMessage;
import server.node.system.battle.PveSystem;
import server.node.system.mission.PointLoadData;
import server.node.system.mission.PointMaking;
import server.node.system.mission.Point;
import server.node.system.player.Player;
import server.node.system.player.PlayerMessage;
import server.node.system.player.PlayerSystem;

/**
 * 关卡触发。
 */
public final class MissionPointTrigger implements Trigger, TopicSubscriber {

	@SuppressWarnings("unused")
	private final static Logger logger = LogManager.getLogger(MissionPointTrigger.class.getName());

	public MissionPointTrigger() {
	}

	public boolean start() {
		Root.pveSystem.subscribe(PveMessage.PVE_EXIT, this);
		Root.playerSystem.subscribe(PlayerMessage.NewPlayer, this);
		return true;
	}

	public void stop() {
		Root.pveSystem.unsubscribe(PveMessage.PVE_EXIT, this);
		Root.playerSystem.unsubscribe(PlayerMessage.NewPlayer, this);

	}

	@Override
	public void onMessage(TopicPublisher publisher, TopicMessage message) {
		//用户系统发来的消息，注册新用户
		if (publisher instanceof PlayerSystem) {
			if (message instanceof PlayerMessage) {
				PlayerMessage playerMsg = (PlayerMessage) message;
				Player player = playerMsg.getPlayer();
				if (message.getName() == PlayerMessage.NewPlayer) {
					//开放默认的point 
					Root.missionSystem.openDefaultPoint(player, null);
				}
			}
		}

		//战斗系统发来的关卡信息
		if (publisher instanceof PveSystem) {
			if (message instanceof PveMessage) {
				PveMessage pveMessage = (PveMessage) message;
				Player player = pveMessage.getPlayer();
				Point pointPO = pveMessage.getPveBattle().getPointPO();
				boolean firstPass = pveMessage.getPveBattleResult().isFirstPass();

				if (message.getName() == PveMessage.PVE_EXIT) {//根据刚通过的关卡来触发下一个关卡

					if (firstPass) {
						//加星星
						if (pointPO.getStar() < 3 || pointPO.getPassStar() < 3) {
							Root.missionSystem.openPointNewStar(player, null, pointPO);
						}

						//打开下一个关卡
						PointMaking pointMaking = PointLoadData.getInstance().getNextPointMaking(pointPO.getMakingId());

						if (pointMaking != null) {
							//确定这个关卡还没有开放
							if (Root.missionSystem.getPointPO(player, pointMaking.getId()) == null) {
								Root.missionSystem.openPoint(player, null, pointMaking);
							}
						}

					}

				}
			}
		}

	}
}
