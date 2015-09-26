package server.node.system.log;

import server.node.system.pay.PayChannel;
import server.node.system.pay.PayStatus;
import common.coin.CoinType;

public class PayLog extends AbstractLog {

	private Long playerId;
	private String orderId;
	private PayChannel payChannel;
	private String rechargePackageId;
	private CoinType coinType;
	private int coin;
	private String info;
	private PayStatus payStatus;

	public PayLog(Long playerId, PayChannel payChannel, String orderId, String rechargePackageId, PayStatus payStatus) {
		super();
		this.playerId = playerId;
		this.payChannel = payChannel;
		this.orderId = orderId;
		this.rechargePackageId = rechargePackageId;
		this.payStatus = payStatus;
	}

	public PayLog(Long playerId, PayChannel payChannel, String orderId, String rechargePackageId, CoinType coinType, Integer coin, String info, PayStatus payStatus) {
		super();
		this.playerId = playerId;
		this.payChannel = payChannel;
		this.orderId = orderId;
		this.rechargePackageId = rechargePackageId;
		this.coinType = coinType;
		this.coin = coin;
		this.info = info;
		this.payStatus = payStatus;
	}

	public Long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public PayChannel getPayChannel() {
		return payChannel;
	}

	public void setPayChannel(PayChannel payChannel) {
		this.payChannel = payChannel;
	}

	public String getRechargePackageId() {
		return rechargePackageId;
	}

	public void setRechargePackageId(String rechargePackageId) {
		this.rechargePackageId = rechargePackageId;
	}

	public CoinType getCoinType() {
		return coinType;
	}

	public void setCoinType(CoinType coinType) {
		this.coinType = coinType;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public PayStatus getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(PayStatus payStatus) {
		this.payStatus = payStatus;
	}

	public long getDBTagId() {
		return new StringBuffer().append(payChannel).append("_").append(orderId).toString().hashCode();
	}

}
