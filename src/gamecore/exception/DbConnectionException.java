package gamecore.exception;

class DbConnectionException extends Exception {

	private static final long serialVersionUID = 8425728935102459453L;

	public DbConnectionException(String msg) {
		super(msg);
	}
}