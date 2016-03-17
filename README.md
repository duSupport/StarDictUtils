#StarDictUtils
星际词典的词库工具类，可以读取词库索引和词库内容。
可以自己写基于词典的分词，或者自己的项目中需要有查词的功能时都可以方便的用星际词典的海量现有词库。
注意：目前版本的工具是一次性把所有单词加载到内存，程序启动时效率有点低，后续会优化该部分。
使用示例：```
package cn.star.dict.test;

import java.io.FileNotFoundException;
import java.util.Scanner;

import cn.star.dict.Dictionary;
import cn.star.dict.Word;

public class IdxTest {
	// E:\excel\stardict-powerword2010_1_501-2.4.2\powerword2010_1_501.ifo
	public static final String base = "e:\\excel\\";

	public static void main(String[] args) throws FileNotFoundException {
		// 初始化一个词典，然后加载词库(.ifo为词库描述文件)
		Dictionary enToZh = new Dictionary();
		enToZh.load(base + "stardict-langdao-ec-gb-2.4.2\\langdao-ec-gb.ifo");
		System.out.println(enToZh.getInfo().getBookName());
		System.out.println(enToZh.getInfo().getAuthor());
		System.out.println("收录词数：" + enToZh.getInfo().getWordCount());
		Scanner scan = new Scanner(System.in);
		String w = null;
		while (true) {
			System.out.println("请输入要查询的单词：");
			if ("exit".equals(w)) {
				break;
			}
			w = scan.nextLine();
			if (w == null || "".equals(w)) {
				continue;
			}
			// 查询单词，并输出结果
			Word wor = enToZh.select(w.trim());
			if (wor == null) {
				System.out.println("词典中尚未收录 " + w);
			} else {
				System.out.println("<-- " + enToZh.getInfo().getBookName() + " -->");
				System.out.println(wor.getWord());
				System.out.println(wor.getExplanation());
			}

			System.out.println();

		}
		scan.close();
	}

}
```