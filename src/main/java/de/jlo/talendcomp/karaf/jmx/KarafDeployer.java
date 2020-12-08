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
		List<ServiceFeature> list = new ArrayList<ServiceFeature>();
		String objectName = "org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName();
		Collection<CompositeData> result = jmxClient.getAttributeValues(objectName, "Features");
		if (result != null) {
			Pattern pattern = Pattern.compile(filter);
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
		} else {
			throw new Exception("fetchFeatures with filter: " + filter + " failed: In object: " + objectName + " the attribute Features is null!");
		}
		return list;
	}
	
	public void installTalendService(String groupId, String artifactId, String version) throws Exception {
		logger.info("Install Talend Service: groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version);
		String featureName = artifactId + "-feature";
		logger.info("Remove repo...");
		removeFeatureRepo(groupId, featureName);
		logger.info("Add the repo...");
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
	
	public void uninstallTalendService(String groupId, String artifactId) throws Exception {
		logger.info("Uninstall Talend Service: artifactId=" + artifactId);
		String featureName = artifactId + "-feature";
		logger.info("Uninstall features...");
		uninstallFeature(featureName);
		logger.info("Remove repo...");
		removeFeatureRepo(groupId, featureName);
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
	
	public void removeFeatureRepo(String groupId, String featureName) {
		String fullQualifiedFeature = "mvn:" + groupId + "/" + featureName;
		String[] opParams = {
				fullQualifiedFeature
		};
		String[] opSig = {
				String.class.getName()
        };
		try {
			jmxClient.getmBeanServerConnection().invoke(new ObjectName("org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName()), "removeRepository", opParams, opSig);
		} catch (Exception e) {
			// ignore
		}
	}

	public void addFeatureRepo(String groupId, String featureName, String version) throws Exception {
		String fullQualifiedFeatureWithVersion = "mvn:" + groupId + "/" + featureName + "/" + version + "/xml";
		String[] opParams = {
				fullQualifiedFeatureWithVersion
		};
		String[] opSig = {
				String.class.getName()
        };
		try {
			jmxClient.getmBeanServerConnection().invoke(new ObjectName("org.apache.karaf:type=feature,name=" + jmxClient.getKarafInstanceName()), "addRepository", opParams, opSig);
		} catch (Exception e) {
			if (e.getMessage().contains("ArtifactResolutionException")) {
				throw new Exception("Adding feature-repository failed: Container cannot resolve feature with groupId: " + groupId + " artifactId: " + featureName + " version: " + version + ". Please check the container logs!");
			}
		}
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
				throw new Exception("uninstallFeature: " + featureName + "  failed: " + rme.getMessage(), rme);
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

}
