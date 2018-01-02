package de.jlo.talendcomp.karaf.jmx;

import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {
	
	private int connectionTimeout = 1000;
	private int statusCode = 0;
	private String statusMessage = null;
	private CloseableHttpClient client = null;
	
	public static HttpClient init(String urlStr, String user, String password) throws Exception {
		HttpClient hc = new HttpClient();
		hc.client = hc.createClient(urlStr, user, password);
		return hc;
	}
	
	public String get(String urlStr, String user, String password) throws Exception {
        CloseableHttpClient httpclient = createClient(urlStr, user, password);
        try {
            HttpGet request = new HttpGet(urlStr);
            CloseableHttpResponse response = httpclient.execute(request);
            try {
            	statusCode = response.getStatusLine().getStatusCode();
            	statusMessage = response.getStatusLine().getReasonPhrase();
                return EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
	}
	
	public String get(CloseableHttpClient httpclient, String urlStr) throws Exception {
        try {
            HttpGet request = new HttpGet(urlStr);
            CloseableHttpResponse response = httpclient.execute(request);
            try {
            	statusCode = response.getStatusLine().getStatusCode();
            	statusMessage = response.getStatusLine().getReasonPhrase();
                return EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
	}
	
	public String get(String urlStr) throws Exception {
		if (client == null) {
			throw new IllegalStateException("Internal CloseableHttpClient is not intialized.");
		}
        try {
            HttpGet request = new HttpGet(urlStr);
            CloseableHttpResponse response = client.execute(request);
            try {
            	statusCode = response.getStatusLine().getStatusCode();
            	statusMessage = response.getStatusLine().getReasonPhrase();
                return EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
        	client.close();
        }
	}

	public HttpEntity buildEntityFromString(String content, String encoding) throws UnsupportedEncodingException {
		if (content != null && content.trim().isEmpty() == false) {
			HttpEntity entity = new StringEntity(content);
			return entity;
		} else {
			return null;
		}
	}
	
	public String post(String urlStr, String user, String password, HttpEntity entity) throws Exception {
        CloseableHttpClient httpclient = createClient(urlStr, user, password); 
        try {
            HttpPost request = new HttpPost(urlStr);
            request.getConfig();
            if (entity != null) {
                request.setEntity(entity);
            }
            CloseableHttpResponse response = httpclient.execute(request);
            try {
            	statusCode = response.getStatusLine().getStatusCode();
            	statusMessage = response.getStatusLine().getReasonPhrase();
                return EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
	}

	public String post(CloseableHttpClient httpclient, String urlStr, HttpEntity entity) throws Exception {
        HttpPost request = new HttpPost(urlStr);
        request.getConfig();
        if (entity != null) {
            request.setEntity(entity);
        }
        CloseableHttpResponse response = httpclient.execute(request);
        try {
        	statusCode = response.getStatusLine().getStatusCode();
        	statusMessage = response.getStatusLine().getReasonPhrase();
            return EntityUtils.toString(response.getEntity());
        } finally {
            response.close();
        }
	}

	public CloseableHttpClient createClient(String urlStr, String user, String password) throws Exception {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        if (user != null && user.trim().isEmpty() == false) {
    		URL url = new URL(urlStr);
            credsProvider.setCredentials(
                    new AuthScope(url.getHost(), url.getPort()),
                    new UsernamePasswordCredentials(user, password));
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(connectionTimeout)
                    .setConnectTimeout(connectionTimeout)
                    .setConnectionRequestTimeout(connectionTimeout)
                    .setRedirectsEnabled(true)
                    .setRelativeRedirectsAllowed(true)
                    .build();
            CloseableHttpClient client = HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            return client;
        } else {
            return HttpClients.custom()
                    .build();
        }
	}

	public int getTimeout() {
		return connectionTimeout;
	}

	public void setTimeout(int timeout) {
		this.connectionTimeout = timeout;
	}

	public void setTimeoutInSec(int timeout) {
		this.connectionTimeout = (timeout * 1000);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

}
