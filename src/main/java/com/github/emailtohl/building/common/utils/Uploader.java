package com.github.emailtohl.building.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.Part;

/**
 * 文件上传器
 * @author HeLei
 */
public class Uploader {
	private final File uploadBase;

	public Uploader(File uploadBase) {
		super();
		this.uploadBase = uploadBase;
	}
	
	/**
	 * 上传文件，地址为uploadBase + relativePath
	 * @param relativePath 例如icon/id_xxx.png
	 * @return uploadBase + relativePath
	 * @throws IOException 保存文件时出现异常
	 */
	public String upload(String relativePath, Part part) throws IOException {
		File f = new File(uploadBase, relativePath);
		if (f.exists()) {
			throw new IllegalArgumentException("上传文件重名，该文件已经上传");
		}
		String absolutePath = f.getAbsolutePath();
		part.write(absolutePath);
		return absolutePath;
	}
	
	/**
	 * 上传文件，地址为uploadBase + relativePath，用于文件小于Integer.MAX_VALUE
	 * @param relativePath 例如icon/id_xxx.png
	 * @param bin 文件二进制数据
	 * @return uploadBase + relativePath
	 * @throws IOException 保存文件时出现异常
	 */
	public String upload(String relativePath, byte[] bin) throws IOException {
		File f = new File(uploadBase, relativePath);
		if (f.exists()) {
			throw new IllegalArgumentException("上传文件重名，该文件已经上传");
		}
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(f))) {
			os.write(bin);
		}
		return f.getAbsolutePath();
	}
	
	/**
	 * 上传文件，地址为uploadBase + relativePath，主要用于文件大于Integer.MAX_VALUE
	 * @param relativePath 例如icon/id_xxx.png
	 * @param bin 文件二进制数据
	 * @return uploadBase + relativePath
	 * @throws IOException 保存文件时出现异常
	 */
	public String upload(String relativePath, InputStream in) throws IOException {
		File f = new File(uploadBase, relativePath);
		if (f.exists()) {
			throw new IllegalArgumentException("上传文件重名，该文件已经上传");
		}
		InputStream din = new BufferedInputStream(in);
		try (OutputStream dos = new BufferedOutputStream(new FileOutputStream(f))) {
			int b;
			byte[] buffer = new byte[1024];
			while (true) {
				b = din.read(buffer);
				if (b == -1)
					break;
				dos.write(buffer, 0, b);
			}
		}
		return f.getAbsolutePath();
	}
	
	/**
	 * 获取存储的文件，文件不大于Integer.MAX_VALUE，用于文件小于Integer.MAX_VALUE
	 * @param relativePath
	 * @return 文件的二进制数组
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public byte[] getFile(String relativePath) throws FileNotFoundException, IOException {
		File f = new File(uploadBase, relativePath);
		byte[] bin;
		try (InputStream in = new BufferedInputStream(new FileInputStream(f))) {
			bin = new byte[in.available()];
			in.read(bin);
		}
		return bin;
	}
	
	/**
	 * 对于大文件，或者是需要操纵流的，可直接使用返回的绝对路径
	 * @param relativePath
	 * @return
	 */
	public String getFileAbsolutePath(String relativePath) {
		return new File(uploadBase, relativePath).getAbsolutePath();
	}
}
