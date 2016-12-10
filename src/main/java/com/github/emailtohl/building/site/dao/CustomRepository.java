package com.github.emailtohl.building.site.dao;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.Customer;
import com.github.emailtohl.building.site.entities.ApplicationForm.Status;

/**
 * 只查询客户的数据访问接口
 * @author HeLei
 */
public interface CustomRepository extends JpaRepository<Customer, Long> {
	/**
	 * 根据客户的真实名字进行查询
	 * @param title
	 * @return
	 */
	Page<Customer> findByNameLike(String name);
	/**
	 * 根据客户的邮箱查询
	 * @param name
	 * @return
	 */
	Page<Customer> findByEmailLike(String name);
	/**
	 * 根据客户职位进行查询
	 * @param title
	 * @return
	 */
	Page<Customer> findByTitleLike(String title);
	/**
	 * 根据客户的公司进行查询
	 * @param title
	 * @return
	 */
	Page<Customer> findByAffiliationLike(String title);
	
	/**
	 * 根据用户名和公司进行组合查询
	 * @param title
	 * @param affiliation
	 * @param pageable
	 * @return
	 */
	Page<ApplicationHandleHistory> findByTitleLikeAndAffiliationLike(Status title, Date affiliation, Pageable pageable);

}
