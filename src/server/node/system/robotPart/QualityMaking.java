package server.node.system.robotPart;

public class QualityMaking {

	private Integer ratio;
	private Integer sameQualityNum;
	private Integer price;
	public int evolveExp;//进化需要经验
	public int useEvolveExp;//提供给进化的经验（被吞掉的时候产生）
	public int evolveMoney;//进化需要钱

	public QualityMaking() {
	}

	public QualityMaking(Integer ratio, Integer sameQualityNum, Integer price) {
		super();
		this.ratio = ratio;
		this.sameQualityNum = sameQualityNum;
		this.price = price;
	}

	public Integer getRatio() {
		return ratio;
	}

	public void setRatio(Integer ratio) {
		this.ratio = ratio;
	}

	public Integer getSameQualityNum() {
		return sameQualityNum;
	}

	public void setSameQualityNum(Integer sameQualityNum) {
		this.sameQualityNum = sameQualityNum;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public int getEvolveExp() {
		return evolveExp;
	}

	public void setEvolveExp(int evolveExp) {
		this.evolveExp = evolveExp;
	}

	public int getUseEvolveExp() {
		return useEvolveExp;
	}

	public void setUseEvolveExp(int useEvolveExp) {
		this.useEvolveExp = useEvolveExp;
	}

	public int getEvolveMoney() {
		return evolveMoney;
	}

	public void setEvolveMoney(int evolveMoney) {
		this.evolveMoney = evolveMoney;
	}

}