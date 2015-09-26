package server.node.system.gamePrice;

import java.io.Serializable;

public class GamePriceMaking implements Serializable {

	private static final long serialVersionUID = 7190566999357151754L;

	private Integer colorPrice;
	private String workerPriceStr;
	private String projectPriceStr;
	private String projectTimeStr;

	public Integer getColorPrice() {
		return colorPrice;
	}

	public void setColorPrice(Integer colorPrice) {
		this.colorPrice = colorPrice;
	}

	public String getWorkerPriceStr() {
		return workerPriceStr;
	}

	public void setWorkerPriceStr(String workerPriceStr) {
		this.workerPriceStr = workerPriceStr;
	}

	public String getProjectPriceStr() {
		return projectPriceStr;
	}

	public void setProjectPriceStr(String projectPriceStr) {
		this.projectPriceStr = projectPriceStr;
	}

	public String getProjectTimeStr() {
		return projectTimeStr;
	}

	public void setProjectTimeStr(String projectTimeStr) {
		this.projectTimeStr = projectTimeStr;
	}

}
