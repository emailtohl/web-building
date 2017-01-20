package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.common.utils.ServletUtil;
import com.github.emailtohl.building.common.utils.TextUtil;
import com.github.emailtohl.building.common.utils.UpDownloader;
import com.github.emailtohl.building.common.ztree.ZtreeNode;
import com.github.emailtohl.building.exception.VerifyFailure;
import com.github.emailtohl.building.site.dto.UserDto;
/**
 * 文件上传控制器
 * @author HeLei
 */
@SuppressWarnings("deprecation")
@Controller
@RequestMapping("fileUploadServer")
public class FileUploadServer {
	private static final Logger logger = LogManager.getLogger();
	public static final String RESOURCE_ROOT = "resource_root";
	private File root;
	private TextUtil textUtil;
	@Inject UpDownloader upDownloader;
	
	@PostConstruct
	public void createIconDir() {
		root = new File(upDownloader.getAbsolutePath(RESOURCE_ROOT));
		if (!root.exists()) {
			root.mkdir();
		}
	}
	
	/**
	 * 获取资源管理的根目录的数据结构
	 * @return
	 */
	@RequestMapping(value = "root", method = RequestMethod.GET)
	@ResponseBody
	public ZtreeNode getRoot() {
		return ZtreeNode.newInstance(root);
	}
	
	/**
	 * 创建一个目录
	 * @param dirName 目录相对路径
	 */
	@RequestMapping(value = "createDir", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void createDir(String dirName) {
		File f = new File(upDownloader.getAbsolutePath(dirName));
		if (!f.exists()) {
			f.mkdirs();
		}
	}
	
	/**
	 * 为目录或文件改名
	 * @param srcName 原来的名字
	 * @param destName 更新的名字
	 */
	@RequestMapping(value = "reName", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void reName(String srcName, String destName) {
		File src = new File(upDownloader.getAbsolutePath(srcName));
		File dest = new File(upDownloader.getAbsolutePath(destName));
		if (src.exists()) {
			src.renameTo(dest);
		}
	}
	
	/**
	 * 删除目录或文件
	 * @param filename
	 */
	@RequestMapping(value = "delete", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void delete(String filename) {
		String absolutePath = upDownloader.getAbsolutePath(filename);
		upDownloader.deleteDir(absolutePath);
	}
	

	@RequestMapping(value = "resource", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String uploadFile(@RequestPart("path") String path, @RequestPart("file") Part file) throws IOException {
		String dir = URLDecoder.decode(path, "UTF-8");
		String fullname, filename = file.getSubmittedFileName();
		if (dir.endsWith("/")) {
			fullname = dir + filename;
		} else {
			fullname = dir + '/' + filename;
		}
		upDownloader.upload(fullname, file);
		return filename + ": 上传成功!";
	}
	
	@RequestMapping(value = "loadText", method = POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public String loadText(@RequestParam(value = "path", required = true) String path
			, @RequestParam(value = "charset", required = false, defaultValue = "UTF-8") String charset) {
		return textUtil.getText(upDownloader.getAbsolutePath(path), charset);
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
