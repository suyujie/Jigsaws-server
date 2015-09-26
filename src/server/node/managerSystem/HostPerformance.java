package server.node.managerSystem;

import gamecore.util.Clock;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceStat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 主机性能信息。
 */
public final class HostPerformance implements JSONCodable {

	public final static String CPU_LOAD = "cpu";
	public final static String CPU_SN = "sn";
	public final static String SYS_PERC = "sys";
	public final static String USER_PERC = "user";
	public final static String WAIT_PERC = "wait";
	public final static String IDLE_PERC = "idle";
	public final static String NICE_PERC = "nice";
	public final static String COMBINED_PERC = "combined";

	public final static String MEM_USAGE = "mem";
	public final static String MEM_TOTAL = "total";
	public final static String MEM_ACTUAL_FREE = "actualFree";
	public final static String MEM_ACTUAL_USED = "actualUsed";
	public final static String MEM_FREE = "free";
	public final static String MEM_FREE_PERC = "freePercent";
	public final static String MEM_USED = "used";
	public final static String MEM_USED_PERC = "usedPercent";

	public final static String NET_STAT = "net";
	public final static String NET_IFNAME = "ifName";
	public final static String NET_IFADDR = "ifAddr";
	public final static String NET_SPEED = "speed";
	public final static String NET_RX_PACKETS = "rxPackets";
	public final static String NET_TX_PACKETS = "txPackets";
	public final static String NET_RX_BYTES = "rxBytes";
	public final static String NET_TX_BYTES = "txBytes";
	public final static String NET_RX_ERRORS = "rxErrors";
	public final static String NET_TX_ERRORS = "txErrors";
	public final static String NET_RX_DROPPED = "rxDropped";
	public final static String NET_TX_DROPPED = "txDropped";

	public final static String FS = "fs";
	public final static String FS_DEV = "dev";
	public final static String FS_DIR = "dir";
	public final static String FS_DISK_READ_BS = "diskReadBytes";
	public final static String FS_DISK_WRITE_BS = "diskWriteBytes";
	public final static String FS_USE_PERC = "usePercent";
	public final static String FS_TOTAL = "total";
	public final static String FS_FREE = "free";

	public final static String UPTIME = "uptime";

	public final static String TIMESTAMP = "timestamp";
	public final static String EXPENDED = "expended";

	private final static DecimalFormat DF = new DecimalFormat("#0.00");

	private long timestamp;
	private long expended;

	private List<CpuLoad> cpuLoads;
	private MemUsage memUsage;
	private List<NetStat> netStats;
	private List<FSUsage> fsUsages;

	private double uptime;

	public HostPerformance() {
		this.timestamp = Clock.currentTimeMillis();
		this.cpuLoads = new LinkedList<CpuLoad>();
		this.netStats = new LinkedList<NetStat>();
		this.fsUsages = new LinkedList<FSUsage>();
	}

	protected void markTimestamp() {
		long time = Clock.currentTimeMillis();
		this.expended = time - this.timestamp;
		this.timestamp = time;
	}

	protected void recordUptime(double uptime) {
		this.uptime = Double.parseDouble(DF.format(uptime));
	}

	protected void recordCpu(CpuPerc perc) {
		CpuLoad load = new CpuLoad();
		load.sysPerc = Double.parseDouble(DF.format(perc.getSys() * 100f));
		load.userPerc = Double.parseDouble(DF.format(perc.getUser() * 100f));
		load.waitPerc = Double.parseDouble(DF.format(perc.getWait() * 100f));
		load.idlePerc = Double.parseDouble(DF.format(perc.getIdle() * 100f));
		load.nicePerc = Double.parseDouble(DF.format(perc.getNice() * 100f));
		load.combinedPerc = Double.parseDouble(DF.format(perc.getCombined() * 100f));
		this.cpuLoads.add(load);
	}

	protected void recordMemory(Mem mem) {
		this.memUsage = new MemUsage();
		this.memUsage.total = mem.getTotal();
		this.memUsage.actualFree = mem.getActualFree();
		this.memUsage.actualUsed = mem.getActualUsed();
		this.memUsage.free = mem.getFree();
		this.memUsage.freePercent = Double.parseDouble(DF.format(mem.getFreePercent()));
		this.memUsage.used = mem.getUsed();
		this.memUsage.usedPercent = Double.parseDouble(DF.format(mem.getUsedPercent()));
	}

	protected void recordNetInterface(HostInfo.NetInterface netif, NetInterfaceStat stat) {
		NetStat ns = new NetStat();
		ns.ifName = netif.getName();
		ns.ifAddress = netif.getIpAddress();
		ns.speed = stat.getSpeed();
		ns.rxPackets = stat.getRxPackets();
		ns.txPackets = stat.getTxPackets();
		ns.rxBytes = stat.getRxBytes();
		ns.txBytes = stat.getTxBytes();
		ns.rxErrors = stat.getRxErrors();
		ns.txErrors = stat.getTxErrors();
		ns.rxDropped = stat.getRxDropped();
		ns.txDropped = stat.getTxDropped();
		this.netStats.add(ns);
	}

