package com.ctsi.springboot.a.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.xml.sax.InputSource;


/**
 * 
 * @author sevenzero
 *
 * @since 2012-6-5
 *
 */
public class Http {
	
	static Logger log = Logger.getLogger(Http.class.getSimpleName());
	
	private static final int RECYCLE_COUNT = 3;
	private static final long SLEEP_TIME = 1000L;
	
	public static final int TIME_OUT = 60000;
	private static final String REQUEST_METHOD = "POST";
	private static final String ENCODING = "UTF-8";
	
//	private static final String FOXIT_SERVICE_URL = "http://service.foxitcloud.com";
	
	
	/**
	 * Http POST 请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static  String getStrContentByPost(String url, String param) {
		if (Util.checkEmptyOrNull(url) || null == param) {
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStreamByPost(url, param);
			if (null == input) {
				return null;
			}
			
			br = new BufferedReader(new InputStreamReader(input));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * Http POST 请求
	 * 
	 * @param url
	 * @param headerRequestProperty
	 * @param formProperty
	 * @param x
	 * @return
	 */
	public static  String getStrContentByPost(String url, Map<String, String> headerRequestProperty, 
			Map<String, String> formProperty) {
		if (Util.checkEmptyOrNull(url) || null == formProperty) {
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStreamByPost(url, headerRequestProperty, formProperty);
			if (null == input) {
				return null;
			}
			
			br = new BufferedReader(new InputStreamReader(input));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param url
	 * @param headerRequestProperty
	 * @param xml
	 * @return
	 */
	public static  String getStrContentByPost(String url, Map<String, String> headerRequestProperty, 
			String xml) {
		if ( Util.checkEmptyOrNull(url) || null == xml ) {
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStreamByPost(url, headerRequestProperty, xml);
			if (null == input) {
				return null;
			}
			
			br = new BufferedReader(new InputStreamReader(input));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * Http POST 请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static InputStream getInputStreamByPost(String url, String param) {
		if (Util.checkEmptyOrNull(url) || null == param) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(param);
		log.info("parme = " + sb.toString());
		
		String message = sb.toString();
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			
			urlConn.addRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded; charset=" + ENCODING);
			urlConn.addRequestProperty("Content-Length", String.valueOf(message.length()));
			
			OutputStream output = urlConn.getOutputStream();
			output.write(message.getBytes());
			output.flush();
			output.close();
			
			return urlConn.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Http POST 请求
	 * 
	 * @param url
	 * @param headerRequestProperty
	 * @param formProperty
	 * @return
	 */
	public static InputStream getInputStreamByPost(String url, Map<String, String> headerRequestProperty, 
			Map<String, String> formProperty) {
		if (Util.checkEmptyOrNull(url) || null == formProperty) {
			return null;
		}
		
		// 表单属性
		StringBuffer sb = new StringBuffer();
		if (formProperty.size() > 0) {
			Set<String> set = formProperty.keySet();
			Iterator <String> it = set.iterator();
			while(it.hasNext()) {
				String key = it.next();
				String value = formProperty.get(key);
				sb.append(key).append("=").append(value).append("&");
			}
			
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		log.info("param = " + sb.toString());
		
		String message = sb.toString();
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		OutputStream output = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			
			// 判断是否需要添加额外的请求头信息
			if (null != headerRequestProperty && headerRequestProperty.size() > 0) {
				Set<String> set = headerRequestProperty.keySet();
				Iterator <String> it = set.iterator();
				while(it.hasNext()) {
					String key = it.next();
					String value = headerRequestProperty.get(key);
					urlConn.addRequestProperty(key, value);
				}
			}
			urlConn.addRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded; charset=" + ENCODING);
			urlConn.addRequestProperty("Content-Length", String.valueOf(message.length()));
			
			output = urlConn.getOutputStream();
			output.write(message.getBytes());
			output.flush();
			
			return urlConn.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (null != output) {
				try {
					output.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param headerRequestProperty
	 * @param xml
	 * @return
	 */
	public static InputStream getInputStreamByPost(String url, Map<String, String> headerRequestProperty,
			String xml) {
		if (Util.checkEmptyOrNull(url) || null == xml) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
//		String xml = "<?xml version='1.1' encoding='UTF-8'?><maven2-moduleset plugin=\"maven-plugin@3.1.2\">  <keepDependencies>false</keepDependencies>  <properties/>  <scm class=\"hudson.scm.NullSCM\"/>  <canRoam>false</canRoam>  <disabled>false</disabled>  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>  <triggers/>  <concurrentBuild>false</concurrentBuild>  <aggregatorStyleBuild>true</aggregatorStyleBuild>  <incrementalBuild>false</incrementalBuild>  <ignoreUpstremChanges>false</ignoreUpstremChanges>  <ignoreUnsuccessfulUpstreams>false</ignoreUnsuccessfulUpstreams>  <archivingDisabled>false</archivingDisabled>  <siteArchivingDisabled>false</siteArchivingDisabled>  <fingerprintingDisabled>false</fingerprintingDisabled>  <resolveDependencies>false</resolveDependencies>  <processPlugins>false</processPlugins>  <mavenValidationLevel>-1</mavenValidationLevel>  <runHeadless>false</runHeadless>  <disableTriggerDownstreamProjects>false</disableTriggerDownstreamProjects>  <settings class=\"jenkins.mvn.DefaultSettingsProvider\"/>  <globalSettings class=\"jenkins.mvn.DefaultGlobalSettingsProvider\"/>  <reporters/>  <publishers/>  <buildWrappers/>  <prebuilders/>  <postbuilders/></maven2-moduleset>";
//		sb.append(xml);
		sb.append(xml);
		log.info("param = " + sb.toString());
		
		String message = sb.toString();
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		OutputStream output = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			
			// 判断是否需要添加额外的请求头信息
			if (null != headerRequestProperty && headerRequestProperty.size() > 0) {
				Set<String> set = headerRequestProperty.keySet();
				Iterator <String> it = set.iterator();
				while(it.hasNext()) {
					String key = it.next();
					String value = headerRequestProperty.get(key);
					urlConn.addRequestProperty(key, value);
				}
			}
//			urlConn.addRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
//			urlConn.addRequestProperty("Content-Type", 
//					"application/x-www-form-urlencoded; charset=" + ENCODING);
			urlConn.addRequestProperty("Content-Length", String.valueOf(message.length()));
			
			output = urlConn.getOutputStream();
			output.write(message.getBytes());
			output.flush();
			
			return urlConn.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (null != output) {
				try {
					output.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param headerRequestProperty
	 */
	public static void sendByPost(String url, Map<String, String> headerRequestProperty) {
		if (Util.checkEmptyOrNull(url)) {
			return;
		}
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(false);
			urlConn.setUseCaches(false);
			
			// 判断是否需要添加额外的请求头信息
			if (null != headerRequestProperty && headerRequestProperty.size() > 0) {
				Set<String> set = headerRequestProperty.keySet();
				Iterator <String> it = set.iterator();
				while(it.hasNext()) {
					String key = it.next();
					String value = headerRequestProperty.get(key);
					urlConn.addRequestProperty(key, value);
				}
			}
			
			int code = urlConn.getResponseCode();
			log.info("ResponseCode ==> " + code);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param headerRequestProperty
	 * @return
	 */
	public static HttpURLConnection sendByGet(String url, Map<String, String> headerRequestProperty) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Http url is null.");
			return null;
		}
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			
			// 判断是否需要添加额外的请求头信息
			if (null != headerRequestProperty && headerRequestProperty.size() > 0) {
				Set<String> set = headerRequestProperty.keySet();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = headerRequestProperty.get(key);
					urlConn.addRequestProperty(key, value);
				}
			}
			
//			long start = System.currentTimeMillis();
			urlConn.getInputStream();
//			long end = System.currentTimeMillis();
//			log.info("Http costs " + (end - start) + "ms");

			return urlConn;
		}
		catch (IOException e) {
			log.info("Http exception.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	static InputStream getInputStream(String url, String param) {
		if (Util.checkEmptyOrNull(url) || Util.checkEmptyOrNull(param)) {
			return null;
		}
		
		String message = "?key=" + param + "&site=";
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			urlConn.setConnectTimeout(TIME_OUT);
			urlConn.setReadTimeout(TIME_OUT);
			urlConn.setRequestMethod(REQUEST_METHOD);
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			
			urlConn.addRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded; charset=" + ENCODING);
			urlConn.addRequestProperty("Content-Length", String.valueOf(message.length()));
			
			OutputStream output = urlConn.getOutputStream();
			output.write(message.getBytes());
			output.flush();
			output.close();
			
			return urlConn.getInputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static String getStrContent(String url, String param) {
		
		if (Util.checkEmptyOrNull(url) || Util.checkEmptyOrNull(param)) {
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStream(url, param);
			if (null == input) {
				return null;
			}
			
			br = new BufferedReader(new InputStreamReader(input));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Get stream object from url （HTTP GET METHOD）
	 * @param url
	 * @return InputStream
	 */
	public static InputStream getInputStreamByGet(String url, Map<String, String> headerRequestProperty) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Http url is null.");
			return null;
		}
		
		URL httpUrl = null;
		HttpURLConnection urlConn = null;
		try {
			httpUrl = new URL(url);
			urlConn = (HttpURLConnection) httpUrl.openConnection();
			
			// 判断是否需要添加额外的请求头信息
			if (null != headerRequestProperty && headerRequestProperty.size() > 0) {
				Set<String> set = headerRequestProperty.keySet();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = headerRequestProperty.get(key);
					urlConn.addRequestProperty(key, value);
				}
			}
//			urlConn.addRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
			
//			long start = System.currentTimeMillis();
			InputStream input = urlConn.getInputStream();
//			long end = System.currentTimeMillis();
//			log.info("Http costs " + (end - start) + "ms");

			return input;
		}
		catch (IOException e) {
			log.info("Http exception.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static InputStream getInputStreamByGet(String url) {
		return getInputStreamByGet(url, null);
	}
	
	public static String getStrContent(InputStream is) {
		BufferedReader br = null;
		try {
			if (null == is) {
				return null;
			}
			
			br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * Get content from url （HTTP GET METHOD）
	 * @param url
	 * @param headerRequestProperty
	 * @return
	 */
	public static String getStrContentByGet(String url, Map<String, String> headerRequestProperty) {
		if (Util.checkEmptyOrNull(url)) {
			log.info("Url is null");
			return null;
		}
		
		BufferedReader br = null;
		try {
			InputStream input = getInputStreamByGet(url, headerRequestProperty);
			if (null == input) {
				return null;
			}
			
			br = new BufferedReader(new InputStreamReader(input));
			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (null != br) {
				try {
					br.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String getStrContentByGet(String url) {
		return getStrContentByGet(url, null);
	}
	
	/**
	 * Get InputSource from url 
	 * 
	 * @param url
	 * @return
	 */
	public static InputSource getInputSource(String url) {
		if (Util.checkEmptyOrNull(url)) {
			return null;
		}
		
		InputStream input = getInputStreamByGet(url);
		if (null == input) {
			int n = 0;
			do {
				try {
					Thread.sleep(SLEEP_TIME);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
					log.info("Thread-Exception.");
				}
				
				input = getInputStreamByGet(url);
				
			} while (null == input && n++ < RECYCLE_COUNT);
			
			if (null == input) {
				return null;
			}
			log.info("NET n = " + n);
		}
		
		return new InputSource(input);
	}
	
	public static String  encode(String value) {
		if (null != value) {
			value = value.replace("+", "%2B");
			value = value.replace("&", "%26");
			value = value.replace("#", "%23");
			value = value.replace(" ", "%20");
			
			return value;
		}
		
		return null;
	}
	
	public static void writeToFile(InputStream is, String fileName) {
		if (null == is) {
			return;
		}
		
		try (
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
				) {
			
//			StringBuffer sb = new StringBuffer(2048);
			String line = null;
			
			while (null != br && null != (line = br.readLine())) {
//				sb.append(line).append("\n");
				bw.append(line);
			}
			
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		
	}
	
	
}
