package ris;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import main.MarcProcessor;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import util.utility;

public class RISGenerator{

	private static final Logger logger = Logger.getLogger(RISGenerator.class.getName());
	List<MarcProcessor> processorList = new LinkedList<MarcProcessor>();
	static String ris_dir = "ris_files";
	public void add(MarcProcessor mp){
		processorList.add(mp);
	}
	
	public void process(String marcDirPath, String ris_file_name_NO_ext) throws FileNotFoundException, DocumentException {
		
		//String ris_dir = RISGenerator.RISConvert(marc_dir_path);
		File marcDir = new File(marcDirPath);

		File[] marcArr = marcDir.listFiles();

		System.out.println("start generating RIS files ...");
		
		for (int i = 0; i < marcArr.length; i++) {
			String filePath = marcArr[i].getAbsolutePath();
			RISConvert(filePath);
		}
		String dir = ris_dir;
		
		for(int i=0; i<processorList.size(); i++){
			processorList.get(i).process(dir);
		}
		
		//finally
		boolean success = new File(dir).renameTo(new File(ris_file_name_NO_ext));
		System.out.println(ris_dir + " is changed to " + ris_file_name_NO_ext + " : " + success);
	}
	
	public static String RISConvert(String filePath) throws FileNotFoundException {
		
		new File(ris_dir).mkdir();
		
		List<String> TY = new ArrayList<String>();
		List<String> ID = new ArrayList<String>();
		List<String> AU = new ArrayList<String>();
		List<String> T1 = new ArrayList<String>();

		List<String> T3 = new ArrayList<String>();
		List<String> PB = new ArrayList<String>();
		List<String> PY = new ArrayList<String>();
		List<String> CY = new ArrayList<String>();
		List<String> SN = new ArrayList<String>();
		List<String> KW = new ArrayList<String>();
		List<String> L2 = new ArrayList<String>();
		List<String> N1 = new ArrayList<String>();
		List<String> UR = new ArrayList<String>();
		List<String> ER = new ArrayList<String>();
		//

		SAXReader reader = new SAXReader();

		File marcFile = new File(filePath);
		int marc_name_len = marcFile.getName().length();
		String ris_filename = marcFile.getName().substring(0, marc_name_len-4) + ".ris";
		// System.out.println(ris_filename);
		File risFile = new File(ris_dir + "/" +ris_filename);

		PrintWriter pw = new PrintWriter(risFile);

		Document document = null;
		try {
			document = reader.read(marcFile);
		} catch (DocumentException e1) {
			logger.warn("DocumentException: ", e1);
			pw.close();
			// simply return null
			return null;
		}

		Element root_element = document.getRootElement(); // root element in
															// MARC "record"
		// System.out.println(root_element.getName());
		Iterator iterator = root_element.elementIterator();

		//int i = 0;

		//while (iterator.hasNext()) {
			Element e = root_element; //(Element) iterator.next(); // the "record" element
			// this is redundant because root here is already record element
			// System.out.println(e.getName());

			Iterator data_field_iterator = e.elementIterator();

			// System.out.println(i);

			//i++;

			Element e_datafield = null;
			Element subfield = null;

			while (data_field_iterator.hasNext()) {
				e_datafield = (Element) data_field_iterator.next(); // "data field"
																	// element

				// System.out.println(e_datafield.getName());

				if (e_datafield.getName().equals("datafield")) {
					if (e_datafield.attributeValue("tag").equals("100")
							|| e_datafield.attributeValue("tag").equals("110")
							|| e_datafield.attributeValue("tag").equals("111")
							|| e_datafield.attributeValue("tag").equals("700")
							|| e_datafield.attributeValue("tag").equals("710")
							|| e_datafield.attributeValue("tag").equals("711")
							|| e_datafield.attributeValue("tag").equals("720")) {
						// AU.add(e_datafield.getStringValue());
						// System.out.println(e_datafield.getStringValue());

						Iterator AU_iter = e_datafield.elementIterator();

						while (AU_iter.hasNext()) // subfield element
						{
							subfield = (Element) AU_iter.next();
							// System.out.println(subfield.getName());
							if (subfield.attributeValue("code").equals("a")) {
								// System.out.println("AU: "+subfield.getStringValue());

								AU.add(subfield.getStringValue());
							}
						}
					} else if (e_datafield.attributeValue("tag").equals("240")
							|| e_datafield.attributeValue("tag").equals("245")) {
						Iterator T1_iter = e_datafield.elementIterator();

						while (T1_iter.hasNext()) {
							subfield = (Element) T1_iter.next();

							if (subfield.attributeValue("code").equals("a")) {
								// System.out.println("T1: "+
								// subfield.getStringValue());
								T1.add(subfield.getStringValue());
							}
						}
					} else if (e_datafield.attributeValue("tag").equals("490")) {
						Iterator T3_iter = e_datafield.elementIterator();
						while (T3_iter.hasNext()) {
							subfield = (Element) T3_iter.next();
							if (subfield.attributeValue("code").equals("a")) {
								// System.out.println("T3: "+
								// subfield.getStringValue());
								T3.add(subfield.getStringValue());
							}
						}
					} else if (e_datafield.attributeValue("tag").equals("260")) {
						Iterator publisher_iter = e_datafield.elementIterator();
						while (publisher_iter.hasNext()) {
							subfield = (Element) publisher_iter.next();

							if (subfield.attributeValue("code").equals("a")) {
								// System.out.println("CY: "+subfield.getStringValue());

								CY.add(subfield.getStringValue());
							} else if (subfield.attributeValue("code").equals(
									"b")) {
								// System.out.println("PB: "+subfield.getStringValue());
								PB.add(subfield.getStringValue());
							} else if (subfield.attributeValue("code").equals(
									"c")) {
								// System.out.println("PY: "+subfield.getStringValue());
								PY.add(subfield.getStringValue());
							}
						}
					} else if (e_datafield.attributeValue("tag").equals("020")
							|| e_datafield.attributeValue("tag").equals("022")) { // this
																					// might
																					// need
																					// to
																					// be
																					// modified
																					// to
																					// reflect
																					// the
																					// difference
																					// between
																					// issn
																					// and
																					// isbn
																					// !!!!!!!!!!!!!!!!!!!!

						Iterator SN_iter = e_datafield.elementIterator();
						while (SN_iter.hasNext()) {
							subfield = (Element) SN_iter.next();
							if (subfield.attributeValue("code").equals("a")) {
								 //System.out.println("SN: "+
								 //subfield.getStringValue());
								SN.add(subfield.getStringValue());
							}
						}

					} else if (e_datafield.attributeValue("tag").equals("650")
							|| e_datafield.attributeValue("tag").equals("651")) {
						Iterator KW_iter = e_datafield.elementIterator();
						String KW_value = "";
						int xx = 0;
						while (KW_iter.hasNext()) {
							subfield = (Element) KW_iter.next();
							if(xx == 0)
							{
								KW_value += subfield.getStringValue();
							}else
							{
								KW_value += " / " ;
								KW_value += subfield.getStringValue();
							}
							
							xx ++;
						}
						KW.add(KW_value);
						
					} else if (e_datafield.attributeValue("tag").equals("974")) {
						Iterator L2_iter = e_datafield.elementIterator();

						while (L2_iter.hasNext()) {
							subfield = (Element) L2_iter.next();

							if (subfield.attributeValue("code").equals("u")) {
								 //System.out.println("L2: "+
								// subfield.getStringValue());

								L2.add(subfield.getStringValue());

								// System.out.println("UR: "+
								// "http://hdl.handle.net/2027/" +
								// subfield.getStringValue());
								// UR.add("http://hdl.handle.net/2027/" +
								// subfield.getStringValue());
							}
						}
					} else if (e_datafield.attributeValue("tag").equals("250")) {
						Iterator N1_iter = e_datafield.elementIterator();
						while (N1_iter.hasNext()) {
							subfield = (Element) N1_iter.next();

							if (subfield.attributeValue("code").equals("a")) {
								// System.out.println("N1: "+
								// subfield.getStringValue());
								N1.add(subfield.getStringValue());
							}
						}
					} else if (e_datafield.attributeValue("tag").equals("970")) {
						Iterator TY_iter = e_datafield.elementIterator();
						while (TY_iter.hasNext()) {
							subfield = (Element) TY_iter.next();

							if (subfield.attributeValue("code").equals("b")) {
								// System.out.println("N1: "+
								// subfield.getStringValue());
								TY.add(subfield.getStringValue());

							}
						}
					}
				} else if (e_datafield.getName().equals("controlfield")) {
					if (e_datafield.attributeValue("tag").equals("001")) {
						// System.out.println("ID: " +
						// e_datafield.getStringValue());

						ID.add(e_datafield.getStringValue());

						UR.add("http://catalog.hathitrust.org/Record/"
								+ e_datafield.getStringValue());
					}
				}
			}
		//}

		// write content to .ris file for each bib record
		int j = 0;
		if (TY.size() == 0) {
			pw.println("TY  - ");
			pw.flush();
		} else if (TY.size() != 0) {

			pw.println("TY  - " + TY.get(0));
			pw.println("TP  - " + TY.get(0));
			pw.flush();

		}

		if (ID.size() != 0) {
			for (j = 0; j < ID.size(); j++) {
				pw.println("ID  - " + ID.get(j));
				pw.flush();
			}
		}

		if (AU.size() != 0) {
			for (j = 0; j < AU.size(); j++) {
				pw.println("AU  - " + AU.get(j));
				pw.flush();
			}
		}

		if (T1.size() != 0) {
			for (j = 0; j < T1.size(); j++) {
				pw.println("T1  - " + T1.get(j));
				pw.flush();
			}
		}

		if (T3.size() != 0) {
			for (j = 0; j < T3.size(); j++) {
				pw.println("T3  - " + T3.get(j));
				pw.flush();
			}
		}

		if (PB.size() != 0) {
			for (j = 0; j < PB.size(); j++) {
				pw.println("PB  - " + PB.get(j));
				pw.flush();
			}
		}

		if (PY.size() != 0) {
			for (j = 0; j < PY.size(); j++) {
				pw.println("PY  - " + PY.get(j));
				pw.flush();
			}
		}

		if (CY.size() != 0) {

			String CY_value = "CY  - ";
			for (j = 0; j < CY.size(); j++) {
				if (j == 0)
					CY_value += CY.get(j);
				else if (j != 0) {
					CY_value += " ";
					CY_value += CY.get(j);
				}
			}

			pw.println(CY_value);
			pw.flush();
		}

		if (SN.size() != 0) {
			for (j = 0; j < SN.size(); j++) {
				pw.println("SN  - " + SN.get(j));
				pw.flush();
			}
		}

		if (KW.size() != 0) {
			for (j = 0; j < KW.size(); j++) {
				pw.println("KW  - " + KW.get(j));
				pw.flush();
			}
		}

		if (L2.size() != 0) {
			for (j = 0; j < L2.size(); j++) {
				pw.println("L2  - " + L2.get(j));
				pw.flush();
			}
		}

		if (N1.size() != 0) {
			for (j = 0; j < N1.size(); j++) {
				pw.println("N1  - " + N1.get(j));
				pw.flush();
			}
		}

		if (UR.size() != 0) {
			for (j = 0; j < UR.size(); j++) {
				pw.println("UR  - " + UR.get(j));
				pw.flush();
			}
		}

		if (ER.size() == 0) {
			pw.println("ER  - ");
			pw.flush();
		} else if (ER.size() != 0) {
			for (j = 0; j < ER.size(); j++) {
				pw.println("ER  - " + ER.get(j));
				pw.flush();
			}
		}

		pw.flush();
		pw.close();
		return ris_dir;

	}

	public static void main(String[] args) throws DocumentException,
			FileNotFoundException {
		File marc_dir = new File("marcs");

		File[] marc_arr = marc_dir.listFiles();

		System.out.println("start converting ...");

		for (int i = 0; i < marc_arr.length; i++) {
			String file_path = marc_arr[i].getAbsolutePath();
			RISConvert(file_path);
		}
		System.out.println("conversion completed !!!");
		// String file_path = "Z:/IUCollection_MARC/inu.30000000208920.xml";
		// RISConvert(file_path);
	}
}
