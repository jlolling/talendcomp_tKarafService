package de.jlo.karaf.jmx;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * JMX client
 * @author jan.lolling@gmail.com
 */
public class BaseClient {

	private String jmxServiceUrl = null;
	private String jmxUser = null;
	private String jmxPassword = null;
	private MBeanServerConnection mBeanServerConnection = null;

	public String getJmxServiceUrl() {
		return jmxServiceUrl;
	}
	
	public void setJmxServiceUrl(String jmxUrl) {
		this.jmxServiceUrl = jmxUrl;
	}
	
	public String getJmxUser() {
		return jmxUser;
	}
	
	public void setJmxUser(String jmxUser) {
		this.jmxUser = jmxUser;
	}
	
	public String getJmxPassword() {
		return jmxPassword;
	}
	
	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}
		
	public static boolean isEmpty(String s) {
		if (s == null) {
			return true;
		}
		if (s.trim().isEmpty()) {
			return true;
		}
		if (s.trim().equalsIgnoreCase("null")) {
			return true;
		}
		return false;
	}

	public MBeanServerConnection getmBeanServerConnection() {
		return mBeanServerConnection;
	}
	
	public void connect() throws Exception {
		System.setProperty("sun.rmi.transport.connectionTimeout", "10000");
		mBeanServerConnection = null;
        Map<String, String[]> environment = new HashMap<String, String[]>();
        environment.put(JMXConnector.CREDENTIALS, new String[] { jmxUser, jmxPassword });
        JMXConnector jmxConnector = connectWithTimeout(new JMXServiceURL(jmxServiceUrl), environment, 10000l);
        mBeanServerConnection = jmxConnector.getMBeanServerConnection();
	}
	
	private void checkConnection() throws Exception {
		if (mBeanServerConnection == null) {
			throw new Exception("Not connected. MBeanServerConnection is null.");
		}
	}
	
	public boolean isConnected() {
		return mBeanServerConnection != null;
	}
	
	public CompositeData getAttributeValue(String objectName, String attribute) throws Exception {
		checkConnection();
		return (CompositeData) mBeanServerConnection.getAttribute(new ObjectName(objectName), attribute);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<CompositeData> getAttributeValues(String objectName, String attribute) throws Exception {
		checkConnection();
		TabularData tabularData = (TabularData) mBeanServerConnection.getAttribute(new ObjectName(objectName), attribute);
		return (Collection<CompositeData>) tabularData.values();
	}

	public MemoryUsage getMemoryInfo() throws Exception {
		checkConnection();
		CompositeData cd = getAttributeValue("java.lang:type=Memory", "HeapMemoryUsage");
		return MemoryUsage.from(cd);
	}
	
	/*
	 * provides a timeout setting for JMX remote connects
	 */
	private static JMXConnector connectWithTimeout(final JMXServiceURL url, final Map<String, String[]> environment, long timeout) throws Exception { 
		final BlockingQueue<Object> mailbox = new ArrayBlockingQueue<Object>(1); 
		ExecutorService executor = Executors.newSingleThreadExecutor(); 
		executor.submit(new Runnable() {
			public void run() {
				try {
					JMXConnector connector = JMXConnectorFactory.connect(url, environment);
					if (!mailbox.offer(connector)) {
						connector.close();
					}
				} catch (Throwable t) {
					mailbox.offer(t);
				}
			}
		}); 
		Object result = null; 
		try { 
			result = mailbox.poll(timeout, TimeUnit.MILLISECONDS); 
			if (result == null) { 
				if (!mailbox.offer("")) {
					result = mailbox.take(); 
				}
			} 
		} catch (InterruptedException e) {
			throw e; 
		} finally { 
			executor.shutdown(); 
		} 
		if (result == null) {
			throw new SocketTimeoutException("Connect timed out: " + url); 
		}
		if (result instanceof JMXConnector) {
			return (JMXConnector) result; 
		}
		try { 
			throw (Throwable) result; 
		} catch (IOException e) { 
			throw e; 
		} catch (RuntimeException e) { 
			throw e; 
		} catch (Error e) { 
			throw e; 
		} catch (Throwable e) { 
			// In principle this can't happen but we wrap it anyway 
			throw new IOException(e.toString(), e); 
		}
	}

}
