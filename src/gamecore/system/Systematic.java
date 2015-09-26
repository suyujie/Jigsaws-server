
package gamecore.system;

import gamecore.trigger.TopicPublisher;

/** 
 * 可系统化接口。
 */
public interface Systematic extends TopicPublisher {

	/** 启动。
	 */
	public boolean startup();

	/** 关闭。
	 */
	public void shutdown();
	
	/**
	 * 运行状态
	 */
	public boolean isRun();
	
}
