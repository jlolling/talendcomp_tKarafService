package de.jlo.talendcomp.karaf.jmx;

import static org.junit.Assert.assertTrue;

import java.lang.management.MemoryUsage;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class TestInstallCheckServices {
	
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
		d.uninstallTalendService("de.gvl", "navi_service_uploaded_files");
		d.installTalendService("de.gvl", "navi_service_uploaded_files", "26.17.0");
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
		coll.setArtifactPattern("core_api|beat17");
		coll.setupCXFTotalsMetricObjectNames();
		assertTrue(true);
	}

}
