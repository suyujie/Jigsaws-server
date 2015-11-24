package gamecore.system;

/**
 * 错误码。
 */
public final class ErrorCode {

	// 没有错误
	public final static int NO_ERROR = 0;
	// 用户未登陆或者session失效
	public final static int NO_LOGIN = 1;
	// 操作超时
	public final static int REQ_TIMEOUT = 2;
	// 校验错误
	public final static int CHECK_ERROR = 3;
	// 参数错误
	public final static int PARAM_ERROR = 8;
	// 未知错误
	public final static int UNKNOWN_ERROR = 9;

}
