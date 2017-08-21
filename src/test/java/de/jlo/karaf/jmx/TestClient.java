package de.jlo.karaf.jmx;

import static org.junit.Assert.assertTrue;

import java.lang.management.MemoryUsage;
import java.util.List;
import java.util.Set;

import javax.management.ObjectInstance;

import org.junit.Test;

public class TestClient {

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
			System.out.println(f + "\n");
		}
	}

	@Test
	public void testInstallFeature() throws Exception {
		String host = "talendjob02.gvl.local";
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
		System.out.println("List initial features...");
		List<ServiceFeature> list = d.fetchFeatures("_service_", true);
		for (ServiceFeature f : list) {
			System.out.println(f + "\n");
		}
		System.out.println("Add repo...");
		d.addFeatureRepo("de.gvl","navi_service_uploaded_files-feature", "26.17.0");
		System.out.println("Uninstall feature...");
		d.uninstallFeature("navi_service_uploaded_files-feature");
		System.out.println("Install feature...");
		d.installFeature("navi_service_uploaded_files-feature", "26.17.0");
		System.out.println("List resulting features...");
		list = d.fetchFeatures("_service_", true);
		for (ServiceFeature f : list) {
			System.out.println(f + "\n");
		}
	}

	@Test
	public void testInstallTalendService() throws Exception {
		String host = "talendjob02.gvl.local";
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
		Set<ObjectInstance> result = c.getmBeanServerConnection().queryMBeans(null /*new ObjectName("org.apache.cxf.bus.id=*")*/, null);
		for (ObjectInstance n : result) {
			System.out.println(n.toString());
		}
		
	}
	
}
