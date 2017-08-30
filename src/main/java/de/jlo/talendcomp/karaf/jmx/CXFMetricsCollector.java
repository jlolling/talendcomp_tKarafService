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
	private BaseClient jmxClient = null;
	private List<ObjectName> listMetricObjectNames = new ArrayList<ObjectName>();
	
	public CXFMetricsCollector(BaseClient jmxClient) {
		if (jmxClient == null || jmxClient.isConnected() == false) {
			throw new IllegalArgumentException("jmxClient cannot be null");
		} else if (jmxClient.isConnected() == false) {
			throw new IllegalArgumentException("jmxClient must be connected");
		}
		this.jmxClient = jmxClient;
	}
	
	public void setupCXFMetricObjectNames(String artifactPattern) throws Exception {
		logger.debug("Fetching metric object names and filter with: " + artifactPattern);
		Pattern p = null;
		if (artifactPattern != null && artifactPattern.trim().isEmpty() == false) {
			p = Pattern.compile(artifactPattern);
		}
		ObjectName onpattern = new ObjectName("org.apache.cxf:type=Metrics.Server,*");
		Set<ObjectInstance> result = jmxClient.getmBeanServerConnection().queryMBeans(onpattern, null);
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

}
