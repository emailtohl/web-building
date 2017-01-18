package com.github.emailtohl.building.site.controller;

import java.io.File;
import java.io.IOException;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.common.utils.ServletUtil;
import com.github.emailtohl.building.common.utils.UpDownloader;
import com.github.emailtohl.building.common.ztree.ZtreeNode;
import com.github.emailtohl.building.exception.VerifyFailure;
import com.github.emailtohl.building.site.dto.UserDto;
/**
 * 文件上传控制器
 * @author HeLei
 */
@Controller
@RequestMapping("fileUploadServer")
public class FileUploadServer {
	private static final Logger logger = LogManager.getLogger();
	public static final String FILE_DIR = "file_dir";
	private File root;
	@Inject UpDownloader upDownloader;
	
	@PostConstruct
	public void createIconDir() {
		root = new File(upDownloader.getAbsolutePath(FILE_DIR));
		if (!root.exists()) {
			root.mkdir();
		}
	}
	
	@RequestMapping(value = "fileRoot", method = RequestMethod.GET)
	@ResponseBody
	public ZtreeNode getRoot() {
		return ZtreeNode.newInstance(root);
	}
	
	@RequestMapping(value = "dir", method = RequestMethod.POST)
	@ResponseBody
	public void createDir(@RequestBody String dirName) {
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
	@RequestMapping(value = "dir/{srcName}", method = RequestMethod.PUT)
	@ResponseBody
	public void reName(@PathVariable("srcName") String srcName, @RequestBody String destName) {
		File src = new File(upDownloader.getAbsolutePath(srcName));
		File dest = new File(upDownloader.getAbsolutePath(destName));
		if (src.exists()) {
			src.renameTo(dest);
		}
	}
	
	@RequestMapping(value = "dir/{dirName}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDir(@PathVariable("dirName") String dirName) {
		String absolutePath = upDownloader.getAbsolutePath(dirName);
		upDownloader.deleteDir(absolutePath);
	}
	

	@RequestMapping(value = "dir", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
	@ResponseBody
	public void uploadFile(@RequestBody String dir, @RequestPart("part") Part part) throws IOException {
		upDownloader.upload(dir, part);
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
	@RequestMapping(value = "test", method = RequestMethod.POST, produces = "text/plain; charset=utf-8")
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
		String result = ServletUtil.multipartOnload(request, "upload/");
		return result;
	}
}
