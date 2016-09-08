package com.github.emailtohl.building.common.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.CriteriaQueries;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion;
import com.github.emailtohl.building.common.jpa.jpaCriterionQuery.Criterion.Operator;

/**
 * 前端传入的可能是一个对象，对象里面各个属性值，作为AND的组合条件查询，这时候不需要每个属性去探测是否为null
 * 本工具将这种对象的中不为null的属性组合成SearchCriteria共AbstractSearchableJpaRepository.searche()方法使用
 * @author Helei
 */
public final class SearchCriteriaUtil {
	/**
	 * 根据查询对象，分析其JavaBean属性，对于不为null的属性将生成组合查询条件
	 * @param javaBean
	 * @param pageable
	 * @return
	 */
	public static CriteriaQueries get(Object javaBean) {
		CriteriaQueries sc = CriteriaQueries.Builder.create();
		Map<String, Object> map = BeanTools.getPropertyNameValueMap(javaBean);
		for (Entry<String, Object> e : map.entrySet()) {
			Object value = e.getValue();
			if (value == null) {
				continue;
			}
			if (value instanceof String) {// 如果是字符串查询，就模糊查询
				String s = ((String) value).trim();
				if (!s.isEmpty()) {// 如果是空字符串，则忽略该条件
					sc.add(new Criterion(e.getKey(), Operator.LIKE, s));
				}
			} else if (value instanceof Collection) {
				sc.add(new Criterion(e.getKey(), Operator.IN, value));
			} else {
				sc.add(new Criterion(e.getKey(), Operator.EQ, value));
			}
		}
		return sc;
	}
}
