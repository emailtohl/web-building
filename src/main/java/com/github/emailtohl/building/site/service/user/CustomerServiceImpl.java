package com.github.emailtohl.building.site.service.user;

import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.CREATE_DATE_PROPERTY_NAME;
import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.ID_PROPERTY_NAME;
import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.MODIFY_DATE_PROPERTY_NAME;
import static com.github.emailtohl.building.common.jpa.entity.BaseEntity.VERSION_PROPERTY_NAME;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.dao.user.CustomerRepository;
import com.github.emailtohl.building.site.entities.user.Customer;
/**
 * 客户管理（CRM）服务的实现
 * @author HeLei
 * @date 2017.02.04
 */
@Service
public class CustomerServiceImpl implements CustomerService {
	@Inject CustomerRepository customRepository;

	@Override
	public Pager<Customer> query(String name, String title, String affiliation, Pageable pageable) {
		Page<Customer> page = customRepository.query(StringUtils.hasText(name) ? name.trim() + '%' : name, 
				StringUtils.hasText(title) ? title.trim() + '%' : title, 
				StringUtils.hasText(affiliation) ? affiliation.trim() + '%' : affiliation, 
				pageable);
		List<Customer> ls = page.getContent().parallelStream().map((p/*持久化*/ -> {
			Customer t = new Customer();// 瞬时
			BeanUtils.copyProperties(p, t, "password", "icon", "roles");
			return t;
		})).collect(Collectors.toList());
		
		return new Pager<Customer>(ls, page.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
	}

	@Override
	public Customer getCustomer(Long id) {
		Customer t = new Customer();// 瞬时
		Customer p = customRepository.getCustomer(id);// 持久化
		if (p != null) {
			BeanUtils.copyProperties(p, t, "password", "icon", "roles");
		}
		return t;
	}

	@Override
	public void update(Long id, Customer customer) {
		Customer c = customRepository.getCustomer(id);
		BeanUtils.copyProperties(customer, c, ID_PROPERTY_NAME, CREATE_DATE_PROPERTY_NAME, MODIFY_DATE_PROPERTY_NAME, VERSION_PROPERTY_NAME, "email", "username", "roles", "password", "enabled");
	}

	@Override
	public Workbook getCustomerExcel() {
		List<Customer> ls = customRepository.findAll();
		Workbook wb = new HSSFWorkbook();
		// Workbook wb = new XSSFWorkbook();
//		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet("sheet1");

		// Create a row and put some cells in it. Rows are 0 based.
		Row heads = sheet.createRow(0);
		setRowData(heads, new String[] {"客户名", "电话", "邮箱", "职务", "组织", "地址", "城市", "省份", "国别"});
		
		int i = 1;
		for (Customer c : ls) {
			Row row = sheet.createRow(i);
			setRowData(row, new String[] {c.getName(), c.getTelephone(), c.getEmail(), c.getTitle(), c.getAffiliation(), c.getAddress(),
					c.getSubsidiary() == null ? "" : c.getSubsidiary().getCity(), 
					c.getSubsidiary() == null ? "" : c.getSubsidiary().getProvince(),
					c.getSubsidiary() == null ? "" : c.getSubsidiary().getCountry()
			});
			i++;
		}

		// Write the output to a file
		// wb.write(out);
		return wb;
	}

	@Override
	public List<Customer> findAll() {
		return customRepository.findAll();
	}
	
	private void setRowData(Row row, String[] data) {
		for (int i = 0; i < data.length; i++) {
			row.createCell(i).setCellValue(data[i]);
		}
	}
	
}
