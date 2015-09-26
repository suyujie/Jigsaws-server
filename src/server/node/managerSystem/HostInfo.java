package server.node.managerSystem;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 主机静态信息。
 */
public final class HostInfo implements JSONCodable {

	private final static String SYS = "sys";
	private final static String SYS_NAME = "name";
	private final static String SYS_ARCH = "arch";
	private final static String SYS_MACHINE = "machine";
	private final static String SYS_VENDOR = "vendor";
	private final static String SYS_VERSION = "version";
	private final static String CPU = "cpu";
	private final static String MEM = "mem";
	private final static String SWAP = "swap";
	private final static String FILE_SYSTEM = "fileSystem";
	private final static String NET_INTERFACE = "netInterface";
	private final static String PID = "pid";

	private String sysName;
	private String sysArch;
	private String sysMachine;
	private String sysVendor;
	private String sysVersion;

	private List<Cpu> cpuList;
	private int totalSockets;

	// 单位：字节
	private long totalMemory;
	private long totalSwap;

	private List<FileSystem> fileSystemList;

	private List<NetInterface> netInterfaceList;

	private long pid;

	public HostInfo() {
		this.cpuList = new LinkedList<Cpu>();
		this.fileSystemList = new LinkedList<FileSystem>();
		this.netInterfaceList = new LinkedList<NetInterface>();
	}

	protected void setSysInfo(String name, String arch, String machine, String vendor, String version) {
		this.sysName = name;
		this.sysArch = arch;
		this.sysMachine = machine;
		this.sysVendor = vendor;
		this.sysVersion = version;
	}

	public String getSysName() {
		return this.sysName;
	}

	public String getSysArch() {
		return this.sysArch;
	}

	public String getSysMachine() {
		return this.sysMachine;
	}

	public String getSysVendor() {
		return this.sysVendor;
	}

	public String getSysVersion() {
		return this.sysVersion;
	}

	protected void setPid(long pid) {
		this.pid = pid;
	}

	public long getPid() {
		return this.pid;
	}

	protected void addCpu(int sn, int mhz, String vendor, String model, long cacheSize) {
		Cpu cpu = new Cpu(sn, mhz, vendor, model, cacheSize);
		this.cpuList.add(cpu);
	}

	protected void setTotalSockets(int value) {
		this.totalSockets = value;
	}

	public List<Cpu> getCpuList() {
		return this.cpuList;
	}

	public int getTotalCpu() {
		return this.cpuList.size();
	}

	public int getTotalSockets() {
		return this.totalSockets;
	}

	protected void setMemoryInfo(long totalMemory, long totalSwap) {
		this.totalMemory = totalMemory;
		this.totalSwap = totalSwap;
	}

	public long getTotalMemory() {
		return this.totalMemory;
	}

	public long getTotalSwap() {
		return this.totalSwap;
	}

	protected void addFileSystem(String devName, String dirName, String sysTypeName, String typeName, long totalSize) {
		this.fileSystemList.add(new FileSystem(devName, dirName, sysTypeName, typeName, totalSize));
	}

	public List<FileSystem> getFileSystemList() {
		return this.fileSystemList;
	}

	protected void addNetInterface(String name, String ipAddress, String broadcast, String macAddress, String netmask, String dest, long metric, long mtu) {
		this.netInterfaceList.add(new NetInterface(name, ipAddress, broadcast, macAddress, netmask, dest, metric, mtu));
	}

	public List<NetInterface> getNetInterfaceList() {
		return this.netInterfaceList;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		// PID
		json.put(PID, this.pid);

		// Sys
		JSONObject sys = new JSONObject();
		sys.put(SYS_NAME, this.sysName);
		sys.put(SYS_ARCH, this.sysArch);
		sys.put(SYS_VENDOR, this.sysVendor);
		sys.put(SYS_MACHINE, this.sysMachine);
		sys.put(SYS_VERSION, this.sysVersion);
		json.put(SYS, sys);

		// CPU
		JSONArray cpuList = new JSONArray();
		for (HostInfo.Cpu c : this.cpuList) {
			cpuList.add(c.toJSON());
		}
		json.put(CPU, cpuList);

		// Mem & Swap
		json.put(MEM, this.totalMemory);
		json.put(SWAP, this.totalSwap);

		// File System
		JSONArray fsList = new JSONArray();
		for (HostInfo.FileSystem fs : this.fileSystemList) {
			fsList.add(fs.toJSON());
		}
		json.put(FILE_SYSTEM, fsList);

		// Net Interface
		JSONArray netIfList = new JSONArray();
		for (HostInfo.NetInterface nif : this.netInterfaceList) {
			netIfList.add(nif.toJSON());
		}
		json.put(NET_INTERFACE, netIfList);

		return json;
	}

