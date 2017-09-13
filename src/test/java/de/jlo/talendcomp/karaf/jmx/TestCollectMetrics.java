package de.jlo.talendcomp.karaf.jmx;

import static org.junit.Assert.assertTrue;

import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class TestCollectMetrics {
	
	@Before
	public void setup() {
		BasicConfigurator.configure();
		Logger root = Logger.getRootLogger();
		root.setLevel(Level.DEBUG);
	}

	@Test
	public void testConnect() throws Exception {
		String host = "talendjobtest01.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		assertTrue(true);
		// check memory usage
		MemoryUsage meminfo = c.getMemoryInfo();
		System.out.println("max=" + meminfo.getMax() + " used=" + meminfo.getUsed());
	}

	@Test
	public void testQueryObjectNames() throws Exception {
		String host = "talendjobtest01.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		ObjectName pattern = new ObjectName("org.apache.cxf:type=Metrics.Server,*");
		System.out.println(pattern.isPropertyListPattern());
		Set<ObjectInstance> result = c.getmBeanServerConnection().queryMBeans(pattern /*new ObjectName("org.apache.cxf.bus.id=*")*/, null);
		for (ObjectInstance oi : result) {
			if ("com.codahale.metrics.JmxReporter$JmxTimer".equals(oi.getClassName())) {
				ObjectName on = oi.getObjectName();
				System.out.println(on.getKeyProperty("bus.id"));
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testSetupCXFMetricObjectNames() throws Exception {
		String host = "talendjobtest01.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		CXFMetricsCollector coll = new CXFMetricsCollector(c);
		coll.setupCXFTotalsMetricObjectNames("core_api|beat17");
		assertTrue(true);
	}

	@Test
	public void testListServiceMetrics() throws Exception {
		String host = "talendjobtest01.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		CXFMetricsCollector coll = new CXFMetricsCollector(c);
		coll.setInterval(5);
		coll.setupCXFTotalsMetricObjectNames("core_api|beat17");
		System.out.println("\n\n#########################");
		int count = 0;
		while (coll.next()) {
			List<ServiceMetric> metrics = coll.fetchServiceMetrics();
			for (ServiceMetric m : metrics) {
				System.out.println(m);
			}
			if (++count == 100) {
				break;
			}
			System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
			System.out.println("#########################");
		}
		assertTrue(true);
	}

	@Test
	public void testListSystemMetrics() throws Exception {
		String host = "talendjob01.gvl.local";
		int jmxPort = 44444;
		int jstatdPort = 1099;
		String karafInstance = "trun";
		String user = "karaf";
		String passwd = "karaf";
		KarafClient c = new KarafClient();
		c.setJmxUser(user);
		c.setJmxPassword(passwd);
		c.setKarafRemoteJmxUrl(host, jmxPort, karafInstance, jstatdPort);
		c.connect();
		JVMMetricsCollector coll = new JVMMetricsCollector(c);
		System.out.println("\n\n#########################");
		for (int i = 0; i < 1000; i++) {
			SystemMetrics metrics = coll.fetchSystemMetrics();
			System.out.println(metrics);
			Thread.sleep(1000);
		}
		assertTrue(true);
	}
}
