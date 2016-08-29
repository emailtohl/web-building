package com.github.emailtohl.building.common.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 未归类的方法
 * @author Helei
 */
public final class JavaBeanTools {
	private static final Logger logger = LogManager.getLogger();
	private JavaBeanTools() {}

	/**
	 * 本方法分析对象bean，将其属性名和属性值转存到map中
	 * 注意，若导出类覆盖了基类的属性，则只存储导出类的属性，此外包括静态属性
	 * 
	 * @param bean 被分析的对象
	 * @return 一个map，key是bean的属性名，value是bean的属性值
	 */
	public static Map<String, Object> getFieldMap(Object bean) {
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> clz = bean.getClass();
		while (clz != Object.class) {
			Field[] fields = clz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isSynthetic())// 若是内部类连接外围类的引用，则忽略
					continue;
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				if (map.containsKey(name)) // 若导出类覆盖了基类属性，只取导出类的值
					continue;
				try {
					map.put(name, fields[i].get(bean));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			clz = clz.getSuperclass();
		}
		return map;
	}
	
	/**
	 * 本方法分析对象bean，将其Property名和PropertyDescriptor转存到map中
	 * @param bean 遵循JavaBean的对象
	 * @return 一个Map，key是Property名，value是JavaBean的PropertyDescriptor
	 */
	public static Map<String, PropertyDescriptor> propertyMap(Object bean) {
		Map<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();
		try {
			for (PropertyDescriptor prop : Introspector.getBeanInfo(bean.getClass(), Object.class)
					.getPropertyDescriptors()) {
				map.put(prop.getName(), prop);
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 本方法分析对象bean，将其Field名和Field转存到map中
	 * 注意，若导出类覆盖了基类的属性，则只存储导出类的属性，此外包括静态属性
	 * 
	 * @param bean 被分析的对象
	 * @return 一个map，key是bean的Field名，value是bean的Field
	 */
	public static Map<String, Field> fieldMap(Object bean) {
		Map<String, Field> map = new HashMap<String, Field>();
		Class<?> clz = bean.getClass();
		while (clz != Object.class) {
			Field[] fields = clz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isSynthetic())// 若是内部类连接外围类的引用，则忽略
					continue;
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				if (map.containsKey(name)) // 若导出类覆盖了基类属性，只取导出类的值
					continue;
				try {
					map.put(name, fields[i]);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			clz = clz.getSuperclass();
		}
		return map;
	}

	/**
	 * 从继承层次来看，根据属性名获取Field可能会抛已检查的异常，这样会影响调用它的程序的运行路径
	 * 本方法简单地封装了Class类中的getDeclaredField()方法，分析宿主对象整个继承层次结构，如果查找到了此Field则返回它，否则返回null
	 * 
	 * 注意：如果导出类的存在属性覆盖超类的情况，则返回导出类的Field
	 * 
	 * @param obj 获取Field的宿主对象
	 * @param fieldName 查找的属性名
	 * @return 返回该宿主对象的Field，如果未查找到则返回null
	 */
	public static Field getDeclaredField(Object obj, String fieldName) {
		if (obj == null || fieldName == null)
			return null;
		Field f = null;
		Class<?> clz = obj.getClass();
		do {
			try {
				f = clz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException | SecurityException e) {
				// e.printStackTrace();
			}
			if (f == null) {
				clz = clz.getSuperclass();
			} else {
				break;
			}
		} while (clz != null);
		return f;
	}

	/**
	 * 本方法分析源bean的属性，复制到目标对象中
	 * 注意：
	 * 1.目标对象必须提供无参构造器；
	 * 2.本方法不会复制static、final属性
	 * 
	 * @param <T> 目标对象的类型
	 * @param <S> 源对象的类型
	 * @param targetObjectClass 目标对象的Class
	 * @param srcBean 源对象
	 * @return 目标对象
	 */
	public static <S, T> T copyProperties(S srcBean, Class<T> targetObjectClass) {
		if (targetObjectClass == null || srcBean == null)
			return null;
		Map<String, Object> map = getFieldMap(srcBean);
		T obj = null;
		try {
			obj = targetObjectClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("实例化目标对象失败，检查此类是否有无参构造器", e);
		}
		Class<? super T> superClass = targetObjectClass;
		while (superClass != Object.class) {
			Field[] fields = superClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				int mod = fields[i].getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod))
					continue;
				Object value = map.get(fields[i].getName());// 若是基本类型这一步会对其自动包装
				// 若有导出类属性覆盖基类属性的情况，也一并注入进去，似乎没多大影响
				injectField(fields[i], obj, value);
			}
			superClass = superClass.getSuperclass();
		}
		return obj;
	}

