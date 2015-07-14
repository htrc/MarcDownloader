package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class utility {
	public static int depth = 0;
	public static String escape(String uncleanID) { // escape ":" because the query
		// follows pattern
		// "field: term". Extra ":" in
		// term part can cause parsing
		// problem. So escape it by
		// adding "\"

		if (uncleanID.contains(":")) {
			return uncleanID.replace(":", "\\:");
		}

		return uncleanID;
	}

	public static void delete(File file) throws IOException {

		if (file == null || !file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
			System.out.println("file " + file.getAbsolutePath() + " is removed !!");
			return;
		}

		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				delete(files[i]);
			} else {
				files[i].delete();
			}
		}

		file.delete();
		return;

		// file.deleteOnExit();
		/*
		 * depth ++; System.out.println("deleting " + file.getAbsolutePath());
		 * System.out.println("depth : " + depth);
		 * 
		 * if(!file.exists()){ return; }
		 * 
		 * if (file.isDirectory()) {
		 * 
		 * // directory is empty, then delete it if (file.list().length == 0) {
		 * 
		 * file.delete(); // System.out.println("Directory is deleted : " // +
		 * file.getAbsolutePath());
		 * 
		 * } else {
		 * 
		 * // list all the directory contents File files[] =
		 * file.listFiles();//list(); System.out.println("there are " +
		 * files.length + "files/dirs in here!"); for (File temp : files) { //
		 * construct the file structure //File fileDelete = new File(file,
		 * temp);
		 * 
		 * // recursive delete delete(temp); }
		 * 
		 * // check the directory again, if empty then delete it if
		 * (file.list().length == 0) { file.delete(); //
		 * System.out.println("Directory is deleted : " // +
		 * file.getAbsolutePath()); } else delete(file); }
		 * 
		 * } else { // if file, then delete it file.delete(); //
		 * System.out.println("File is deleted : " + // file.getAbsolutePath());
		 * }
		 */
	}

	public static String zipDir(File dir, String zipName_no_ext) throws FileNotFoundException {
		int BUFFER = 2048;
		String outputname = zipName_no_ext + ".zip";
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(outputname);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory

			File files[] = dir.listFiles();

			for (int i = 0; i < files.length; i++) {
				// System.out.println("Adding: "+files[i]);
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(files[i].getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				fi.close();
				origin.close();
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputname;
	}

	static public String unzip(String zipFile) throws ZipException, IOException {
		//System.out.println(zipFile);
		int size = 2048;
		ZipFile zip = new ZipFile(new File(zipFile));
		String newPath = zipFile.substring(0, zipFile.length() - 4);

		new File(newPath).mkdir();
		Enumeration zipFileEntries = zip.entries();

		// Process each entry
		while (zipFileEntries.hasMoreElements()) {
			// grab a zip file entry
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(newPath, currentEntry);
			// destFile = new File(newPath, destFile.getName());
			File destinationParent = destFile.getParentFile();

			// create the parent directory structure if needed
			destinationParent.mkdirs();

			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip
						.getInputStream(entry));
				int currentByte;
				// establish buffer for writing file
				byte data[] = new byte[size];

				// write the current file to disk
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos,
						size);

				// read and write until last byte is encountered
				while ((currentByte = is.read(data, 0, size)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}

			if (currentEntry.endsWith(".zip")) {
				// found a zip file, try to open
				unzip(destFile.getAbsolutePath());
			}
		}
		return newPath;
	}
	
	public static void zipDir(String zipFileName, String dir) throws Exception {
	    File dirObj = new File(dir);
	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
	    System.out.println("Creating : " + zipFileName);
	    addDir(dirObj, out);
	    out.close();
	  }

	  static void addDir(File dirObj, ZipOutputStream out) throws IOException {
	    File[] files = dirObj.listFiles();
	    byte[] tmpBuf = new byte[1024];
	    if(files ==null) return;
	    for (int i = 0; i < files.length; i++) {
	      if (files[i].isDirectory()) {
	        addDir(files[i], out);
	        continue;
	      }
	      FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
	      System.out.println(" Adding: " + files[i].getAbsolutePath());
	      out.putNextEntry(new ZipEntry(files[i].getParentFile().getName() + File.separator +files[i].getName()));
	      int len;
	      while ((len = in.read(tmpBuf)) > 0) {
	        out.write(tmpBuf, 0, len);
	      }
	      out.closeEntry();
	      in.close();
	    }
	  }

	  public static void main(String[] args) throws Exception {
	    zipDir("result.zip", ".");
	  }
	/*public static void main(String[] args) throws IOException {
		// unzip("marcs.zip");
		// delete(new File("marcs.zip"));

		// new File("ris_files").renameTo(new File("xxx"));
		//zipDir(new File("marcs"), "xxx");
		
		//unzip("E:/download_result/marcs_1TO100.zip");
		
		zipDir(new File("E:/download_result"), "result.zip");
	}*/
}
