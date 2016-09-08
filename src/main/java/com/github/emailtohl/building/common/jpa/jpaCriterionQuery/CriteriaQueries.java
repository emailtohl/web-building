package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import java.util.ArrayList;
import java.util.List;
/**
 * 由List组成的查询条件
 * @author Nick Williams
 */
public interface CriteriaQueries extends List<Criterion> {
	public static class Builder {
		public static CriteriaQueries create() {
			return new CriteriaQueriesImpl();
		}

		private static class CriteriaQueriesImpl extends ArrayList<Criterion> implements CriteriaQueries {
			private static final long serialVersionUID = 7453919407619881505L;

		}
	}
}