	/**
	 * CPU
	 */
	public class Cpu implements JSONCodable {
		private final static String SN = "sn";
		private final static String MHZ = "mhz";
		private final static String VENDOR = "vendor";
		private final static String MODEL = "model";
		private final static String CACHE = "cache";

		private int sn;
		private int frequency;
		private String vendor;
		private String model;
		private long cacheSize;

		protected Cpu(int sn, int mhz, String vendor, String model, long cacheSize) {
			this.sn = sn;
			this.frequency = mhz;
			this.vendor = vendor;
			this.model = model;
			this.cacheSize = cacheSize;
		}

		public int getFrequency() {
			return this.frequency;
		}

		public String getVendor() {
			return this.vendor;
		}

		public String getModel() {
			return this.model;
		}

		public long getCacheSize() {
			return this.cacheSize;
		}

		@Override
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			json.put(SN, this.sn);
			json.put(MHZ, this.frequency);
			json.put(VENDOR, this.vendor);
			json.put(MODEL, this.model);
			json.put(CACHE, this.cacheSize);
			return json;
		}
	}

	/**
	 * File System
	 */
	public class FileSystem implements JSONCodable {
		private final static String DEV_NAME = "dev";
		private final static String DIR_NAME = "dir";
		private final static String SYSTYPE = "sysType";
		private final static String TYPE = "type";
		private final static String SIZE = "size";

		// 分区盘符名称
		private String devName;
		// 分区目录名称
		private String dirName;
		// 系统类型，Ext、NTFS...
		private String sysTypeName;
		// 类型名称
		private String typeName;
		// 总大小，单位 KB
		private long totalSize;

		protected FileSystem(String devName, String dirName, String sysTypeName, String typeName, long totalSize) {
			this.devName = devName;
			this.dirName = dirName;
			this.sysTypeName = sysTypeName;
			this.typeName = typeName;
			this.totalSize = totalSize;
		}

		public String getDevName() {
			return this.devName;
		}

		public String getDirName() {
			return this.dirName;
		}

		public String getSysTypeName() {
			return this.sysTypeName;
		}

		public String getTypeName() {
			return this.typeName;
		}

		public long getTotalSize() {
			return this.totalSize;
		}

		@Override
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			json.put(DEV_NAME, this.devName);
			json.put(DIR_NAME, this.dirName);
			json.put(SYSTYPE, this.sysTypeName);
			json.put(TYPE, this.typeName);
			json.put(SIZE, this.totalSize);
			return json;
		}
	}

	/**
	 * 网络接口。
	 */
	public class NetInterface implements JSONCodable {
		private final static String NAME = "name";
		private final static String IP = "ip";
		private final static String MAC = "mac";
		private final static String BROADCAST = "broadcast";
		private final static String NETMASK = "netmask";
		private final static String DEST = "destination";
		private final static String METRIC = "metric";
		private final static String MTU = "mtu";

		// 接口名
		private String name;
		// IP 地址
		private String ipAddress;
		// 网关广播地址
		private String broadcast;
		// 网卡 MAC 地址
		private String macAddress;
		// 子网掩码
		private String netmask;
		// 描述信息
		private String destination;
		// Metric
		private long metric;
		// MTU
		private long mtu;

		protected NetInterface(String name, String ipAddress, String broadcast, String macAddress, String netmask, String dest, long metric, long mtu) {
			this.name = name;
			this.ipAddress = ipAddress;
			this.broadcast = broadcast;
			this.macAddress = macAddress;
			this.netmask = netmask;
			this.destination = dest;
			this.metric = metric;
			this.mtu = mtu;
		}

		public String getName() {
			return this.name;
		}

		public String getIpAddress() {
			return this.ipAddress;
		}

		public String getBroadcast() {
			return this.broadcast;
		}

		public String getMacAddress() {
			return this.macAddress;
		}

		public String getNetmask() {
			return this.netmask;
		}

		public String getDestination() {
			return this.destination;
		}

		public long getMetric() {
			return this.metric;
		}

		public long getMTU() {
			return this.mtu;
		}

		@Override
		public JSONObject toJSON() {
			JSONObject json = new JSONObject();
			json.put(NAME, this.name);
			json.put(IP, this.ipAddress);
			json.put(MAC, this.macAddress);
			json.put(BROADCAST, this.broadcast);
			json.put(NETMASK, this.netmask);
			json.put(DEST, this.destination);
			json.put(METRIC, this.metric);
			json.put(MTU, this.mtu);
			return json;
		}
	}
}
