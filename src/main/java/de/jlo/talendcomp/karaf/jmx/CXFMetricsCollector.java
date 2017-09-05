package de.jlo.talendcomp.karaf.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class CXFMetricsCollector {
	
	private static Logger logger = Logger.getLogger(CXFMetricsCollector.class);
	private BaseClient baseClient = null;
	private List<ObjectName> listMetricObjectNames = new ArrayList<ObjectName>();
	private long lastExecutionTime = 0l;
	private long interval = 1000l;
	private List<ServiceMetric> lastListServiceMetrics = new ArrayList<ServiceMetric>();
	
	public CXFMetricsCollector(BaseClient baseClient) {
		if (baseClient == null || baseClient.isConnected() == false) {
			throw new IllegalArgumentException("baseClient cannot be null");
		} else if (baseClient.isConnected() == false) {
			throw new IllegalArgumentException("baseClient must be connected");
		}
		this.baseClient = baseClient;
	}
	
	public void setupCXFTotalsMetricObjectNames(String artifactPattern) throws Exception {
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
							listMetricObjectNames.add(oi.getObjectName());
						}
					} else {
						listMetricObjectNames.add(oi.getObjectName());
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(listMetricObjectNames.size() + " metric object names fetched.");
			for (ObjectName name : listMetricObjectNames) {
				logger.debug(name);
			}
		}
	}

	public List<ObjectName> getListMetricObjectNames() {
		return listMetricObjectNames;
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
	
	public ServiceMetric fetchServiceMetric(ObjectName objectName) throws Exception {
		String name = getArtifactIdFromObjectName(objectName);
		String op = objectName.getKeyProperty("Operation");
		if (op != null && op.trim().isEmpty() == false) {
			name = name + "." + op;
		}
		ServiceMetric metric = new ServiceMetric();
		metric.setServiceName(name);
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
			listServiceMetrics.add(fetchServiceMetric(on));
		}
		lastListServiceMetrics = listServiceMetrics;
		return listServiceMetrics;
	}
	
	public boolean next() {
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
