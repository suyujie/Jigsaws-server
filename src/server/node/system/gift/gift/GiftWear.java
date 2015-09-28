package server.node.system.gift.gift;

import server.node.system.Root;
import server.node.system.gift.GiftStatus;
import server.node.system.player.Player;

public class GiftWear {

	public Long id;
	public Long giverId;
	public int num;
	public long giveTime;
	public GiftStatus status;// 1有,可以领取,2领完

	public GiftWear() {
	}

	public GiftWear(Long id, Long giverId, int num, long giveTime, GiftStatus status) {
		this.id = id;
		this.giverId = giverId;
		this.num = num;
		this.giveTime = giveTime;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGiverId() {
		return giverId;
	}

	public void setGiverId(Long giverId) {
		this.giverId = giverId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public long getGiveTime() {
		return giveTime;
	}

	public void setGiveTime(long giveTime) {
		this.giveTime = giveTime;
	}

	public GiftStatus getStatus() {
		return status;
	}

	public void setStatus(GiftStatus status) {
		this.status = status;
	}

	public void accept(Player player) {
		if (status == GiftStatus.Wait) {// 还没有领取,可以领了
			status = GiftStatus.Accept;
		}
	}

}
