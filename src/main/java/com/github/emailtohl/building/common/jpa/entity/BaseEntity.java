package com.github.emailtohl.building.common.jpa.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * Entity 基类
 * 注意：依赖本基类的equals和hashCode方法会使你的实体对象在瞬时状态（没有id）时不能正确地存入集合（如HashSet）中
 * 
 * @author HeLei
 * @date 2017.02.04
 */
@EntityListeners(EntityListener.class)
/*
 * @MappedSuperclass 用在父类上面。
 * 当这个类肯定是父类时，加此标注。
 * 如果改成@Entity，则继承后，多个类继承，只会生成一个表，而不是多个继承，生成多个表。
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -411374988586534072L;
	/**
	 * "ID"属性名称
	 */
	public static final String ID_PROPERTY_NAME = "id";

	/**
	 * "创建日期"属性名称
	 */
	public static final String CREATE_DATE_PROPERTY_NAME = "createDate";

	/**
	 * "修改日期"属性名称
	 */
	public static final String MODIFY_DATE_PROPERTY_NAME = "modifyDate";
	
	/**
	 * "并发控制的版本号"属性名称
	 */
	public static final String VERSION_PROPERTY_NAME = "version";
	
	public static final String[] PROPERTY_NAMES = {ID_PROPERTY_NAME, CREATE_DATE_PROPERTY_NAME, MODIFY_DATE_PROPERTY_NAME, VERSION_PROPERTY_NAME};

	/** ID */
	protected Long id;

	/**
	 * 创建日期
	 */
	protected Date createDate;

	/**
	 * 修改日期
	 */
	protected Date modifyDate;
	
	/**
	 * 本字段存在的意义在于并发修改同一记录时，抛出OptimisticLockException异常提醒用户，使用的乐观锁并发控制策略
	 * 假如获取本实例时，version = 0， 在提交事务时，JPA提供程序会执行如下语句
	 * 
	 * update item set name = ?, version = 1 where id = ? and version = 0
	 * 若jdbc返回0，要么item不存在，要么不再有版本0，此时会抛javax.persistence.OptimisticLockException异常
	 * 需捕获此异常给用户适当提示。
	 */
	protected Integer version;
	
	/**
	 * 获取ID
	 * @return ID
	 */
	@org.hibernate.search.annotations.DocumentId// 全文索引id，可选，默认是@Id
	@Id
	// MySQL/SQLServer: @GeneratedValue(strategy = GenerationType.AUTO)
	// Oracle: @GeneratedValue(strategy = GenerationType.AUTO, generator = "sequenceGenerator")
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	/**
	 * 设置ID
	 * @param id ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取创建日期
	 * @return 创建日期
	 */
	@org.hibernate.search.annotations.DateBridge(resolution = org.hibernate.search.annotations.Resolution.DAY)
	@Column(nullable = false, updatable = false, name = "create_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * 设置创建日期
	 * @param createDate 创建日期
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 获取修改日期
	 * @return 修改日期
	 */
	@Column(nullable = false, name = "modify_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getModifyDate() {
		return modifyDate;
	}

	/**
	 * 设置修改日期
	 * @param modifyDate 修改日期
	 */
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
	@Version
	protected Integer getVersion() {
		return version;
	}

	protected void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * 重写hashCode方法
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * 重写equals方法
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		Class<?> thisClass = getClass(), otherClass = obj.getClass();
		// 两者都不在同一继承结构上，包括JPA提供程序生成的代理
		// 由于this是BaseEntity的实例，所以这种判断涵盖other instanceof BaseEntity
		if (!thisClass.isAssignableFrom(otherClass) && !otherClass.isAssignableFrom(thisClass))
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null || other.getId() == null) {// 注意此处不能直接访问other的字段：other.id，因为other可能是JPA提供程序生成的代理
			return false;
		} else {
			return id.equals(other.getId());
		}
	}
	
	/**
	 * Spring 的BeanUtils.copyProperties方法在复制时需要指明忽略什么属性
	 * 而本类在实体复制时往往需要忽略id，createDate，modifyDate，version的属性，因为他们是提供给JPA提供程序使用
	 * @param array
	 * @param other
	 * @return
	 */
	public static String[] getIgnoreProperties(String... other) {
		List<String> ls = new ArrayList<String>();
		for (String t : PROPERTY_NAMES) {
			ls.add(t);
		}
		for (String t : other) {
			ls.add(t);
		}
		return ls.toArray(new String[ls.size()]);
	}
}
