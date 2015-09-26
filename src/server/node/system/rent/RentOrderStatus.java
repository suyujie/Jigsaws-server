package server.node.system.rent;

public enum RentOrderStatus {
	
	Wait(0),
	Rent(1),
	Close(2);
	
	private int code;
	
	private RentOrderStatus(int code){
		this.code = code;
	}
	
	public int asCode(){
		return code;
	}
}
