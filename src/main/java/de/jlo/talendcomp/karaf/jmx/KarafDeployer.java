package de.jlo.talendcomp.karaf.jmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

public class KarafDeployer {
	
	private KarafClient jmxClient = null;
	private static Logger logger = Logger.getLogger(KarafDeployer.class.getName());
	private HttpClient httpClient = null;
	private String nexusGetFeatureXmlTemplate = "http://localhost:8081/nexus/content/repositories/{repository}/{groupId}/{artifactId}-feature/{version}/{artifactId}-feature-{version}.xml";
	
	public KarafDeployer(KarafClient jmxClient) {
		if (jmxClient == null) {
			throw new IllegalArgumentException("jmxClient cannot be null");
		}
		if (jmxClient.isConnected() == false) {
			throw new IllegalArgumentException("jmxClient is not connected");
		}
		this.jmxClient = jmxClient;
		System.setSecurityManager(new NOSecurityManager());
	}
	
	public List<ServiceFeature> fetchFeatures(String filter, boolean onlyInstalled) throws Exception {
		Pattern pattern = Pattern.compile(filter);
		List<ServiceFeature> list = new ArrayList<ServiceFeature>();
		Collection<CompositeData> result = jmxClient.getAttributeValues("org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName(), "Features");
		for (CompositeData cd : result) {
			String name = (String) cd.get("Name");
			Matcher matcher = pattern.matcher(name);
			if (matcher.find()) {
				ServiceFeature f = ServiceFeature.from(cd);
				if (onlyInstalled == false || f.isInstalled()) {
					list.add(ServiceFeature.from(cd));
				}
			}
		}
		return list;
	}
	
	private HttpClient getHttpClient() throws Exception {
		if (httpClient == null) {
			httpClient = HttpClient.init(null, null, null);
		}
		return httpClient;
	}
	
	public boolean checkArtifactInNexus(String groupId, String artifactId, String version) throws Exception {
		if (nexusGetFeatureXmlTemplate == null) {
			throw new IllegalStateException("Nexus feature get template url not set!");
		}
		String groupStr = groupId.replace('.', '/');
		String url = nexusGetFeatureXmlTemplate.replace("{artifactId}", artifactId).replace("{version}", version).replace("{groupId}", groupStr);
		HttpClient getter = getHttpClient();
		getter.get(url);
		if (getter.getStatusCode() == 200) {
			return true;
		}
		return false;
	}
	
	public void installTalendService(String groupId, String artifactId, String version) throws Exception {
		logger.info("Install Talend Service: groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version);
		logger.info("Add the repo...");
		String featureName = artifactId + "-feature";
		addFeatureRepo(groupId, featureName, version);
		logger.info("Uninstall previous features...");
		uninstallFeature(featureName);
		logger.info("Install feature...");
		installFeature(featureName, version);
		logger.info("Done");
		logger.info("Check feature list...");
		List<ServiceFeature> list = fetchFeatures(featureName, true);
		if (list.isEmpty()) {
			throw new Exception("Installation seems to be ok but we cannot find the feature in the list of the installed features!");
		}
	}
	
	public void uninstallTalendService(String artifactId) throws Exception {
		logger.info("Uninstall Talend Service: artifactId=" + artifactId);
		String featureName = artifactId + "-feature";
		logger.info("Uninstall features...");
		uninstallFeature(featureName);
		logger.info("Done");
		logger.info("Check feature list...");
		List<ServiceFeature> list = fetchFeatures(featureName, true);
		if (list.isEmpty() == false) {
			throw new Exception("Uninstallation seems to be ok but we still find the feature in the list of the installed features!");
		}
	}

	public void installFeature(String featureName, String version) throws Exception {
		String[] opParams = {
				featureName,
				version
		};
		String[] opSig = {
				String.class.getName(),
                String.class.getName()
        };
		jmxClient.getmBeanServerConnection().invoke(new ObjectName("org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName()), "installFeature", opParams, opSig);
	}
	
	public void addFeatureRepo(String groupId, String featureName, String version) throws Exception {
		String[] opParams = {
				"mvn:" + groupId + "/" + featureName + "/" + version + "/xml"
		};
		String[] opSig = {
				String.class.getName()
        };
		jmxClient.getmBeanServerConnection().invoke(new ObjectName("org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName()), "addRepository", opParams, opSig);
	}

	public void uninstallFeature(String featureName) throws Exception {
		String[] opParams = {
				featureName
		};
		String[] opSig = {
				String.class.getName()
        };
		try {
			jmxClient.getmBeanServerConnection().invoke(new ObjectName("org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName()), "uninstallFeature", opParams, opSig);
		} catch (javax.management.RuntimeMBeanException rme) {
			String message = rme.getMessage();
			if (message.contains("not installed")) {
				// ignore message
			} else {
				throw rme;
			}
		}
	}

	public static String getStringArrayAsString(String[] array) {
		if (array == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				sb.append("\n");
			}
			sb.append(array[i]);
		}
		return sb.toString();
	}

	public String getNexusGetFeatureXmlTemplate() {
		return nexusGetFeatureXmlTemplate;
	}

	public void setNexusGetFeatureXmlTemplate(String nexusGetFeatureXmlTemplate) {
		this.nexusGetFeatureXmlTemplate = nexusGetFeatureXmlTemplate;
	}

}
