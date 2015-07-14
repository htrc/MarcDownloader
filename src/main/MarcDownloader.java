package main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

class MarcDownloader {
	private static final Logger logger = Logger.getLogger(MarcDownloader.class.getName());
	List<String> idList = new LinkedList<String>();

	public int getIdNumber() {
		return idList.size();
	}
	public void add(String id) {
		idList.add(id);
	}
	public void clearList(){
		idList = new LinkedList<String>();
	}

	public String download(String solrProxy, String filename) throws IOException {
		String restCall = getRestCall(solrProxy);
		return httpDownload(restCall, filename+".zip");
	}

	private String getRestCall(String solrProxy) {
		//String url = "http://chinkapin.pti.indiana.edu:9994/solr/MARC/?volumeIDs=";
		StringBuilder sb = new StringBuilder();
		sb.append(solrProxy).append("/MARC/?volumeIDs=");
		for (int i = 0; i < idList.size(); i++) {
			String id = idList.get(i);
			//id = utility.escape(id);
			if (i == 0) {
				sb.append(id);
			} else {
				sb.append("|").append(id);
			}
		}
		return sb.toString();
	}

	public static String httpDownload(String urlStr, String filename) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//System.out.println(urlStr);
		int responseCode = conn.getResponseCode();
		if ( responseCode != 200) {
			logger.error("connection returns response code: " + responseCode);
			throw new IOException(conn.getResponseMessage());
		}

		byte[] buf = new byte[8192];

		BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

		File downloadFile = new File(filename);
		FileOutputStream fos = new FileOutputStream(downloadFile);
		int size = 0;
		logger.info(urlStr + "downloading...");
		System.out.println(urlStr + "downloading...");
		while ((size = bis.read(buf)) != -1) {
			fos.write(buf, 0, size);
			fos.flush();
		}
		fos.flush();
		fos.close();
		bis.close();
		conn.disconnect();
		return downloadFile.getAbsolutePath();
	}
}
