package server.node.system.buff;

import java.io.Serializable;

public class BuffMaking implements Serializable {

	private static final long serialVersionUID = -5567867242030265911L;
	private Integer id;
	private Integer type;
	private Integer useType;
	private Integer ifType;
	private String ifnum;
	private Integer minvalues;
	private Integer values;
	private String cont;
	private Integer conType;
	private Integer valuesXi;

	public Integer getId() {
		return id;
	}

	public String getIfnum() {
		return ifnum;
	}

	public void setIfnum(String ifnum) {
		this.ifnum = ifnum;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getUseType() {
		return useType;
	}

	public void setUseType(Integer useType) {
		this.useType = useType;
	}

	public Integer getIfType() {
		return ifType;
	}

	public void setIfType(Integer ifType) {
		this.ifType = ifType;
	}

	public Integer getValues() {
		return values;
	}

	public void setValues(Integer values) {
		this.values = values;
	}

	public String getCont() {
		return cont;
	}

	public void setCont(String cont) {
		this.cont = cont;
	}

	public Integer getConType() {
		return conType;
	}

	public void setConType(Integer conType) {
		this.conType = conType;
	}

	public Integer getMinvalues() {
		return minvalues;
	}

	public void setMinvalues(Integer minvalues) {
		this.minvalues = minvalues;
	}

	public Integer getValuesXi() {
		return valuesXi;
	}

	public void setValuesXi(Integer valuesXi) {
		this.valuesXi = valuesXi;
	}

}