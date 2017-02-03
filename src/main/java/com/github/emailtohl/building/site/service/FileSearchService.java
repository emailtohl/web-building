package com.github.emailtohl.building.site.service;

import java.util.Set;

/**
 * 对文件系统的文本内容进行索引和搜索
 * @author HeLei
 */
public interface FileSearchService {
	/**
	 * 为文件目录创建索引
	 * @param filePath
	 */
	void index(String filePath);
	/**
	 * 根据查询语句获取文件的路径
	 * @param query
	 * @return
	 */
	Set<String> queryPath(String queryString);
}
