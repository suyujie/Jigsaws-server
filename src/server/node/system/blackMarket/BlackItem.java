package server.node.system.blackMarket;

/**
 * 黑市商品
 */
public class BlackItem {

	private long id;
	private boolean buyed = false;
	private BlackItemType type;
	private int gold;
	private String item;
	private int num;

	public BlackItem() {
	}

	public BlackItem(long id, boolean buyed, BlackItemType type, int gold, String item, int num) {
		super();
		this.id = id;
		this.buyed = buyed;
		this.type = type;
		this.gold = gold;
		this.item = item;
		this.num = num;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isBuyed() {
		return buyed;
	}

	public void setBuyed(boolean buyed) {
		this.buyed = buyed;
	}

	public BlackItemType getType() {
		return type;
	}

	public void setType(BlackItemType type) {
		this.type = type;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

}
