package com.gy.utils.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static com.gy.utils.String.StringUtils.toHexString;

public class MD5Utils {

	/**
	 * 获取签名字符串
	 * 
	 * @return String 返回签名字符串
	 */
	public static String getSign(Map<String, String> data) {
		// 获取map长度
		int length = data.size();
		String[] array = new String[length];
		StringBuffer stringBuffer = new StringBuffer();

		int x = 0;// 数组下标标识
		for (Map.Entry<String, String> m : data.entrySet()) {
			array[x] = m.getKey() + m.getValue();
			x++;
		}
		// 升序排序
		Arrays.sort(array);

		for (int i = 0; i < length; i++) {
			stringBuffer.append(array[i]);
		}
		return getStringMD5(stringBuffer.toString());
	}

	/**
	 * MD5运算
	 * 
	 * @param s
	 *            传入明文
	 * @return String 返回密文
	 */
	public static String getStringMD5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String getFileMD5(File file) {
		MessageDigest digest;
		InputStream inputStream = null;
		try {
			digest = MessageDigest.getInstance("MD5");

			inputStream = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int numRead = 0;
			while ((numRead = inputStream.read(buffer)) > 0) {
				digest.update(buffer, 0, numRead);
			}
			inputStream.close();
			return toHexString(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
