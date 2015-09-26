package server.node.managerSystem;

import gamecore.task.TaskCenter;
import gamecore.util.Clock;

import com.alibaba.fastjson.JSONObject;

/**
 * 虚拟机统计信息。
 */
public final class VMStatistics implements JSONCodable {

	public final static String CollectTimestamp = "t";
	public final static String MaxThreadNum = "mtn";
	public final static String RealtimeThreadNum = "rtn";
	public final static String RealtimeQueueSize = "rqs";
	public final static String RealtimeScheduleNum = "rsn";
	public final static String RealtimeWindowQueueSize = "rwqs";

	public final static String TotalMemory = "tm";
	public final static String FreeMemory = "fm";
	public final static String MaxMemory = "mm";
	public final static String AvailableProcessors = "ap";

	private long collectTimestamp;

	private int maxThreadNum;
	private int realtimeThreadNum;
	private int realtimeQueueSize;
	private int realtimeScheduleNum;
	private int realtimeWindowQueueSize;

	private long totalMemory;
	private long freeMemory;
	private long maxMemory;
	private int availableProcessors;

	public VMStatistics() {
	}

	public void collect() {
		this.collectTimestamp = Clock.currentTimeMillis();

		this.maxThreadNum = TaskCenter.getInstance().getMaxThread();
		this.realtimeThreadNum = TaskCenter.getInstance().snapshotThreadNum();
		this.realtimeQueueSize = TaskCenter.getInstance().snapshotQueueSize();
		this.realtimeScheduleNum = TaskCenter.getInstance().snapshotScheduleNum();
		this.realtimeWindowQueueSize = TaskCenter.getInstance().getWindowQueueSize();

		Runtime rt = Runtime.getRuntime();
		this.totalMemory = rt.totalMemory();
		this.freeMemory = rt.freeMemory();
		this.maxMemory = rt.maxMemory();
		this.availableProcessors = rt.availableProcessors();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		JSONObject thread = new JSONObject();
		thread.put(CollectTimestamp, this.collectTimestamp);
		thread.put(MaxThreadNum, this.maxThreadNum);
		thread.put(RealtimeThreadNum, this.realtimeThreadNum);
		thread.put(RealtimeQueueSize, this.realtimeQueueSize);
		thread.put(RealtimeScheduleNum, this.realtimeScheduleNum);
		thread.put(RealtimeWindowQueueSize, this.realtimeWindowQueueSize);

		json.put("thread", thread);

		JSONObject mem = new JSONObject();

		mem.put(CollectTimestamp, this.collectTimestamp);
		mem.put(TotalMemory, this.totalMemory);
		mem.put(FreeMemory, this.freeMemory);
		mem.put(MaxMemory, this.maxMemory);
		mem.put(AvailableProcessors, this.availableProcessors);

		json.put("mem", mem);

		return json;
	}
}
