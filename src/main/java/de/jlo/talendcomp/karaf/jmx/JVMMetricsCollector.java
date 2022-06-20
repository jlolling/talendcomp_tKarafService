package de.jlo.talendcomp.karaf.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JVMMetricsCollector {
	
	private static Logger logger = LogManager.getLogger(JVMMetricsCollector.class);
	private BaseClient baseClient = null;
	private ObjectName onMemory = null;
	private ObjectName onOperatingSystem = null;
	private long lastExecutionTime = 0l;
	private long interval = 1000l;

	public JVMMetricsCollector(BaseClient baseClient) throws MalformedObjectNameException {
		if (baseClient == null || baseClient.isConnected() == false) {
			throw new IllegalArgumentException("baseClient cannot be null");
		} else if (baseClient.isConnected() == false) {
			throw new IllegalArgumentException("baseClient must be connected");
		}
		this.baseClient = baseClient;
		onMemory = new ObjectName("java.lang:type=Memory");
		onOperatingSystem = new ObjectName("java.lang:type=OperatingSystem");
	}

	public SystemMetrics fetchSystemMetrics() throws Exception {
		try {
			SystemMetrics metrics = new SystemMetrics();
			CompositeData cdMemory = (CompositeData) baseClient.getAttributeValue(onMemory, "HeapMemoryUsage");
			metrics.setHeapMemoryUsed((Long) cdMemory.get("used"));
			metrics.setHeapMemoryMax((Long) cdMemory.get("max"));
			metrics.setProcessCpuLoad((Double) baseClient.getAttributeValue(onOperatingSystem, "ProcessCpuLoad"));
			metrics.setSystemCpuLoad((Double) baseClient.getAttributeValue(onOperatingSystem, "SystemCpuLoad"));
			return metrics;
		} catch (Exception e) {
			logger.error("fetchSystemMetrics failed: " + e.getMessage(), e);
			throw e;
		}
	}

	public boolean next() {
		if (Thread.currentThread().isInterrupted()) {
			return false;
		}
		long now = System.currentTimeMillis();
		long diff = interval - (now - lastExecutionTime);
		if (diff > 0) {
			try {
				Thread.sleep(diff);
			} catch (InterruptedException e) {
				return false;
			}
		}
		lastExecutionTime = now;
		return true;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval * 1000l;
	}

}
