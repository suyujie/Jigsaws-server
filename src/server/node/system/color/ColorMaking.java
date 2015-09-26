package server.node.system.color;

import java.io.Serializable;

public class ColorMaking implements Serializable {

	private static final long serialVersionUID = 2773436443468102070L;

	private Integer id;
	private String name;

	private Integer red;
	private Integer green;
	private Integer blue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRed() {
		return red;
	}

	public void setRed(Integer red) {
		this.red = red;
	}

	public Integer getGreen() {
		return green;
	}

	public void setGreen(Integer green) {
		this.green = green;
	}

	public Integer getBlue() {
		return blue;
	}

	public void setBlue(Integer blue) {
		this.blue = blue;
	}

}
