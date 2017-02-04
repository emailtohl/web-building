package com.github.emailtohl.building.site.dao.impl;

import com.github.emailtohl.building.common.jpa.fullTextSearch.AbstractSearchableRepository;
import com.github.emailtohl.building.common.jpa.fullTextSearch.SearchableRepository;
import com.github.emailtohl.building.site.entities.ForumPost;
/**
 * 论坛模块的数据源
 * @author HeLei
 * @date 2017.02.04
 */
public class ForumPostRepositoryImpl extends AbstractSearchableRepository<ForumPost>
		implements SearchableRepository<ForumPost> {
}
