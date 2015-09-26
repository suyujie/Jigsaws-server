package server.node.system.handbook;

public class HandbookMaking {

	private String name;//机器人套装名称
	private Integer diamons;//奖励钻石
	private String qualityId;//对应的材质在部件xml表里的id
	private int[] qualityIds;//对应的材质在部件xml表里的id

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDiamons() {
		return diamons;
	}

	public void setDiamons(Integer diamons) {
		this.diamons = diamons;
	}

	public String getQualityId() {
		return qualityId;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}

	public int[] getQualityIds() {
		return qualityIds;
	}

	public void setQualityIds(int[] qualityIds) {
		this.qualityIds = qualityIds;
	}

}