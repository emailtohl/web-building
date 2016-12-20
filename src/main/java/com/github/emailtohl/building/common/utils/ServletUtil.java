package com.github.emailtohl.building.common.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * 本类使用HttpServletRequest和HttpServletResponse，与业务结合较紧，根据实际需要进行调整
 * @author HeLei
 */
public final class ServletUtil {
	private static final Logger logger = Logger.getLogger(ServletUtil.class.getName());

	private ServletUtil() {
	}
	
	/**
	 * 大多数情况下，前端提交的参数都是单一参数，故本方法将ServletRequest中获取Map<String,
	 * String[]>简单转成Map<String, String> 若遇到有checkbox等多个相同参数名的情况，则需单独获取
	 * 
	 * @param request
	 *            ServletRequest及其子类，如HttpServletRequest
	 * @return 名值对的Map<String, String>
	 */
	public static Map<String, String> getFirstParamsMap(ServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Map<String, String[]> maps = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : maps.entrySet()) {
			String[] values = entry.getValue();
			if (values == null || values.length == 0) {
				continue;
			}
			res.put(entry.getKey(), values[0]);
		}
		return res;
	}
	
	/**
	 * 通过流上传文件
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param uploadPath 文件位于服务器的路径
	 * @param filename 文件名，若在请求头中设置了filename，则此参数可设置为null
	 */
	public static void upload(HttpServletRequest request, HttpServletResponse response, String uploadPath,
			String filename) {
		uploadPath = uploadPath == null ? "" : uploadPath;
		if (filename == null) {
			try {
				filename = URLDecoder.decode(request.getHeader("filename"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
		}
		response.setContentType("text/plain;charset=utf-8");
		long fileLength = -1L;// 初始化fileLength防止不能正确获取文件长度的情况而影响最后的判断
		fileLength = request.getContentLengthLong();
		File f = new File(request.getServletContext().getRealPath(uploadPath + filename));
		ServletInputStream sis = null;
		FileOutputStream fos = null;
		byte[] buffer = new byte[1024];
		int b;
		try {
			sis = request.getInputStream();
			fos = new FileOutputStream(f);
			while (true) {
				b = sis.read(buffer);
				if (b == -1)
					break;
				fos.write(buffer, 0, b);
				fileLength -= b;
			}
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			try {
				if (sis != null)
					sis.close();
				if (fos != null)
					fos.close();
				// 若前端终止了文件流输入，则异常由容器触发，本类无法截获，不管是否异常，清理工作在此处做
				if (f != null && f.exists() && fileLength > 0L) {
					logger.info("上传文件不完整");
					if (f.delete()) {
						logger.info("已删除该文件");
					} else {
						logger.info("但删除失败");
					}
				}
				PrintWriter out = response.getWriter();
				if (out != null) {
					if (fileLength == 0L)
						out.println(true);
					else
						out.println(false);
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将file输出到文件中
	 * 注意：
	 * （1） Since: Servlet 3.0
	 * （2）要使用本功能，需在Servet上要注解：@MultipartConfig
	 * 
	 * @param request HttpServletRequest
	 * @param uploadPath 上传到服务器中的目录
	 * @return 上传成功的文件名
	 */
	public static String multipartOnload(HttpServletRequest request, String uploadPath) {
		if (request == null) {
			throw new IllegalArgumentException("传入参数是null");
		}
		StringBuilder msg = new StringBuilder();
		Collection<Part> fileParts = null;
		Map<String, String[]> map = request.getParameterMap();
		try {
			fileParts = request.getParts();
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		if (fileParts != null) {
			Iterator<Part> iterable = fileParts.iterator();
			while (iterable.hasNext()) {
				Part filePart = iterable.next();// 每个filePart表示一个文件，前端可能同时上传多个文件
				try {
					String filename = filePart.getSubmittedFileName();// 获取提交文件原始的名字
					if (filename != null && !map.containsKey(filename)) {
						filePart.write(request.getServletContext().getRealPath(uploadPath + filename));
						msg.append(',').append(filename);
					}
				} catch (IOException e) {
					e.printStackTrace();
					if (filePart != null) {
						try {
							filePart.delete();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
		if (msg.length() == 0)
			return "未上传成功的文件";
		else {
			msg.deleteCharAt(0);
			msg.insert(0, "上传成功：");
			return msg.toString();
		}

	}

	/**
	 * 处理文件下载
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param downloadPath 文件下载的目录
	 */
	public static void download(HttpServletRequest request, HttpServletResponse response, String downloadPath) {
		String filename = request.getParameter("filename");
		if (filename == null || filename.trim().length() == 0)
			return;
		File f = new File(request.getServletContext().getRealPath(downloadPath + filename));
		try {
			filename = URLEncoder.encode(filename, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 设置响应头Content-Disposition，将强制浏览器询问客户是保存还是下载文件，而不是在浏览器中在线打开该文件
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		// 设置文件ContentType类型，是通用的，二进制内容类型，这样容器就不会使用字符编码对该数据进行处理（当然更规范的是使用附件真正的MIME内容类型）
		response.setContentType("application/octet-stream");
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			fis = new FileInputStream(f);
			out = response.getOutputStream();
			int b;
			byte[] buffer = new byte[1024];
			while (true) {
				b = fis.read(buffer);
				if (b == -1)
					break;
				out.write(buffer, 0, b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	  * 将'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'编码的字符串解析成对象
	  * 注意：对于多个同名参数，例如“type=a&type=b”这种情况将会被覆盖
	  * @param request
	  * @param clz
	  * @return
	  */
	public static <T> T parseForm(HttpServletRequest request, Class<T> clz) {
		if (!request.getContentType().contains("application/x-www-form-urlencoded")) {
			logger.severe(
					"T parseForm(HttpServletRequest request, Class<T> clz)： 请求头未注明application/x-www-form-urlencoded类型，若不是表单数据的话会解析失败");
		}
		StringBuilder sb = new StringBuilder();
		try (Scanner scanner = new Scanner(request.getInputStream())) {
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException("从HttpServletRequest中获取流数据失败");
		}
		String[] pairs = sb.toString().split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < pairs.length; i++) {
			String[] pair = pairs[i].trim().split("=");
			if (pair.length == 1) {// 防止“param=”情况的出现
				map.put(pair[0].trim(), "");
			} else {
				map.put(pair[0].trim(), pair[1].trim());
			}
		}
		T obj;
		try {
			obj = clz.newInstance();
		} catch (InstantiationException | IllegalAccessException e2) {
			e2.printStackTrace();
			throw new RuntimeException("该类可能没有无参构造器");
		}
/*
		Class<? super T> c = clz;
		while (c != null && c != Object.class) {
			for (Field f : c.getDeclaredFields()) {
				String name = f.getName();
				String value = map.get(name);
				if (value != null) {
					f.setAccessible(true);
					try {
						value = URLDecoder.decode(value, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					BeanUtils.injectFieldWithString(f, obj, value);
				}
			}
			c = c.getSuperclass();
		}
*/
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(clz, Object.class).getPropertyDescriptors()) {
				String name = pd.getName();
				String value = map.get(name);
				if (value != null) {
					try {
						value = URLDecoder.decode(value, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					BeanUtil.injectPropertyWithString(pd, obj, value);
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "接收表单数据的类不符合JavaBean规范", e);
		}
		return obj;

	}
	 
	/**
	 * 根据内容类型判断文件扩展名
	 * 
	 * @param contentType 内容类型
	 * @return
	 */
	public static String getFileExt(String contentType) {
		String fileExt = "";
		if ("image/jpeg".equals(contentType))
			fileExt = ".jpg";
		else if ("audio/mpeg".equals(contentType))
			fileExt = ".mp3";
		else if ("audio/amr".equals(contentType))
			fileExt = ".amr";
		else if ("video/mp4".equals(contentType))
			fileExt = ".mp4";
		else if ("video/mpeg4".equals(contentType))
			fileExt = ".mp4";
		return fileExt;
	}
}
