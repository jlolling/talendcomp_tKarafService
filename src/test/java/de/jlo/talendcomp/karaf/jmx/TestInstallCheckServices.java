package de.jlo.talendcomp.karaf.jmx;

import static org.junit.Assert.assertTrue;

import java.lang.management.MemoryUsage;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestInstallCheckServices {
	
	@Before
	public void printout() {
		System.out.println("java.version=" + System.getProperty("java.version"));
	}
	
	@Test
	public void testConnect() throws Exception {
		//String host = "talendjobtest01.gvl.local";
		String host = "localhost";
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
		//String host = "talendjobtest01.gvl.local";
		String host = "localhost";
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
		String featureFilter = ""; // "_service_";
		List<ServiceFeature> list = d.fetchFeatures(featureFilter, true);
		for (ServiceFeature f : list) {
			System.out.println(f.getArtifactId() + " - " + f.getVersion());
		}
	}

	@Test
	public void testUnAndInstallFeature() throws Exception {
		//String host = "talendjobtest01.gvl.local";
		String host = "localhost";
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
		String artifactId = "service_ping";
		String version = "0.4.0";
		String groupId = "de.gvl";
		KarafDeployer d = new KarafDeployer(c);
		System.out.println("Uninstall feature...");
		d.uninstallFeature(artifactId + "-feature");
		System.out.println("Add repo...");
		d.addFeatureRepo(groupId, artifactId + "-feature", version);
		System.out.println("Install feature...");
		d.installFeature(artifactId + "-feature", version);
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
