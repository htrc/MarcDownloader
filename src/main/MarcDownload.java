package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class MarcDownload {

	public static void main(String[] args) throws Exception {

		Properties properties = new Properties();

		properties.load(new FileInputStream("collection.properties"));

		String location = properties.getProperty("collectionLocation");

		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File(location))));
		MarcDownloader downloader = new MarcDownloader();
		String id = null;
		while ((id = br.readLine()) != null) {
			downloader.add(id);
		}
		br.close();
		downloader.download("http://chinkapin.pti.indiana.edu:9994/solr", "newmarc");
	}
}
