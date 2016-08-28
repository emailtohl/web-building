package com.github.emailtohl.building.common.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * JPA更新时，传入的Entity对象只是携带了某几个更新项，这时候不得不先将一个一个属性提出来再设置到持久化Entity属性中
 * 由于传入的Entity对象只是携带某几个更新项，具体哪项未知，所以需要动态地将有的更新项设置到持久化Entity属性中
 * 本工具仅将非null的属性设置到持久化Entity对象中
 * @author HeLei
 */
public class EntityMerger {
	public static void merge(Object dto, Object entity) {
		try {
			for (PropertyDescriptor p : Introspector.getBeanInfo(dto.getClass(), Object.class)
					.getPropertyDescriptors()) {
				
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}
}
