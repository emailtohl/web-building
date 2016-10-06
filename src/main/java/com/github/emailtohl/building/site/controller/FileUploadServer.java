package com.github.emailtohl.building.site.controller;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.emailtohl.building.common.utils.ServletUtils;
import com.github.emailtohl.building.exception.VerifyFailure;
import com.github.emailtohl.building.site.controller.form.UserForm;
/**
 * 文件上传控制器
 * @author HeLei
 */
@Controller
@RequestMapping("fileUploadServer")
public class FileUploadServer {
	private static final Logger logger = LogManager.getLogger();
	
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
			@Valid UserForm user,
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
		String result = ServletUtils.multipartOnload(request, "upload/");
		return result;
	}
}
