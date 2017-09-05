package de.jlo.talendcomp.karaf.jmx;

import static org.junit.Assert.assertTrue;

import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Set;

import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class TestClient {
	
	@Before
	public void setup() {
		BasicConfigurator.configure();
		Logger root = Logger.getRootLogger();
		root.setLevel(Level.INFO);
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
	public void testListFeatures() throws Exception {
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
		KarafDeployer d = new KarafDeployer(c);
		List<ServiceFeature> list = d.fetchFeatures("_service_", true);
		for (ServiceFeature f : list) {
			System.out.println(f.getArtifactId() + " - " + f.getVersion() + "\n");
		}
	}

	@Test
	public void testUnAndInstallFeature() throws Exception {
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
		KarafDeployer d = new KarafDeployer(c);
		System.out.println("Uninstall feature...");
		d.uninstallFeature("navi_service_uploaded_files-feature");
		System.out.println("Add repo...");
		d.addFeatureRepo("de.gvl","navi_service_uploaded_files-feature", "26.17.0");
		System.out.println("Install feature...");
		d.installFeature("navi_service_uploaded_files-feature", "26.17.0");
		System.out.println("List resulting features...");
		List<ServiceFeature> list = d.fetchFeatures("_service_", true);
		for (ServiceFeature f : list) {
			System.out.println(f.getArtifactId() + "\t" + f.getVersion() + "\t : " + f.getBundles());
		}
	}

	@Test
	public void testInstallTalendService() throws Exception {
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
		KarafDeployer d = new KarafDeployer(c);
		d.uninstallTalendService("navi_service_uploaded_files");
		d.installTalendService("de.gvl","navi_service_uploaded_files", "26.17.0");
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
		coll.setupCXFTotalsMetricObjectNames("core_api|beat17");
		System.out.println("\n\n#########################");
		List<ServiceMetric> metrics = coll.fetchServiceMetrics();
		for (ServiceMetric m : metrics) {
			System.out.println(m);
		}
		assertTrue(true);
	}

}
