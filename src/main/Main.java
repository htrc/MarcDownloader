package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import ris.RISGenerator;
import util.PropertyManager;
import util.utility;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	public static String TEMPERARY_DIRECTORY = "tmpDir";

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream("collection.properties"));
		String inputPath = properties.getProperty("collectionLocation");
		String solrProxy = properties.getProperty("SolrProxy");

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputPath))));
		MarcDownloader downloader = new MarcDownloader();
		String id = null;
		int count = 0;
		while ((id = br.readLine()) != null) {
			downloader.add(id);
			count++;
			if (count%100 == 0) {
				marcDownloadRisConvert(downloader, solrProxy, "marc_" + (count - 99) + "TO" + (count));
				downloader.clearList();
				Thread.sleep(10000);
			}
		}
		br.close();

		if(downloader.getIdNumber() != 0) {
			marcDownloadRisConvert(downloader, solrProxy, "marc_" + (count / 100) * 100 + "TO" + count);
		}
		String OutputDirPath = PropertyManager.getProperty("outputDir");
		String resultName = PropertyManager.getProperty("outputFile");
		utility.zipDir(resultName, TEMPERARY_DIRECTORY);
		//
		utility.delete(new File(TEMPERARY_DIRECTORY));

		File final_result = new File(OutputDirPath, resultName);

		if (final_result.exists()) {
			final_result.delete();
		}
		FileUtils.moveFile(new File(resultName), final_result);
	}
	
	public static void marcDownloadRisConvert(MarcDownloader downloader, String solrProxy, String filenameXtoY) throws IOException, DocumentException {
		String marcsLocation = downloader.download(solrProxy, filenameXtoY);
		System.out.println("filenameXtoY" + filenameXtoY);
		logger.info("filenameXtoY" + filenameXtoY);
		System.out.println("marcs_location " + marcsLocation);
		logger.info("marcs_location " + marcsLocation);
		String unzippedPath = utility.unzip(marcsLocation);
		System.out.println("unzipped_path " + unzippedPath);
		logger.info("unzipped_path " + unzippedPath);
		// generate RIS from MARC on the fly
		
		RISGenerator risGenerator = new RISGenerator();
		risGenerator.add(new EndPunctuationRISFilter());
		risGenerator.add(new FinalSpaceRISFilter());
		String ris_file_name_NO_ext = filenameXtoY.replace("marc", "ris");
	
		logger.info(ris_file_name_NO_ext);
		System.out.println(ris_file_name_NO_ext);
		risGenerator.process(unzippedPath, ris_file_name_NO_ext);
		
		//utility.delete(new File(unzipped_path));
		utility.delete(new File(marcsLocation));
		
		File marc = new File(filenameXtoY);
		System.out.println(marc.getAbsolutePath());
		File ris = new File(ris_file_name_NO_ext);
		//String OutputDir_path = PropertyManager.getProperty("outputDir");
		File outputDir = new File(TEMPERARY_DIRECTORY);
		System.out.println("outputDir " + outputDir.getAbsolutePath());
		if(!ris.exists() || !marc.exists() || ris==null || marc==null) {
			return;
		}
		if(!outputDir.exists()){outputDir.mkdirs();}
		FileUtils.moveDirectory(marc, new File(outputDir, marc.getName()));
		FileUtils.moveDirectory(ris, new File(outputDir, ris.getName()));
	}
}
