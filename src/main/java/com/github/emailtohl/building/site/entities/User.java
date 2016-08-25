package com.github.emailtohl.building.site.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

/**
 * Entity 用户
 * @author Helei
 */
@Entity
@Table(name = "t_user")
@Access(AccessType.PROPERTY) // 实际上这就是默认的配置
//指定继承的映射策略，所有继承树上的实体共用一张表：SINGLE_TABLE，这是默认值
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//定义辨别者列的列名为“user_type”，列类型是字符串
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
//指定User实体对应的记录在辨别者列的值是“user”
@DiscriminatorValue("user") // 若不注解，则默认使用实体名
public class User extends BaseEntity {
	private static final long serialVersionUID = -2648409468140926726L;
	public enum Gender {
		MALE, FEMALE, UNSPECIFIED
	}
	protected String name;
	protected String username;
	@NotNull// 校验
	@Pattern(// 校验
		regexp = "^[a-z0-9`!#$%^&*'{}?/+=|_~-]+(\\.[a-z0-9`!#$%^&*'{}?/+=" +
		"|_~-]+)*@([a-z0-9]([a-z0-9-]*[a-z0-9])?)+(\\.[a-z0-9]" +
		"([a-z0-9-]*[a-z0-9])?)*$",
		flags = {Pattern.Flag.CASE_INSENSITIVE}
	)
	protected String email;
	protected String address;
	protected String telephone;
	@Pattern(regexp = "^[^\\s&\"<>]+$")
	protected transient String password;
	protected Boolean enabled;
	@Past// 校验，日期相对于当前较早
	protected Date birthday;
	protected String icon;
	@Min(value = 1)
	@Max(value = 150)
	protected Integer age;
	protected Gender gender;
	protected transient byte[] pic;
	protected String description;
	protected Set<Role> roles = new HashSet<Role>();
	@Valid protected Subsidiary subsidiary;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Column(nullable = false, unique = true)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
    public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	@Column(nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	// 枚举存入数据库默认为序号，这里指明将枚举名以字符串存入数据库
	@Enumerated(EnumType.STRING)
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	@Lob
	public byte[] getPic() {
		return pic;
	}
	public void setPic(byte[] pic) {
		this.pic = pic;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "t_user_role"
	, joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	@Embedded
	/*嵌入属性的映射可在嵌入实体中声明，不必要在此覆盖
	@AttributeOverrides({
		@AttributeOverride(name = "city", column = @Column(name = "subsidiary_city")),
		@AttributeOverride(name = "province", column = @Column(name = "subsidiary_province"))
	})*/
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}
	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", username=" + username + ", email=" + email + ", address=" + address
				+ ", telephone=" + telephone + ", enabled=" + enabled + ", birthday=" + birthday + ", icon=" + icon
				+ ", age=" + age + ", gender=" + gender + ", description=" + description + ", roles=" + roles
				+ ", subsidiary=" + subsidiary + ", id=" + id + ", createDate=" + createDate + ", modifyDate="
				+ modifyDate + "]";
	}
}
