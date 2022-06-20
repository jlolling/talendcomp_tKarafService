package de.jlo.talendcomp.karaf.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CXFMetricsCollector {
	
	private static Logger logger = LogManager.getLogger(CXFMetricsCollector.class);
	private BaseClient baseClient = null;
	private List<ObjectName> listMetricObjectNames = new ArrayList<ObjectName>();
	private List<ObjectName> listNewMetricObjectNames = new ArrayList<ObjectName>();
	private long lastExecutionTime = 0l;
	private long interval = 1000l;
	private List<ServiceMetric> lastListServiceMetrics = new ArrayList<ServiceMetric>();
	private boolean ignoreSummaryMetrics = false;
	private String artifactPattern = null;
	private long timeBetweenObjectNameRefresh = 10000; // 10s
	private long lastTimeObjectNameRefreshed = 0;
	
	public CXFMetricsCollector(BaseClient baseClient) {
		if (baseClient == null || baseClient.isConnected() == false) {
			throw new IllegalArgumentException("baseClient cannot be null");
		} else if (baseClient.isConnected() == false) {
			throw new IllegalArgumentException("baseClient must be connected");
		}
		this.baseClient = baseClient;
	}
	
	public void setupCXFTotalsMetricObjectNames() throws Exception {
		listNewMetricObjectNames.clear();
		logger.debug("Fetching metric object names and filter with: " + artifactPattern);
		Pattern p = null;
		if (artifactPattern != null && artifactPattern.trim().isEmpty() == false) {
			p = Pattern.compile(artifactPattern);
		}
		ObjectName onpattern = new ObjectName("org.apache.cxf:type=Metrics.Server,*");
		Set<ObjectInstance> result = baseClient.getmBeanServerConnection().queryMBeans(onpattern, null);
		for (ObjectInstance oi : result) {
			if ("com.codahale.metrics.JmxReporter$JmxTimer".equals(oi.getClassName())) {
				ObjectName name = oi.getObjectName();
				if ("Totals".equals(name.getKeyProperty("Attribute"))) {
					if (p != null) {
						Matcher matcher = p.matcher(name.getKeyProperty("bus.id"));
						if (matcher.find()) {
							if (listMetricObjectNames.contains(oi.getObjectName()) == false) {
								listMetricObjectNames.add(oi.getObjectName());
								listNewMetricObjectNames.add(oi.getObjectName());
							}
						}
					} else {
						if (listMetricObjectNames.contains(oi.getObjectName()) == false) {
							listMetricObjectNames.add(oi.getObjectName());
							listNewMetricObjectNames.add(oi.getObjectName());
						}
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(listMetricObjectNames.size() + " metric object names fetched.");
			for (ObjectName name : listMetricObjectNames) {
				logger.debug(name.getCanonicalName());
			}
		}
	}

	public List<ObjectName> getListMetricObjectNames() {
		return listMetricObjectNames;
	}
	
	public List<ObjectName> getListNewMetricObjectNames() {
		return listNewMetricObjectNames;
	}

	private String getArtifactIdFromObjectName(ObjectName name) {
		String artifactId = name.getKeyProperty("bus.id");
		if (artifactId != null) {
			int pos = artifactId.indexOf(".");
			if (pos > 0) {
				artifactId = artifactId.substring(pos + 1);
				pos = artifactId.indexOf("-cxf");
				if (pos > 0) {
					artifactId = artifactId.substring(0, pos);
				}
			}
		}
		return artifactId;
	}
	
	public ServiceMetric fetchServiceMetric(ObjectName objectName, String operation) throws Exception {
		String name = getArtifactIdFromObjectName(objectName);
		if (operation == null || operation.trim().isEmpty()) {
			String op = objectName.getKeyProperty("Operation");
			if (op != null && op.trim().isEmpty() == false) {
				operation = op;
			}
		}
		ServiceMetric metric = new ServiceMetric();
		metric.setServiceName(name);
		metric.setOperation(operation);
		Double meanDuration = (Double) baseClient.getAttributeValue(objectName, "Mean");
		metric.setDurationMean(meanDuration);
		Long count = (Long) baseClient.getAttributeValue(objectName, "Count");
		metric.setCount(count);
		setupDiffToLastMeasurement(metric);
		return metric;
	}
	
	private void setupDiffToLastMeasurement(ServiceMetric metric) {
		int index = lastListServiceMetrics.indexOf(metric);
		if (index >= 0) {
			ServiceMetric last = lastListServiceMetrics.get(index);
			metric.setCountLast(last.getCount());
		}
	}
	
	public List<ServiceMetric> fetchServiceMetrics() throws Exception {
		List<ServiceMetric> listServiceMetrics = new ArrayList<ServiceMetric>();
		for (ObjectName on : listMetricObjectNames) {
			if (ignoreSummaryMetrics) {
				String op = on.getKeyProperty("Operation");
				if (op != null && op.trim().isEmpty() == false) {
					listServiceMetrics.add(fetchServiceMetric(on, op));
				}
			} else {
				listServiceMetrics.add(fetchServiceMetric(on, null));
			}
		}
		lastListServiceMetrics = listServiceMetrics;
		return listServiceMetrics;
	}
	
	public boolean next() throws Exception {
		if (Thread.currentThread().isInterrupted()) {
			return false;
		}
		long now = System.currentTimeMillis();
		if (lastTimeObjectNameRefreshed == 0 || (now - lastTimeObjectNameRefreshed) > timeBetweenObjectNameRefresh) {
			setupCXFTotalsMetricObjectNames();
			lastTimeObjectNameRefreshed = now;
		}
		long diff = interval - (now - lastExecutionTime);
		if (diff > 0 && lastExecutionTime > 0l) {
			try {
				Thread.sleep(diff);
			} catch (InterruptedException e) {
				return false;
			}
		}
		boolean run = false;
		if (interval > 0 || lastExecutionTime == 0l) {
			run = true;
		}
		lastExecutionTime = System.currentTimeMillis();
		// if the interval == 0 we want only one run
		return run;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval * 1000l;
	}

	public boolean isIgnoreSummaryMetrics() {
		return ignoreSummaryMetrics;
	}

	public void setIgnoreSummaryMetrics(boolean ignoreSummaryMetrics) {
		this.ignoreSummaryMetrics = ignoreSummaryMetrics;
	}

	public String getArtifactPattern() {
		return artifactPattern;
	}

	public void setArtifactPattern(String artifactPattern) {
		this.artifactPattern = artifactPattern;
	}

	public void setTimeBetweenServiceRefresh(Integer timeBetweenObjectNameRefresh) {
		if (timeBetweenObjectNameRefresh != null && timeBetweenObjectNameRefresh > 0) {
			this.timeBetweenObjectNameRefresh = timeBetweenObjectNameRefresh * 1000;
		}
	}

}