	/**
	 * 将源List中的bean复制到新生成的List中
	 * 1.目标对象必须提供无参构造器；
	 * 2.本方法不会复制static、final属性
	 * 
	 * @param <T> 目标对象的类型
	 * @param <S> 源对象的类型
	 * @param sourceList 源对象列表
	 * @param targetObjectClass 被注入数据的bean的类
	 * @return 返回目标对象列表
	 */
	public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetObjectClass) {
		List<T> targetList = new ArrayList<T>();
		for (S sourceObject : sourceList) {
			T targetObject = copyProperties(sourceObject, targetObjectClass);
			targetList.add(targetObject);
		}
		return targetList;
	}

	/**
	 * 利用对象序列化机制，对整个对象网络进行深度复制
	 * 
	 * @param src 源对象
	 * @return 克隆的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deepCopy(T src) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bout);
			oos.writeObject(src);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
			return (T) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			logger.warn("检查是否加载了类文件", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 比对对象属性，分析修改前后哪些属性改变了
	 * 注意：比对的属性时使用的是equals方法，String、Integer等没问题，若是自定义对象，需要重写equals和hashcode方法
	 * 
	 * @param preObj 之前的对象
	 * @param afterObj 修改后的对象
	 * @return 返回一个map，key是Field名，value是修改后的值
	 */
	public static <T> Map<String, Object> getModifiedField(T preObj, T afterObj) {
		if (preObj == null || afterObj == null)
			throw new IllegalArgumentException("比较对象为null");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Map<String, Object> preMap = getFieldMap(preObj);
		Map<String, Object> afterMap = getFieldMap(afterObj);
		for (Entry<String, Object> entry : afterMap.entrySet()) {// 一般应用于操作记录，所以以更改后的为准
			String key = entry.getKey();
			Object afterValue = entry.getValue();
			Object preValue = preMap.get(key);
			if (afterValue == null) {
				if (preValue != null) {
					map.put(key, null);
				}
			} else if (!afterValue.equals(preValue)) {
				map.put(key, afterValue);
			}
		}
		return map;
	}

	/**
	 * 本方法将对象注入到Field中，Field可以是基本数据类型，但对象应该是其包装类
	 * 
	 * 注意：如果注入的对象为null，且Field是基本类型，则设置为其初始值，例如int的初始值为0
	 * 
	 * @param field 被注入的Field
	 * @param bean 该Field所在的对象
	 * @param value 注入的对象
	 */
	public static void injectField(Field field, Object bean, Object value) {
		if (field == null || bean == null)
			throw new IllegalArgumentException("field或bean为null");
		field.setAccessible(true);
		Class<?> type = field.getType();
		try {
			if (value == null) {
				if (type == int.class) {
					field.setInt(bean, 0);
				} else if (type == long.class) {
					field.setLong(bean, 0L);
				} else if (type == double.class) {
					field.setDouble(bean, 0.0d);
				} else if (type == float.class) {
					field.setFloat(bean, 0.0f);
				} else if (type == short.class) {
					field.setShort(bean, (short) 0);
				} else if (type == boolean.class) {
					field.setBoolean(bean, false);
				} else if (type == byte.class) {
					field.setByte(bean, (byte) 0);
				} else if (type == char.class) {
					field.setChar(bean, (char) 0);
				} else if (type == void.class) {
				} else {
					field.set(bean, null);
				}
			} else {
				if (type.isInstance(value)) {
					field.set(bean, value);
				} else if (value instanceof Number) {
					Number num = (Number) value;
					if (type == int.class) {
						field.setInt(bean, num.intValue());
					} else if (type == long.class) {
						field.setLong(bean, num.longValue());
					} else if (type == double.class) {
						field.setDouble(bean, num.doubleValue());
					} else if (type == float.class) {
						field.setFloat(bean, num.floatValue());
					} else if (type == short.class) {
						field.setShort(bean, num.shortValue());
					} else if (type == byte.class) {
						field.setByte(bean, num.byteValue());
					} else if (type == Byte.class) {
						field.set(bean, num.byteValue());
					} else if (type == char.class) {
						field.setChar(bean, (char) num.intValue());
					} else if (type == Character.class) {
						field.set(bean, (char) num.intValue());
					}
				} else if (type == boolean.class && value instanceof Boolean) {
					field.setBoolean(bean, ((Boolean) value).booleanValue());
				} else if (type == byte.class && value instanceof Byte) {
					field.setByte(bean, ((Byte) value).byteValue());
				} else if (type == char.class && value instanceof Character) {
					field.setChar(bean, ((Character) value).charValue());
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 本方法主要用于将HttpRequest携带的参数注入进bean对象对应的属性中，也可以用于类型不匹配的注入
	 * 由于HttpRequest的参数值是String，而bean对象中的属性不一定是String，所以根据具体类型进行转换
	 * 
	 * 注意：如果注入的对象为null，且Field是基本类型，则设置为其初始值，例如int的初始值为0
	 * 
	 * @param field bean对象中的某个属性对象
	 * @param bean bean对象实例
	 * @param value 要注入的String值
	 */
	public static void injectFieldWithString(Field field, Object bean, String value) {
		if (field == null || bean == null)
			throw new IllegalArgumentException("field或bean为null");
		String className = field.getType().getName();
		try {
			field.setAccessible(true);
			if (value == null || value.trim().length() == 0) {
				switch (className) {
				case "int":
					field.setInt(bean, 0);
					break;
				case "short":
					field.setShort(bean, (short) 0);
					break;
				case "long":
					field.setLong(bean, 0L);
					break;
				case "float":
					field.setFloat(bean, 0.0f);
					break;
				case "double":
					field.setDouble(bean, 0.0d);
					break;
				case "boolean":
					field.setBoolean(bean, false);
					break;
				case "byte":
					field.setByte(bean, (byte) 0);
					break;
				case "char":
					field.setChar(bean, (char) 0);
					break;
				case "void":
					break;
				case "java.lang.String":
					field.set(bean, value);
					break;
				default:
					field.set(bean, null);
				}
			} else {
				value = value.trim();
				switch (className) {
				case "java.lang.String":
					field.set(bean, value);
					break;
				case "java.lang.Long":
					field.set(bean, Long.parseLong(value));
					break;
				case "java.lang.Integer":
					field.set(bean, Integer.parseInt(value));
					break;
				case "java.lang.Double":
					field.set(bean, Double.parseDouble(value));
					break;
				case "java.util.Date":
					field.set(bean, Date.valueOf(value));
					break;
				case "java.sql.Time":
					field.set(bean, Time.valueOf(value));
					break;
				case "java.sql.Date":
					field.set(bean, Date.valueOf(value));
					break;
				case "java.sql.Timestamp":
					field.set(bean, Timestamp.valueOf(value));
					break;
				case "java.math.BigDecimal":
					field.set(bean, new BigDecimal(value));
					break;
				case "java.math.BigInteger":
					field.set(bean, new BigInteger(value));
					break;
				case "int":
					field.setInt(bean, Integer.parseInt(value));
					break;
				case "short":
					field.setShort(bean, Short.parseShort(value));
					break;
				case "long":
					field.setLong(bean, Long.parseLong(value));
					break;
				case "float":
					field.setFloat(bean, Float.parseFloat(value));
					break;
				case "double":
					field.setDouble(bean, Double.parseDouble(value));
					break;
				case "boolean":
					field.setBoolean(bean, Boolean.parseBoolean(value));
					break;
				case "byte":
					field.setByte(bean, Byte.parseByte(value));
					break;
				case "char":
					char[] c = value.toCharArray();
					if (c.length > 0)
						field.setChar(bean, c[0]);
					break;
				default:
					Class<?> et = field.getType();// 查看该属性是否枚举
					if (et.isEnum()) {
						for (Object en : et.getEnumConstants()) {
							Enum<?> enu = (Enum<?>) en;
							if (value.equals(enu.name())) {
								field.set(bean, enu);
								break;
							}
						}
					} else {
						logger.warn("未找到匹配类型，不做处理");
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.warn("数字解析错误，此字段注入失败");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	  * 本方法主要用于将HttpRequest携带的参数注入进bean对象对应的属性中，也可以用于类型不匹配的注入
	  * 由于HttpRequest的参数值是String，而bean对象中的属性不一定是String，所以根据具体类型进行转换
	  * 
	  * 注意：如果注入的对象为null，且Field是基本类型，则设置为其初始值，例如int的初始值为0
	  * 
	  * @param pd 属性描述器
	  * @param bean 标准JavaBean
	  * @param value 字符串值
	  */
	public static void injectPropertyWithString(PropertyDescriptor pd, Object bean, String value) {
		String className = pd.getPropertyType().getName();
		Method m = pd.getWriteMethod();
		try {
			if (value == null || value.trim().length() == 0) {
				switch (className) {
				case "int":
					m.invoke(bean, 0);
					break;
				case "short":
					m.invoke(bean, (short) 0);
					break;
				case "long":
					m.invoke(bean, 0L);
					break;
				case "float":
					m.invoke(bean, 0f);
					break;
				case "double":
					m.invoke(bean, 0d);
					break;
				case "boolean":
					m.invoke(bean, false);
					break;
				case "byte":
					m.invoke(bean, (byte) 0);
					break;
				case "char":
					m.invoke(bean, (char) 0);
					break;
				case "void":
					break;
				case "java.lang.String":
					m.invoke(bean, value);
					break;
				default:
					m.invoke(bean, new Object[] { null });
				}
			} else {
				value = value.trim();
				switch (className) {
				case "java.lang.String":
					m.invoke(bean, value);
					break;
				case "java.lang.Long":
					m.invoke(bean, Long.parseLong(value));
					break;
				case "java.lang.Integer":
					m.invoke(bean, Integer.parseInt(value));
					break;
				case "java.lang.Double":
					m.invoke(bean, Double.parseDouble(value));
					break;
				case "java.util.Date":
					m.invoke(bean, Date.valueOf(value));
					break;
				case "java.sql.Time":
					m.invoke(bean, Time.valueOf(value));
					break;
				case "java.sql.Date":
					m.invoke(bean, Date.valueOf(value));
					break;
				case "java.sql.Timestamp":
					m.invoke(bean, Timestamp.valueOf(value));
					break;
				// java8新加的时间类型
				case "java.time.Instant":
					m.invoke(bean, Instant.parse(value));
					break;
				case "java.time.LocalDate":
					m.invoke(bean, LocalDate.parse(value));
					break;
				case "java.time.LocalTime":
					m.invoke(bean, LocalTime.parse(value));
					break;
				case "java.time.ZonedDateTime":
					m.invoke(bean, ZonedDateTime.parse(value));
					break;
				case "java.time.YearMonth":
					m.invoke(bean, YearMonth.parse(value));
					break;
				case "java.time.MonthDay":
					m.invoke(bean, MonthDay.parse(value));
					break;
				case "java.math.BigDecimal":
					m.invoke(bean, new BigDecimal(value));
					break;
				case "java.math.BigInteger":
					m.invoke(bean, new BigInteger(value));
					break;
				case "int":
					m.invoke(bean, Integer.parseInt(value));
					break;
				case "short":
					m.invoke(bean, Short.parseShort(value));
					break;
				case "long":
					m.invoke(bean, Long.parseLong(value));
					break;
				case "float":
					m.invoke(bean, Float.parseFloat(value));
					break;
				case "double":
					m.invoke(bean, Double.parseDouble(value));
					break;
				case "boolean":
					m.invoke(bean, Boolean.parseBoolean(value));
					break;
				case "byte":
					m.invoke(bean, Byte.parseByte(value));
					break;
				case "char":
					char[] c = value.toCharArray();
					if (c.length > 0)
						m.invoke(bean, c[0]);
					break;
				default:
					Class<?> et = pd.getPropertyType();// 查看该属性是否枚举
					if (et.isEnum()) {
						for (Object en : et.getEnumConstants()) {
							Enum<?> enu = (Enum<?>) en;
							if (value.equals(enu.name())) {
								m.invoke(bean, enu);
								break;
							}
						}
					} else {
						System.err.println("未找到匹配类型，不做处理");
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.err.println("数字解析错误，此属性注入失败");
		}
	}
	
	/**
	 * 将相同JavaBean的属性进行合并
	 * @param dest 合并的目标
	 * @param src 若src中的属性不为null，则合并到目标对象中
	 * @return 返回合并后的对象
	 */
	@SafeVarargs
	public static <T> T merge(T dest, Object... srcs) {
		Map<String, PropertyDescriptor> destMap = propertyMap(dest);
		for (Object t : srcs) {
			Map<String, PropertyDescriptor> srcMap = propertyMap(t);
			for (Entry<String, PropertyDescriptor> entry : srcMap.entrySet()) {
				String name = entry.getKey();
				Class<?> type = null;
				Object value = null;
				try {
					type = entry.getValue().getPropertyType();
					value = entry.getValue().getReadMethod().invoke(t, new Object[] {});
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
				if (value != null && type != null && destMap.containsKey(name)) {
					PropertyDescriptor pd = destMap.get(name);
					if (pd.getPropertyType().isAssignableFrom(type)) {
						try {
							pd.getWriteMethod().invoke(dest, value);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return dest;
	}

	/**
	 * 程序中经常有比较List中po的场景，比较优良的实践是先将一个List中的po转存到一个HashMap中，这样根据主键获取DTO的效率接近常量级别
	 * 本方法将List中的对象转存到一个HashMap中，并把该对象的主键属性作为该HashMap的key
	 * 注意:
	 * 1.确定List中的对象拥有一个主键的属性
	 * 2.主键不能在List中的对象的超类中
	 * 
	 * @param <K> 主键的类型
	 * @param <O> bean对象的类型
	 * @param list 被操作的List实例
	 * @param keyName ValueType实例中作为主键的属性名
	 * @return 返回一个Map，键是bean对象中被注解为key的值，值是该bean对象
	 */
	public static <K extends Serializable, O> Map<K, O> saveListToMap(List<O> list, String keyName) {
		if (list == null || keyName == null)
			return null;
		Map<K, O> map = new HashMap<K, O>();
			for (O obj : list) {
				Field keyField = getDeclaredField(obj, keyName);
				keyField.setAccessible(true);
				try {
					@SuppressWarnings("unchecked")
					K key = (K) keyField.get(obj);
					map.put(key, obj);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		return map;
	}
	
	private static final Pattern genericPattern = Pattern.compile("<(.+)>");

	/**
	 * 从Field中获取泛型的实际Class
	 * 
	 * @param f
	 * @return
	 */
	public static Class<?>[] getGenericClass(Field f) {
		List<Class<?>> ls = new ArrayList<Class<?>>();
		Matcher m = genericPattern.matcher(f.getGenericType().toString());
		if (m.find()) {
			for (String className : m.group(1).split(",")) {
				try {
					ls.add(Class.forName(className.trim()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		Class<?>[] cs = new Class<?>[ls.size()];
		return ls.toArray(cs);
	}

	/**
	 * 从属性描述器中获取泛型的实际Class
	 * 
	 * @param p
	 * @return
	 */
	public static Class<?>[] getGenericClass(PropertyDescriptor p) {
		List<Class<?>> ls = new ArrayList<Class<?>>();
		Method method = p.getReadMethod();
		if (method == null) {
			method = p.getWriteMethod();
		}
		Matcher m = genericPattern.matcher(method.toGenericString());
		if (m.find()) {
			for (String className : m.group(1).split(",")) {
				try {
					ls.add(Class.forName(className.trim()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		Class<?>[] cs = new Class<?>[ls.size()];
		return ls.toArray(cs);
	}

	/**
	 * 程序中经常有比较两对象的属性值是否一致，以判断对象是否相等，例如数据库表同步场景中 用户可以覆写equals()/hashCode()进行比较
	 * 本方法以基准对象为准，比较相对对象中对应属性字段中的值，如若全部匹配，则返回true，否则返回false
	 * 
	 * 注意：本方法仅仅只进行浅层次的属性比较，且比较的属性对象有自己的equals()/hashCode()方法，例如String、Long、 Integer等
	 * 
	 * @param baseObj 基准对象，以此对象为准
	 * @param comparePropertyNames 指出需要做比较的属性名
	 * @param relativelyObj 相对对象
	 * @return 如若所有属性字段比对成功则返回true，否则返回false
	 */
	public static boolean compareProperties(Object baseObj, String[] comparePropertyNames, Object relativelyObj) {
		if (baseObj == null || comparePropertyNames == null || relativelyObj == null)
			throw new IllegalArgumentException("做比较的对象是null");// NullPointerException一般由虚拟机抛出，IllegalArgumentException定位问题更清晰
		List<String> list = Arrays.asList(comparePropertyNames);
		Map<String, Object> map = getFieldMap(baseObj);
		Set<String> set = map.keySet();
		set.retainAll(list);// 只保留需要做比较的属性
		boolean flag = true;
		for (Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			Object baseValue = null, relativelyValue = null;
			baseValue = entry.getValue();
			Field field = getDeclaredField(relativelyObj, name);
			if (field == null) {
				throw new IllegalArgumentException("传入的relativelyObj的属性与baseObj的属性不匹配");
			}
			field.setAccessible(true);
			try {
				relativelyValue = field.get(relativelyObj);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (baseValue == null) {
				if (relativelyValue != null) {
					flag = false;
					break;
				}
			} else {
				if (!baseValue.equals(relativelyValue)) {
					flag = false;
					break;
				}
			}
		}
		return flag;
	}
}
