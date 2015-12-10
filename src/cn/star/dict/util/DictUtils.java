package cn.star.dict.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class DictUtils {
	/** 解压数据，将gzip（.gz , .dz）压缩的文件解压到输出流中 */
	public static void unZip(File dz, OutputStream out) {
		GZIPInputStream gin = null;
		byte[] buffer = new byte[1024];
		try {
			int i = 0;
			gin = new GZIPInputStream(new FileInputStream(dz));
			while ((i = gin.read(buffer)) > 0) {
				out.write(buffer, 0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gin != null) {
				try {
					gin.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/** 获取文件扩展名 ,不包含(.)点 */
	public static String getExtend(String fileName) {
		String ext = null;
		int index = fileName.lastIndexOf(".");
		if (index > 0 && index + 1 < fileName.length())
			ext = fileName.substring(index + 1);
		return ext;
	}

	/** 获取文件名字部分，不包含扩展名 */
	public static String getName(String fileName) {
		String ext = null;
		int index = fileName.lastIndexOf(".");
		if (index > 0 && index < fileName.length())
			ext = fileName.substring(0, index);
		else
			ext = fileName;
		return ext;
	}

	/**
	 * 格式化数据长度，保留2位小数
	 * 
	 * @param length
	 *            数据长度
	 * @return 格式化的值，带单位
	 */
	public static String formatSize(long length) {
		double size = length;
		String[] format = { "B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
		int num = 0;
		while (true) {
			if (size > 0 && size < 1024) {
				break;
			}
			size /= 1024.0;
			num++;
		}

		return String.format("%.2f %s", size, format[num]);
	}

	public static void main(String[] args) {
		System.out.println(formatSize(5000000));
	}
}
