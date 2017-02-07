package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
	@Inject
	ForumPostService forumPostService;
	@Inject
	UpDownloader upDownloader;
	
	@PostConstruct
	public void createIconDir() {
		File f = new File(upDownloader.getAbsolutePath(IMAGE_DIR));
		if (!f.exists()) {
			f.mkdir();
		}
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
	
	/**
	 * 上传图片
	 * @param image
	 * @return 上传文件的相对路径
	 * @throws IOException
	 */
	@RequestMapping(value = "image", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public String uploadImage(@RequestParam("CKEditor") String CKEditor, @RequestPart("upload") Part image) throws IOException {
		LocalDate date = LocalDate.now();
		String dir = IMAGE_DIR + File.separator + date.getYear() + File.separator + date.getDayOfYear();
		File fdir = new File(upDownloader.getAbsolutePath(dir));
		if (!fdir.exists()) {
			fdir.mkdirs();
		}
		String imageName = null;
		imageName = dir + File.separator + '_' + image.getSubmittedFileName();
		String path = upDownloader.upload(imageName, image);
		path = upDownloader.getRelativePath(path);
		return path;
	}

	public void setForumPostService(ForumPostService forumPostService) {
		this.forumPostService = forumPostService;
	}

	public void setUpDownloader(UpDownloader upDownloader) {
		this.upDownloader = upDownloader;
	}
	
}
