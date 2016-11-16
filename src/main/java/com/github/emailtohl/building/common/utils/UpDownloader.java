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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * 文件上传下载器
 * @author HeLei
 */
public class UpDownloader {
	private File basePath;

	/**
	 * 构造时，同时在文件系统中创建basePath的目录
	 * @param basePath
	 */
	public UpDownloader(File basePath) {
		this.basePath = basePath;
		if (!this.basePath.exists()) {
			if (!basePath.mkdirs()) {
				throw new IllegalStateException("路径不存在，并且创建失败：" + basePath);
			}
		}
	}

	/**
	 * 接收字符串的构造器
	 * @param basePath
	 */
	public UpDownloader(String basePath) {
		this(new File(basePath));
	}
	
	/**
	 * 上传文件，地址为basePath + relativePath
	 * @param relativePath 例如icon/id_xxx.png
	 * @return basePath + relativePath
	 * @throws IOException 保存文件时出现异常
	 */
	public String upload(String relativePath, Part part) throws IOException {
		File f = new File(basePath, relativePath);
		if (f.exists()) {
			throw new IllegalArgumentException("上传文件重名，该文件已经上传");
		}
		String absolutePath = f.getAbsolutePath();
		part.write(absolutePath);
		return absolutePath;
	}
	
	/**
	 * 上传文件，地址为basePath + relativePath，用于文件小于Integer.MAX_VALUE
	 * @param relativePath 例如icon/id_xxx.png
	 * @param bin 文件二进制数据
	 * @return basePath + relativePath
	 * @throws IOException 保存文件时出现异常
	 */
	public String upload(String relativePath, byte[] bin) throws IOException {
		File f = new File(basePath, relativePath);
		if (f.exists()) {
			throw new IllegalArgumentException("上传文件重名，该文件已经上传");
		}
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(f))) {
			os.write(bin);
		}
		return f.getAbsolutePath();
	}
	
	/**
	 * 上传文件，地址为basePath + relativePath，主要用于文件大于Integer.MAX_VALUE
	 * @param relativePath 例如icon/id_xxx.png
	 * @param bin 文件二进制数据
	 * @return basePath + relativePath
	 * @throws IOException 保存文件时出现异常
	 */
	public String upload(String relativePath, InputStream in) throws IOException {
		File f = new File(basePath, relativePath);
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
		File f = new File(basePath, relativePath);
		byte[] bin;
		try (InputStream in = new BufferedInputStream(new FileInputStream(f))) {
			bin = new byte[in.available()];
			in.read(bin);
		}
		return bin;
	}
	
	/**
	 * 获取到实际环境中的根目录，也可以应用于大文件下载，或者是需要操纵流的情况
	 * @param relativePath
	 * @return
	 */
	public String getAbsolutePath(String relativePath) {
		return new File(basePath, relativePath).getAbsolutePath();
	}

	/**
	 * 供Servlet环境下载
	 * @param relativePath 文件相对路径
	 * @param response Servlet响应
	 * @throws FileNotFoundException 没有查找到文件的异常
	 * @throws IOException
	 */
	public void download(String relativePath, HttpServletResponse response) throws FileNotFoundException, IOException {
		File f = new File(basePath, relativePath);
		try (InputStream fis = new BufferedInputStream(new FileInputStream(f))) {
			// 设置响应头Content-Disposition，将强制浏览器询问客户是保存还是下载文件，而不是在浏览器中在线打开该文件
			response.setHeader("Content-Disposition", "attachment;filename=" + f.getName());
			// 设置文件ContentType类型，是通用的，二进制内容类型，这样容器就不会使用字符编码对该数据进行处理（当然更规范的是使用附件真正的MIME内容类型）
			response.setContentType("application/octet-stream");
			try (ServletOutputStream out = response.getOutputStream()) {
				int b;
				byte[] buffer = new byte[1024];
				while (true) {
					b = fis.read(buffer);
					if (b == -1)
						break;
					out.write(buffer, 0, b);
				}
			}
		}
	}
	
	/**
	 * 使用输出流传输文件
	 * @param relativePath 文件相对路径
	 * @param out 输出流
	 * @throws FileNotFoundException 没有查找到文件的异常
	 * @throws IOException
	 */
	public void download(String relativePath, OutputStream out) throws FileNotFoundException, IOException {
		File f = new File(basePath, relativePath);
		try (InputStream fis = new BufferedInputStream(new FileInputStream(f))) {
			int b;
			byte[] buffer = new byte[1024];
			while (true) {
				b = fis.read(buffer);
				if (b == -1)
					break;
				out.write(buffer, 0, b);
			}
		}
	}
	
	/**
	 * 删除整个目录树
	 */
	public void deleteDir(String absolutePath) {
		File f = new File(absolutePath);
		if (f.isDirectory()) {
			for (File ff : f.listFiles()) {
				deleteDir(ff.getAbsolutePath());
			}
			f.delete();
		} else {
			f.delete();
		}
	}
	
}