	protected void recordFileSystem(HostInfo.FileSystem fs, FileSystemUsage usage) {
		FSUsage fsUsage = new FSUsage();
		fsUsage.devName = fs.getDevName();
		fsUsage.dirName = fs.getDirName();
		fsUsage.diskReadBytes = usage.getDiskReadBytes();
		fsUsage.diskWriteBytes = usage.getDiskWriteBytes();
		fsUsage.usePercent = Double.parseDouble(DF.format(usage.getUsePercent() * 100f));
		fsUsage.total = usage.getTotal();
		fsUsage.free = usage.getFree();
		this.fsUsages.add(fsUsage);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		// Uptime
		json.put(UPTIME, this.uptime);

		// CPU
		JSONArray cpuLoad = new JSONArray();
		for (CpuLoad load : this.cpuLoads) {
			JSONObject d = new JSONObject();
			d.put(CPU_SN, cpuLoad.size() + 1);
			d.put(SYS_PERC, load.sysPerc);
			d.put(USER_PERC, load.userPerc);
			d.put(WAIT_PERC, load.waitPerc);
			d.put(IDLE_PERC, load.idlePerc);
			d.put(NICE_PERC, load.nicePerc);
			d.put(COMBINED_PERC, load.combinedPerc);
			cpuLoad.add(d);
		}
		json.put(CPU_LOAD, cpuLoad);

		// Memory
		JSONObject mem = new JSONObject();
		mem.put(MEM_TOTAL, this.memUsage.total);
		mem.put(MEM_ACTUAL_FREE, this.memUsage.actualFree);
		mem.put(MEM_ACTUAL_USED, this.memUsage.actualUsed);
		mem.put(MEM_FREE, this.memUsage.free);
		mem.put(MEM_FREE_PERC, this.memUsage.freePercent);
		mem.put(MEM_USED, this.memUsage.used);
		mem.put(MEM_USED_PERC, this.memUsage.usedPercent);
		json.put(MEM_USAGE, mem);

		// Net stat
		JSONArray netStats = new JSONArray();
		for (NetStat stat : this.netStats) {
			JSONObject d = new JSONObject();
			d.put(NET_IFNAME, stat.ifName);
			d.put(NET_IFADDR, stat.ifAddress);
			d.put(NET_SPEED, stat.speed);
			d.put(NET_RX_PACKETS, stat.rxPackets);
			d.put(NET_TX_PACKETS, stat.txPackets);
			d.put(NET_RX_BYTES, stat.rxBytes);
			d.put(NET_TX_BYTES, stat.txBytes);
			d.put(NET_RX_ERRORS, stat.rxErrors);
			d.put(NET_TX_ERRORS, stat.txErrors);
			d.put(NET_RX_DROPPED, stat.rxDropped);
			d.put(NET_TX_DROPPED, stat.txDropped);
			netStats.add(d);
		}
		json.put(NET_STAT, netStats);

		// File system
		JSONArray fsUsages = new JSONArray();
		for (FSUsage fs : this.fsUsages) {
			JSONObject d = new JSONObject();
			d.put(FS_DEV, fs.devName);
			d.put(FS_DIR, fs.dirName);
			d.put(FS_DISK_READ_BS, fs.diskReadBytes);
			d.put(FS_DISK_WRITE_BS, fs.diskWriteBytes);
			d.put(FS_USE_PERC, fs.usePercent);
			d.put(FS_TOTAL, fs.total);
			d.put(FS_FREE, fs.free);
			fsUsages.add(d);
		}
		json.put(FS, fsUsages);

		// 采集时间戳
		json.put(TIMESTAMP, this.timestamp);
		// 采集消耗时间
		json.put(EXPENDED, this.expended);

		return json;
	}

	/**
	 * CPU 负载。
	 */
	public class CpuLoad {
		protected double userPerc;
		protected double sysPerc;
		protected double waitPerc;
		protected double idlePerc;
		protected double nicePerc;
		protected double combinedPerc;

		protected CpuLoad() {
		}
	}

	/**
	 * 内存使用率。
	 */
	public class MemUsage {
		protected long total;
		protected long actualFree;
		protected long actualUsed;
		protected long free;
		protected double freePercent;
		protected long used;
		protected double usedPercent;

		protected MemUsage() {
		}
	}

	/**
	 * 网络接口状态。
	 */
	public class NetStat {
		protected String ifName;
		protected String ifAddress;
		protected long speed;
		protected long rxPackets;
		protected long txPackets;
		protected long rxBytes;
		protected long txBytes;
		protected long rxErrors;
		protected long txErrors;
		protected long rxDropped;
		protected long txDropped;

		protected NetStat() {
		}
	}

	/**
	 * 文件系统使用率。
	 */
	public class FSUsage {
		protected String devName;
		protected String dirName;
		protected long diskReadBytes;
		protected long diskWriteBytes;
		protected double usePercent;
		protected long total;
		protected long free;

		protected FSUsage() {
		}
	}
}
