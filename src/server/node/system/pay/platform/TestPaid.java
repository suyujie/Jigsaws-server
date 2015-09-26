package server.node.system.pay.platform;

import gamecore.system.ErrorCode;
import gamecore.system.SystemResult;

import java.util.UUID;

import server.node.system.Root;
import server.node.system.log.PayLog;
import server.node.system.pay.AbstractPaid;
import server.node.system.pay.PayChannel;
import server.node.system.pay.PayStatus;
import server.node.system.player.GoldType;
import server.node.system.player.Player;
import server.node.system.rechargePackage.ItemType;
import server.node.system.rechargePackage.RechargePackage;

import common.coin.CoinType;

public class TestPaid extends AbstractPaid {

	public SystemResult buyGold(Player player, RechargePackage rechargePackage, CoinType coinType) {

		SystemResult result = new SystemResult();

		result.setCode(ErrorCode.NO_ERROR);

		if (player != null && rechargePackage != null) {

			int buyGold = rechargePackage.getItem().get(ItemType.GOLD);

			PayLog payLog = new PayLog(player.getId(), PayChannel.Test, UUID.randomUUID().toString(), rechargePackage.getId(), coinType, rechargePackage.getCoin().get(coinType),
					"testinfo", PayStatus.TEST);

			addPayLog(payLog);

			Root.playerSystem.changeGold(player, buyGold, GoldType.BUY, true);

		} else {
			result.setCode(ErrorCode.RECHARGE_ERROR);
		}
		return result;
	}

}
