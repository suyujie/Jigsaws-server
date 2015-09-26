package server.node.system.feedback;


public class Feedback {

	private long id;
	private String msg;
	private String email;

	public Feedback(long id, String msg, String email) {
		super();
		this.id = id;
		this.msg = msg;
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
