package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.entity.BaseEntity;
import com.github.emailtohl.building.common.utils.UpDownloader;
import com.github.emailtohl.building.site.dto.ForumPostDto;
import com.github.emailtohl.building.site.service.ForumPostService;
/**
 * 论坛控制器
 * @author HeLei
 * @date 2017.02.04
 */
@RestController
@RequestMapping("forum")
public class ForumPostCtrl {
	private static final Logger logger = LogManager.getLogger();
	public static final String IMAGE_DIR = "image_dir";
	private UpDownloader upDownloader;
	@Inject ForumPostService forumPostService;
	@Inject File resourcePath;
	
	@PostConstruct
	public void createIconDir() {
		File f = new File(resourcePath, IMAGE_DIR);
		if (!f.exists()) {
			f.mkdir();
		}
		upDownloader = new UpDownloader(f);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void add(@RequestBody @Valid ForumPostDto form, Errors e) {
		if (e.hasErrors()) {
			for (ObjectError oe : e.getAllErrors()) {
				logger.info(oe);
			}
			return;
		}
		this.forumPostService.save(form.getTitle(), form.getKeywords(), form.getBody());
	}
	
	@RequestMapping(value = "search", method = RequestMethod.GET)
	public Pager<ForumPostDto> search(@RequestParam String query,
			@PageableDefault(page = 0, size = 5, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
//		演示时使用完全查询，因为Hibernate Search FullTextQuery getResultSize never matches getResultList().size()
//		return this.forumPostService.find(query, pageable);
		return this.forumPostService.findAllAndPaging(query, pageable);
	}

	@RequestMapping(value = "pager", method = RequestMethod.GET)
	Pager<ForumPostDto> searchPager(@PageableDefault(page = 0, size = 5, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		return this.forumPostService.getPager(pageable);
	}
	
	/**
	 * 特殊情况，管理员删除论坛帖子
	 * @param id
	 */
	@RequestMapping(value = "{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") @Min(1L) long id) {
		this.forumPostService.delete(id);
	}
	
	private static volatile int IMAGE_ID = 0;
	/**
	 * 上传图片,针对前端CKEditor接口编写的控制器方法
	 * @param image
	 * @return 返回一个CKEditor识别的回调函数
	 * @throws IOException
	 */
	@RequestMapping(value = "image", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void uploadImage(@RequestParam("CKEditorFuncNum") String CKEditorFuncNum/* 回调显示图片的位置 */, 
			@RequestPart("upload") Part image
			, HttpServletResponse response) throws IOException {
		LocalDate date = LocalDate.now();
		String dir = IMAGE_DIR + File.separator + date.getYear() + File.separator + date.getDayOfYear();
		File fdir = new File(upDownloader.getAbsolutePath(dir));
		if (!fdir.exists()) {
			fdir.mkdirs();
		}
		String html;
		String absolutePath = null;
		short count = 1;
		do {
			try {
				String imageName = dir + File.separator + (++IMAGE_ID) + "_" + image.getSubmittedFileName();
				absolutePath = upDownloader.upload(imageName, image);
			} catch (IllegalArgumentException e) {
				logger.debug("文件重名，重命名后再保存", e);
				count++;
			}
		} while (absolutePath == null && count < 5);
		if (absolutePath == null) {
			// 第三个参数为空表示没有错误，不为空则会弹出一个对话框显示　error　message　的内容
			html = "<script type=\"text/javascript\">window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ",'','上传的文件名冲突');</script>";
		} else {
			String url = absolutePath = UpDownloader.getRelativeRootURL(absolutePath, resourcePath.getAbsolutePath());
			html = "<script type=\"text/javascript\">window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum + ",'" + url + "','');</script>";
		}
		response.addHeader("X-Frame-OPTIONS", "SAMEORIGIN");
		response.setContentType("text/html; charset=utf-8");  
        PrintWriter out = response.getWriter();
        out.println(html);
        out.close();
	}
	
	public void setForumPostService(ForumPostService forumPostService) {
		this.forumPostService = forumPostService;
	}

	public void setUpDownloader(UpDownloader upDownloader) {
		this.upDownloader = upDownloader;
	}
	
}
