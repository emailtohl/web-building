package com.github.emailtohl.building.site.dao.cms;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.cms.Type;

/**
 * 文章类型的数据访问接口
 * @author HeLei
 * @date 2017.02.17
 */
public interface TypeRepository extends JpaRepository<Type, Long> {

}
