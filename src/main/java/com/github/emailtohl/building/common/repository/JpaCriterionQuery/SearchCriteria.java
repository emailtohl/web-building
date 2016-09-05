package com.github.emailtohl.building.common.repository.JpaCriterionQuery;

import java.util.ArrayList;
import java.util.List;
/**
 * 由List组成的查询条件
 *
 */
public interface SearchCriteria extends List<Criterion> {
	public static class Builder {
		public static SearchCriteria create() {
			return new SearchCriteriaImpl();
		}

		private static class SearchCriteriaImpl extends ArrayList<Criterion> implements SearchCriteria {
			private static final long serialVersionUID = 7453919407619881505L;

		}
	}
}
