package cn.star.dict.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import cn.star.dict.Dictionary;
import cn.star.dict.Word;

public class IdxTest {
	// E:\excel\stardict-powerword2010_1_501-2.4.2\powerword2010_1_501.ifo
	public static final String base = "e:\\excel\\";

	// public static final String base =
	// "e:\\excel\\stardict-powerword2011_1_900-2.4.2\\";

	public static void main(String[] args) throws FileNotFoundException {
//		Dictionary enToZh = new Dictionary();
//		Dictionary zhToEn = new Dictionary();
//		// dict.load(base + "powerword2011_1_900.ifo");
//		enToZh.load(base + "stardict-langdao-ec-gb-2.4.2\\langdao-ec-gb.ifo");
//		zhToEn.load(base + "stardict-langdao-ce-gb-2.4.2\\langdao-ce-gb.ifo");
//		// dict.load(base + "powerword2010_1_501.ifo"); // 金山简明英汉词典
//		System.out.println(enToZh.getInfo().getBookName());
//		System.out.println(enToZh.getInfo().getAuthor());
//		System.out.println("收录词数：" + enToZh.getInfo().getWordCount());
//		Scanner scan = new Scanner(System.in);
//		String w = null;
//		while (true) {
//			System.out.println("请输入要查询的单词：");
//			if ("exit".equals(w)) {
//				break;
//			}
//			w = scan.nextLine();
//			if (w == null || "".equals(w)) {
//				continue;
//			}
//			Word wor = enToZh.select(w.trim());
//			Word zh = zhToEn.select(w.trim());
//			if (zh == null && wor == null) {
//				System.out.println("词典中尚未收录 " + w);
//			} else {
//				if (zh != null) {
//					System.out.println("<-- " + zhToEn.getInfo().getBookName() + " -->");
//					System.out.println(zh.getWord());
//					System.out.println(zh.getExplanation());
//				}
//				if (wor != null) {
//					System.out.println("<-- " + enToZh.getInfo().getBookName() + " -->");
//					System.out.println(wor.getWord());
//					System.out.println(wor.getExplanation());
//				}
//			}
//
//			System.out.println();
//
//		}
//		scan.close();
		long t=Long.MAX_VALUE;
		System.out.println(Integer.MAX_VALUE/1000/60/60/24);
		
	} 

	@Test
	public void myDict() throws IOException {
		File mydict = new File(base + "java_docs_dic.txt");
		Dictionary dict = new Dictionary();
		dict.load(base + "stardict-langdao-ec-gb-2.4.2\\langdao-ec-gb.ifo");
		Scanner scan = null;
		try {
			scan = new Scanner(mydict);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String str = null;
		Word w = null;
		List<String> words = new ArrayList<String>();
		FileOutputStream out = new FileOutputStream(base + "docs_words.txt");
		try {
			while ((str = scan.nextLine()) != null) {
				String[] t = str.split(" ");
				if (t.length > 0 && t[0].length() > 2) {
					words.add(t[0]);
				}
			}
		} catch (Exception e) {

		}
		for (int i = words.size() - 1; i >= 0; i--) {
			w = dict.select(words.get(i));
			if (w != null) {
				out.write(w.getWord().getBytes());
				out.write("\n".getBytes());
				out.write(w.getExplanation().getBytes());
				out.write("\n\n".getBytes());
			}
		}
		scan.close();
		out.close();
	}

	
	
}
