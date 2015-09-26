package server.node.system.rent;

import gamecore.trigger.TopicMessage;
import server.node.system.npc.NpcPlayer;
import server.node.system.player.Player;

/**
 * 租赁系统消息
 */
public final class RentOrderMessage extends TopicMessage {

	//新订单
	public static final String NewOrder = "new_order";
	//订单超时
	public static final String Timeout = "timeout";
	//取消订单
	public static final String Cancel = "cancel";
	//雇佣
	public static final String Hire = "hire";
	//已收取租金，关闭订单
	public static final String Close = "close";

	private Player renter;//出租者
	private Player hirer;//雇佣者
	private NpcPlayer npcPlayer;
	private RentOrder order;

	public RentOrderMessage(String name, Player renter, Player hirer, NpcPlayer npcPlayer) {
		super(name);
		this.renter = renter;
		this.hirer = hirer;
		this.npcPlayer = npcPlayer;
	}

	public RentOrderMessage(String name, Player renter, Player hirer, RentOrder order) {
		super(name);
		this.renter = renter;
		this.hirer = hirer;
		this.order = order;
	}

	public Player getRenter() {
		return renter;
	}

	public void setRenter(Player renter) {
		this.renter = renter;
	}

	public Player getHirer() {
		return hirer;
	}

	public void setHirer(Player hirer) {
		this.hirer = hirer;
	}

	public RentOrder getOrder() {
		return order;
	}

	public void setOrder(RentOrder order) {
		this.order = order;
	}

	public NpcPlayer getNpcPlayer() {
		return npcPlayer;
	}

	public void setNpcPlayer(NpcPlayer npcPlayer) {
		this.npcPlayer = npcPlayer;
	}

}
