package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import util.utility;

public class EndPunctuationRISFilter implements MarcProcessor {

	@Override
	public void process(String dir) {
		//System.out.println("EndPunctionRisFilter filtering " + dir + "...");
		File ris_dir = new File(dir);
		String tmp_dir_path = "ris_tmp_EndPunctuationRISFilter";
		
		File tmp = new File(tmp_dir_path);
		if(!tmp.exists())
		{
			new File(tmp_dir_path).mkdir();
		}
		File[] ris_files = ris_dir.listFiles();
		if(ris_files == null) {
			return;
		}
		for(int i=0; i<ris_files.length; i++){
			
			// skip hidden file in ufs
			if(ris_files[i].getName().startsWith(".")){
				continue;
			}
			File new_ris_file = new File(tmp_dir_path + "/" +ris_files[i].getName());
			try {
				trim_end_punctuation(ris_files[i], new_ris_file);
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		
		try {
			utility.delete(ris_dir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	//	System.out.println("deletion in EndPunctuationRISfilter finished");
		
		new File(tmp_dir_path).renameTo(new File(dir));
	}

	public static void trim_end_punctuation(File ris_file, File modified_ris_file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ris_file)));
		PrintWriter ps = new PrintWriter(new FileOutputStream(modified_ris_file));

		String line = null;

		while ((line = br.readLine()) != null) {
			if (line.endsWith(",") || line.endsWith("/") || line.endsWith("\\") || line.endsWith(":") || line.endsWith(",")) {
				// System.out.println(line.length());
				String temp_str = line.substring(0, line.length() - 1);
				ps.print(temp_str);
				ps.print('\n');
				ps.flush();
			} else if (line.endsWith(".") && !line.startsWith("AU") && !line.startsWith("KW") && !line.endsWith("..")) {
				// System.out.println(line.length());
				String temp_str = line.substring(0, line.length() - 1);
				ps.print(temp_str);
				ps.print('\n');
				ps.flush();
			} else {
				// System.out.println(line.length());
				ps.print(line);
				ps.print('\n');
				ps.flush();
			}
		}
		ps.flush();
		ps.close();
		br.close();
	}
}
