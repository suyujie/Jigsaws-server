package server.node.managerSystem;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SysInfo;

/**
 * 监视器。
 */
public final class Monitor {

	private final static Logger logger = LogManager.getLogger(Monitor.class.getName());

	private HostInfo hostInfo;

	protected Monitor() {
	}

	public void release() {
	}

	/**
	 * 获取主机信息。
	 * @return
	 */
	public synchronized HostInfo getHostInfo() {
		if (null == this.hostInfo) {
			this.hostInfo = new HostInfo();

			Sigar sigar = new Sigar();
			try {
				// 系统信息
				SysInfo sys = new SysInfo();
				sys.gather(sigar);
				this.hostInfo.setSysInfo(sys.getName(), sys.getArch(), sys.getMachine(), sys.getVendor(), sys.getVersion());

				// PID
				this.hostInfo.setPid(sigar.getPid());

				// CPU 信息
				CpuInfo[] cpuInfos = sigar.getCpuInfoList();
				int sn = 1;
				for (CpuInfo cpu : cpuInfos) {
					this.hostInfo.addCpu(sn, cpu.getMhz(), cpu.getVendor(), cpu.getModel(), cpu.getCacheSize());
					++sn;
				}
				this.hostInfo.setTotalSockets(cpuInfos[0].getTotalSockets());

				// 内存信息
				this.hostInfo.setMemoryInfo(sigar.getMem().getTotal(), sigar.getSwap().getTotal());

				// 文件系统信息
				FileSystem[] fsList = sigar.getFileSystemList();
				for (FileSystem fs : fsList) {
					long total = 0;
					switch (fs.getType()) {
					case FileSystem.TYPE_LOCAL_DISK:
						FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
						total = usage.getTotal();
						break;
					case FileSystem.TYPE_UNKNOWN:
					case FileSystem.TYPE_NONE:
						break;
					default:
						break;
					}
					this.hostInfo.addFileSystem(fs.getDevName(), fs.getDirName(), fs.getSysTypeName(), fs.getTypeName(), total);
				}

				// 网络设备信息
				String[] ifaces = sigar.getNetInterfaceList();
				for (String iface : ifaces) {
					NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(iface);
					if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || NetFlags.LOOPBACK_ADDRESS_V6.equals(cfg.getAddress())
							|| (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0 || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
						continue;
					}

					this.hostInfo.addNetInterface(cfg.getName(), cfg.getAddress(), cfg.getBroadcast(), cfg.getHwaddr(), cfg.getNetmask(), cfg.getDescription(), cfg.getMetric(),
							cfg.getMtu());
				}
			} catch (SigarException e) {
				logger.error("#getHostInfo", e);
			} catch (Exception e) {
				logger.error("#getHostInfo", e);
			} finally {
				try {
					sigar.close();
				} catch (Exception e) {
					// Nothing
				}
			}
		}

		return this.hostInfo;
	}

	/**
	 * 快照性能数据。
	 * @return
	 */
	public synchronized HostPerformance snapshotPerformance() {
		return this.snapshot();
	}

	public void printHostInfo(StringBuilder buf) {
		buf.append("----------------------------------------\n");

		DecimalFormat df = new DecimalFormat("#0.00");

		HostInfo info = this.getHostInfo();

		String memTotal = df.format((double) info.getTotalMemory() / (1024l * 1024l * 1024l));
		String swapTotal = df.format((double) info.getTotalSwap() / (1024l * 1024l));

		buf.append("System Name:    ").append(info.getSysName()).append("\n");
		buf.append("System Arch:    ").append(info.getSysArch()).append("\n");
		buf.append("System Vendor:  ").append(info.getSysVendor()).append("\n");
		buf.append("System Machine: ").append(info.getSysMachine()).append("\n");
		buf.append("System Version: ").append(info.getSysVersion()).append("\n");
		buf.append("CPU Cores:      ").append(info.getTotalCpu()).append("\n");
		buf.append("CPU Sockets:    ").append(info.getTotalSockets()).append("\n");
		buf.append("CPU Clock:      ").append(info.getCpuList().get(0).getFrequency()).append(" MHz\n");
		buf.append("CPU Vendor:     ").append(info.getCpuList().get(0).getVendor()).append("\n");
		buf.append("CPU Model:      ").append(info.getCpuList().get(0).getModel()).append("\n");
		buf.append("CPU Cache Size: ").append(info.getCpuList().get(0).getCacheSize()).append(" KB/Core\n");
		buf.append("Memory Total:   ").append(memTotal).append(" GB\n");
		buf.append("Swap Total:     ").append(swapTotal).append(" MB\n");
		buf.append("File System:").append("\n");
		for (HostInfo.FileSystem fs : info.getFileSystemList()) {
			buf.append("    + ").append(fs.getDirName()).append("\n");
			buf.append("      Device:   ").append(fs.getDevName()).append("\n");
			buf.append("      Sys Type: ").append(fs.getSysTypeName()).append("\n");
			buf.append("      Type:     ").append(fs.getTypeName()).append("\n");
			buf.append("      Total:    ").append(df.format((double) fs.getTotalSize() / (1024l * 1024l))).append(" GB\n");
		}
		buf.append("Net Interface:").append("\n");
		for (HostInfo.NetInterface iface : info.getNetInterfaceList()) {
			buf.append("    + ").append(iface.getName()).append("\n");
			buf.append("      Destination: ").append(iface.getDestination()).append("\n");
			buf.append("      IP Address:  ").append(iface.getIpAddress()).append("\n");
			buf.append("      MAC Address: ").append(iface.getMacAddress()).append("\n");
			buf.append("      Netmask:     ").append(iface.getNetmask()).append("\n");
			buf.append("      Broadcast:   ").append(iface.getBroadcast()).append("\n");
			buf.append("      Metric:      ").append(iface.getMetric()).append("\n");
			buf.append("      MTU:         ").append(iface.getMTU()).append("\n");
		}

		buf.append("----------------------------------------\n");

		buf.append("PID: ").append(info.getPid()).append("\n");

		buf.append("----------------------------------------\n");

		df = null;
	}

	private HostPerformance snapshot() {
		HostPerformance perf = new HostPerformance();
		Sigar sigar = new Sigar();
		try {
			// 系统运行时间
			perf.recordUptime(sigar.getUptime().getUptime());

			// CPU 负载
			CpuPerc[] list = sigar.getCpuPercList();
			for (CpuPerc perc : list) {
				perf.recordCpu(perc);
			}

			// 内存使用率
			Mem mem = sigar.getMem();
			perf.recordMemory(mem);

			HostInfo info = this.getHostInfo();

			// 网络接口状态
			for (HostInfo.NetInterface ni : info.getNetInterfaceList()) {
				perf.recordNetInterface(ni, sigar.getNetInterfaceStat(ni.getName()));
			}

			// 文件系统
			for (HostInfo.FileSystem fs : info.getFileSystemList()) {
				try {
					perf.recordFileSystem(fs, sigar.getFileSystemUsage(fs.getDirName()));
				} catch (Exception e) {
				}
			}
		} catch (SigarException e) {
			logger.error("#snapshot", e);
		} catch (Exception e) {
			logger.error("#snapshot", e);
		} finally {
			try {
				sigar.close();
			} catch (Exception e) {
				// Nothing
			}
		}

		perf.markTimestamp();
		return perf;
	}

}
