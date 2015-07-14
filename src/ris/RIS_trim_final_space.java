package ris;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class RIS_trim_final_space {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void trim_end_space(File ris_file, File modified_ris_file) throws IOException {
		
		//File ris_file = new File("C:/Users/zongpeng/Desktop/rissample_modified_UR/inu.32000007756036.ris");
		
		//File modified_ris_file = new File("C:/Users/zongpeng/Desktop/rissample_modified_UR_n_trimmedPunc/inu.32000007756036.ris");
	
		//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ris_file), "UTF-8"));
		//PrintStream ps = new PrintStream(new FileOutputStream(modified_ris_file), false, "UTF-8");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ris_file)));
		PrintWriter ps = new PrintWriter(new FileOutputStream(modified_ris_file));
			
		String line = null;
		
		while((line = br.readLine()) != null)
		{
			if(line.endsWith(" ") && !line.startsWith("ER"))
			{
				//System.out.println(line.length());
				String temp_str = line.substring(0, line.length()-1);
				ps.print(temp_str);
				ps.print('\n');
				ps.flush();
			}else 
			{
				//System.out.println(line.length());
				ps.print(line);
				ps.print('\n');
				ps.flush();
			}
		}
		ps.flush();
		ps.close();
	
	
	}
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("start triming ...");
		
		File ris_dir = new File("X:/ris_file_no_end_punc");
		//File ris_dir = new File("C:\\Users\\zongpeng\\Desktop\\RISSamplev1.1");
		
		File[] ris_files = ris_dir.listFiles();
		
		for(int i = 0; i<ris_files.length; i++)
		{
			File new_ris_file = new File("X:/RIS_final/" + ris_files[i].getName());
			
			trim_end_space(ris_files[i], new_ris_file);
		}
		
		System.out.println("trim completed !!!");
	}

}
