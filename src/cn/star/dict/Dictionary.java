package cn.star.dict;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.star.dict.util.DictUtils;

/**
 * 字典，传入.ifo文件路径，会自动搜索 .idx, .idx.dz , .dict , .dict.dz。<br>
 * 如果.idx存在，则不会加载.idx.dz文件， dict文件同理
 */
public class Dictionary implements Serializable {

	private static final long serialVersionUID = -1463120587565804394L;
	/** 对应ifo文件信息 */
	private DictInfo info;
	/** 所有词条(分析idx和dict文件后得到) */
	private List<Word> words;

	private Map<String, Word> wordMap;

	public Dictionary() {

	}

	/** 传入词典的ifo文件路径，加载并初始化词典 */
	public Dictionary(String dict) {
		load(dict);
	}

	/** 加载词典文件并初始化词典 */
	public void load(String dict) {
		File ifoFile = new File(dict);
		String ext = DictUtils.getExtend(dict);
		if (".ifo".equalsIgnoreCase(ext)) {
			throw new Error("提供的 " + dict + " 文件不是 .ifo 文件，无法使用。");
		}
		String basePath = ifoFile.getParent();
		String baseName = DictUtils.getName(ifoFile.getName());

		info = loadInfo(dict);

		File idxFile = new File(basePath + File.separatorChar + baseName + ".idx");
		File idxGz = new File(basePath + File.separatorChar + baseName + ".idx.dz");
		File dictFile = new File(basePath + File.separatorChar + baseName + ".dict");
		File dictGz = new File(basePath + File.separatorChar + baseName + ".dict.dz");
		List<DicIdx> idxs = null;
		if (idxFile.exists()) {
			// System.out.println("准备读取" + idxFile.getName() + " 文件。");
			idxs = loadIdx(idxFile);
		} else if (idxGz.exists()) {
			// System.out.println("准备读取" + idxGz.getName() + " 文件。");
			idxs = loadIdxForGz(idxFile);
		}

		if (idxs.size() != info.getWordCount()) {
			System.out.println(String.format("警告：解析到的单词数量 (%d) 与描述文件数量 (%d) 不一致，请检查.", idxs.size(), info.getWordCount()));
		}

		InputStream fdict = null;
		try {
			if (dictFile.exists()) {
				// System.out.println("准备读取" + dictFile.getName() + " 文件。");
				fdict = new FileInputStream(dictFile);
			} else {
				// System.out.println("准备读取" + dictGz.getName() + " 文件。");
				ByteArrayOutputStream outBuffer = new ByteArrayOutputStream(info.getIdxFileSize());
				DictUtils.unZip(dictGz, outBuffer);
				fdict = new ByteArrayInputStream(outBuffer.toByteArray());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Word> words = new ArrayList<Word>();
		Map<String, Word> wordMap = new HashMap<String, Word>();
		// System.out.println("开始解析词典数据。");
		if (idxs != null) {
			for (DicIdx i : idxs) {
				byte[] bexp = readData(fdict, i.getOffset(), i.getLength());
				Word w = new Word(i.getWord(), new String(bexp));
				words.add(w);
				wordMap.put(w.getWord().toLowerCase(), w);
			}
			this.wordMap = wordMap;
		}
		// System.out.println("词典解析完毕，一共加载了" + words.size() + " 条数据。");
		this.setWords(words);
	}

	/** 从数据流中读取单词的释义 */
	private byte[] readData(InputStream dict, int offset, int length) {
		byte[] info = new byte[length];
		try {
			dict.read(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;

	}

	/** 加载索引信息 */
	private List<DicIdx> loadIdx(File idxfile) {
		List<DicIdx> idxs = new ArrayList<DicIdx>();
		FileInputStream in = null;
		try {
			in = new FileInputStream(idxfile);
			idxs = loadIdx(in, idxfile.length());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return idxs;
	}

	/** 通过gzip文件加载索引 */
	private List<DicIdx> loadIdxForGz(File idxfile) {
		List<DicIdx> idxs = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DictUtils.unZip(idxfile, out);
		byte[] b = out.toByteArray();
		ByteArrayInputStream bin = new ByteArrayInputStream(b);

		idxs = loadIdx(bin, b.length);
		return idxs;
	}

	/** 加载索引信息 */
	private List<DicIdx> loadIdx(InputStream idxStream, long count) {
		List<DicIdx> idxs = new ArrayList<DicIdx>();
		byte[] idx = new byte[(int) count];
		InputStream in = idxStream;
		try {
			// in = new FileInputStream(idxStream);
			in.read(idx);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String word = null;
		for (int i = 0; i < count; i++) {
			int offset = 0;
			int length = 0;
			int j = 0;
			while (idx[i + j] != 0) {
				j++;
			}
			byte[] dest = new byte[j];
			System.arraycopy(idx, i, dest, 0, j);
			word = new String(dest);
			i += j;
			// do {
			// sb.append((char)idx[i]);
			// } while (idx[++i] != 0);
			// 前置 ++ 跳过'\0'这一位
			// 四位为一个数字，将 有符号的byte 转换为无符号数字（idx[++i] & 0xFF）
			int num = info.getIdxOffsetBits() / 32;
			for (int x = 0; x < num; x++) {
				offset |= (idx[++i] & 0xFF);
				offset <<= 8;
				offset |= (idx[++i] & 0xFF);
				offset <<= 8;
				offset |= (idx[++i] & 0xFF);
				offset <<= 8;
				offset |= (idx[++i] & 0xFF);
			}
			for (int x = 0; x < num; x++) {
				length |= (idx[++i] & 0xFF);
				length <<= 8;
				length |= (idx[++i] & 0xFF);
				length <<= 8;
				length |= (idx[++i] & 0xFF);
				length <<= 8;
				length |= (idx[++i] & 0xFF);
			}
			idxs.add(new DicIdx(word, offset, length));
		}
		return idxs;
	}

	/** 加载配置文件信息 */
	private DictInfo loadInfo(String infoFile) {
		DictInfo info = null;
		Map<String, String> vmap = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(infoFile));
			String tmp = "";
			while ((tmp = reader.readLine()) != null) {
				String[] line = tmp.split("=");
				if (line == null || line.length < 2)
					continue;
				vmap.put(line[0], line[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String t;
		info = new DictInfo(vmap.get("version"), vmap.get("bookname"), Integer.parseInt(vmap.get("wordcount")), Integer.parseInt(vmap.get("idxfilesize")), vmap.get("author"), vmap.get("description"));
		info.setDate(vmap.get("date"));
		info.setEmail(vmap.get("email"));
		t = vmap.get("idxoffsetbits");
		if (t != null && "".equals(t)) {
			try {
				// 32 或 64 ，3.0.0版本以后的有用，其它没用，我就按32位处理
				info.setIdxOffsetBits(Integer.parseInt(t));
			} catch (NumberFormatException e) {
				info.setIdxOffsetBits(32);
			}
		} else {
			info.setIdxOffsetBits(32);
		}

		info.setSameTypesEquence(vmap.get("sametypeequence"));
		t = vmap.get("synwordcount");
		if (t != null && !"".equals(t.trim())) {
			info.setSynWordCount(Integer.parseInt(t));
		}
		info.setWebsite(vmap.get("website"));

		return info;
	}

	public Word select(String word) {
		if (wordMap == null) {
			System.out.println("词典初始化失败！");
			return null;
		}
		if (word != null) {
			word = word.toLowerCase();
		}
		return wordMap.get(word);
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}

	public DictInfo getInfo() {
		return info;
	}

	public void setInfo(DictInfo info) {
		this.info = info;
	}
}
