package de.jlo.talendcomp.karaf.jmx;

public class KarafClient extends BaseClient {

	private String karafInstanceName = null;
	
	/**
	 * Set the typically Karaf remote URL by parameters
	 * @param host
	 * @param jmxRemotePort 
	 * @param karafInstanceName 
	 * @param jstatdPort typical value is 1099
	 */
	public void setKarafRemoteJmxUrl(String host, int jmxRemotePort, String karafInstanceName, int jstatdPort) {
		this.karafInstanceName = karafInstanceName;
		setJmxServiceUrl("service:jmx:rmi://" + host + ":" + jmxRemotePort + "/jndi/rmi://" + host + ":" + jstatdPort + "/karaf-" + karafInstanceName);
	}

	public String getKarafInstanceName() {
		return karafInstanceName;
	}

	public void setKarafInstanceName(String karafInstanceName) {
		this.karafInstanceName = karafInstanceName;
	}
	
}
