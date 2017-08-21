package de.jlo.karaf.jmx;

import javax.management.openmbean.CompositeData;

public class ServiceFeature {
	
	private String featureName;
	private String version;
	private boolean isInstalled = false;
	private String bundles = null;
	
	private ServiceFeature() {}
	
	public static ServiceFeature from(CompositeData cd) {
		ServiceFeature f = new ServiceFeature();
		f.featureName = (String) cd.get("Name");
		f.version = (String) cd.get("Version");
		f.isInstalled = (Boolean) cd.get("Installed");
		f.bundles = getStringArrayAsString((String[]) cd.get("Bundles"));
		return f;
	}

	public String getFeatureName() {
		return featureName;
	}

	public String getVersion() {
		return version;
	}

	public boolean isInstalled() {
		return isInstalled;
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

	public String getBundles() {
		return bundles;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=");
		sb.append(featureName);
		sb.append("\nversion=");
		sb.append(version);
		sb.append("\ninstalled=");
		sb.append(isInstalled);
		sb.append("\nbundles=");
		sb.append(bundles);
		return sb.toString();
	}

}
