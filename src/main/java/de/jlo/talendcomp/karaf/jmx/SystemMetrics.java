package de.jlo.talendcomp.karaf.jmx;

public class SystemMetrics {

	private long timestamp = 0l;
	private long heapMemoryUsed = 0l;
	private long heapMemoryMax = 0l;
	private double processCpuLoad = 0d;
	private double systemCpuLoad = 0d;
	
	public SystemMetrics() {
		timestamp = System.currentTimeMillis();
	}

	public long getHeapMemoryUsed() {
		return heapMemoryUsed;
	}

	public void setHeapMemoryUsed(Long heapMemoryUsed) {
		if (heapMemoryUsed != null) {
			this.heapMemoryUsed = heapMemoryUsed;
		}
	}

	public long getHeapMemoryMax() {
		return heapMemoryMax;
	}

	public void setHeapMemoryMax(Long heapMemoryMax) {
		if (heapMemoryMax != null) {
			this.heapMemoryMax = heapMemoryMax;
		}
	}

	public double getProcessCpuLoad() {
		return processCpuLoad;
	}

	public void setProcessCpuLoad(Double processCpuLoad) {
		if (processCpuLoad != null) {
			this.processCpuLoad = processCpuLoad;
		}
	}

	public double getSystemCpuLoad() {
		return systemCpuLoad;
	}

	public void setSystemCpuLoad(Double systemCpuLoad) {
		if (systemCpuLoad != null) {
			this.systemCpuLoad = systemCpuLoad;
		}
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("heapMemoryUsed=");
		sb.append(heapMemoryUsed);
		sb.append("| heapMemoryMax=");
		sb.append(heapMemoryMax);
		sb.append("| processCpuLoad=");
		sb.append(processCpuLoad);
		sb.append("| systemCpuLoad=");
		sb.append(systemCpuLoad);
		return sb.toString();
	}
	
}
