package com.github.emailtohl.building.site.controller;

import static com.github.emailtohl.building.common.Constant.PATTERN_SEPARATOR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.common.lucene.FileSearch;
import com.github.emailtohl.building.common.utils.ServletUtil;
import com.github.emailtohl.building.common.utils.TextUtil;
import com.github.emailtohl.building.common.utils.UpDownloader;
import com.github.emailtohl.building.common.ztree.ZtreeNode;
import com.github.emailtohl.building.exception.VerifyFailure;
import com.github.emailtohl.building.site.dto.UserDto;
/**
 * 文件上传控制器
 * 本类涉及很多文件路径，为避免参数中的路径和实际路径冲突，所以请求统一为POST
 * @author HeLei
 * @date 2017.02.04
 */
@SuppressWarnings("deprecation")
@Controller
@RequestMapping("fileUploadServer")
public class FileUploadServer {
	private static final Logger logger = LogManager.getLogger();
	public static final String CMS_DIR = "cms_dir";
	private File cmsRoot;
	private Pattern startAtCmsRoot;
	private TextUtil textUtil = new TextUtil();
	
	private Object textUpdateMutex = new Object();
	private Object fileMutex = new Object();
	private UpDownloader upDownloader;
	@Inject File resourcePath;
	@Inject FileSearch fileSearch;
	
	@PostConstruct
	public void createCmsDir() throws IOException {
		cmsRoot = new File(resourcePath, CMS_DIR);
		if (!cmsRoot.exists()) {
			cmsRoot.mkdir();
		}
		upDownloader = new UpDownloader(cmsRoot);
		fileSearch.index(cmsRoot);
		startAtCmsRoot = Pattern.compile("(^" + PATTERN_SEPARATOR + cmsRoot.getName() + PATTERN_SEPARATOR + "?)|(^cms_dir" + PATTERN_SEPARATOR + "?)");
		logger.debug(startAtCmsRoot.matcher(cmsRoot.getName()));
	}
	
	@PreDestroy
	public void closeFileSearch() throws Exception {
		fileSearch.close();
	}
	
	/**
	 * 获取资源管理的根目录的数据结构
	 * @return
	 */
	@RequestMapping(value = "root", method = RequestMethod.GET)
	@ResponseBody
	public ZtreeNode getRoot() {
		return ZtreeNode.newInstance(cmsRoot);
	}

	/**
	 * 查询文本内容
	 * @param param 内容的字符串
	 * @return 路径集合
	 */
	@RequestMapping(value = "query", method = RequestMethod.GET)
	@ResponseBody
	public ZtreeNode query(@RequestParam(required = false, name = "param", defaultValue = "") String param) {
		ZtreeNode node = ZtreeNode.newInstance(cmsRoot);
		if (!param.isEmpty()) {
			fileSearch.queryForFilePath(param).forEach(s -> {
				String relativelyPath = s.substring(s.indexOf(CMS_DIR));
				node.setOpen(relativelyPath);
			});
		}
		return node;
	}
	
	/**
	 * 创建一个目录
	 * @param dirName 目录相对路径
	 */
	@RequestMapping(value = "createDir", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void createDir(String dirName) {
		String relativePath = getRelativePath(dirName);
		File f = new File(upDownloader.getAbsolutePath(relativePath));
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	
	/**
	 * 为目录或文件改名
	 * @param srcName 原来的名字
	 * @param destName 更新的名字
	 * @throws IOException 
	 */
	@RequestMapping(value = "reName", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void reName(String srcName, String destName) throws IOException {
		String srcap = upDownloader.getAbsolutePath(getRelativePath(srcName));
		String destap = upDownloader.getAbsolutePath(getRelativePath(destName));
		File src = new File(srcap);
		File dest = new File(destap);
		if (src.exists()) {
			synchronized (fileMutex) {
				src.renameTo(dest);
				fileSearch.deleteIndex(new File(srcap));
				fileSearch.updateIndex(new File(destap));
			}
		}
	}
	
	/**
	 * 删除目录或文件
	 * @param filename
	 * @throws IOException 
	 */
	@RequestMapping(value = "delete", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void delete(String filename) throws IOException {
		String absolutePath = upDownloader.getAbsolutePath(getRelativePath(filename));
		synchronized (fileMutex) {
			UpDownloader.deleteDir(absolutePath);
			fileSearch.deleteIndex(new File(absolutePath));
		}
	}
	
	/**
	 * 前端用到FormData对象提交multipart formdata数据，所以需要对中文编码
	 * @param path
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "resource", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String uploadFile(@RequestPart("path") String path, @RequestPart("file") Part file) throws IOException {
		String dir = URLDecoder.decode(path, "UTF-8");
		String fullname, filename = file.getSubmittedFileName();
		if (dir.endsWith(File.separator)) {
			fullname = dir + filename;
		} else {
			fullname = dir + File.separator + filename;
		}
		fullname = getRelativePath(fullname);
		String absolutePath = upDownloader.upload(fullname, file);
		fileSearch.addIndex(new File(absolutePath));
		return filename + ": 上传成功!";
	}
	
	@RequestMapping(value = "availableCharsets", method = GET)
	@ResponseBody
	public Set<String> availableCharsets() {
		return textUtil.availableCharsets();
	}
	
	@RequestMapping(value = "loadText", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String loadText(@RequestParam(value = "path", required = true) String path
			, @RequestParam(value = "charset", required = false, defaultValue = "UTF-8") String charset) {
		String absolutePath = upDownloader.getAbsolutePath(getRelativePath(path));
		return textUtil.getText(absolutePath, charset);
	}
	
	@RequestMapping(value = "writeText", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void writeText(@RequestBody Form f) throws IOException {
		synchronized (textUpdateMutex) {
			String absolutePath = upDownloader.getAbsolutePath(getRelativePath(f.getPath()));
			textUtil.writeText(absolutePath, f.getTextContext(), f.getCharset());
			fileSearch.updateIndex(new File(absolutePath));
		}
	}
	
	/**
	 * 相对于cmsRoot的路径
	 * @param path
	 * @return
	 */
	private String getRelativePath(String path) {
		Matcher m = startAtCmsRoot.matcher(path);
		String relativePath;
		if (m.find()) {
			relativePath = m.replaceFirst("");
		} else {
			relativePath = path;
		}
		return UpDownloader.getSystemSeparator(relativePath);
	}
	/*
	public static void main(String[] args) {
		String dir = "cms_dir/new node1";
		Pattern startAtCmsRoot = Pattern.compile("(^" + PATTERN_SEPARATOR + "cms_dir" + PATTERN_SEPARATOR + "?)|(^cms_dir" + PATTERN_SEPARATOR + "?)");
		Matcher m = startAtCmsRoot.matcher(dir);
		System.out.println(m.find());
		System.out.println(m.replaceFirst(""));
		
	}*/
	
	@SuppressWarnings("unused")
	private static class Form implements Serializable {
		private static final long serialVersionUID = 968705461440871636L;
		String path;
		String textContext;
		String charset;
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public String getTextContext() {
			return textContext;
		}
		public void setTextContext(String textContext) {
			this.textContext = textContext;
		}
		public String getCharset() {
			return charset;
		}
		public void setCharset(String charset) {
			this.charset = charset;
		}
		@Override
		public String toString() {
			return "Form [path=" + path + ", textContext=" + textContext + ", charset=" + charset + "]";
		}
	}
	
	/**
	 * 用于angular fileload指令的测试控制器
	 * 由于未在spring中找到如何通过@RequestPart获取多文件
	 * 所以自定义工具从HttpServletRequest获取所有文件
	 * @param request
	 * @param multiplefiles
	 * @param singlefile
	 * @param user
	 * @param errors
	 * @return
	 */
	@RequestMapping(value = "test", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String fileUploadServer(
			HttpServletRequest request,
			@RequestPart("multiplefiles") Part multiplefiles,
			@RequestPart("singlefile") Part singlefile,
			@Valid UserDto user,
			Errors errors) {
		if (errors.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			for (ObjectError s : errors.getAllErrors()) {
				sb.append(s.toString());
			}
			throw new VerifyFailure(sb.toString());
		}
		logger.debug(multiplefiles);
		logger.debug(singlefile);
		logger.debug(user);
		String result = ServletUtil.multipartOnload(request, "temp/");
		return result;
	}
}
