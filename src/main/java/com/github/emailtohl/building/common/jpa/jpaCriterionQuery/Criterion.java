package com.github.emailtohl.building.common.jpa.jpaCriterionQuery;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
/**
 * 标准查询的条件
 * propertyName可以用“.”表示嵌套实体下的属性名
 * @author HeLei
 */
public class Criterion {
	private final String propertyName;
	private final Operator operator;
	private final Object compareTo;

	public Criterion(String propertyName, Operator operator, Object compareTo) {
		this.propertyName = propertyName;
		this.operator = operator;
		this.compareTo = compareTo;
	}

	public String getPropertyName() {
		return this.propertyName;
	}

	public Operator getOperator() {
		return this.operator;
	}

	public Object getCompareTo() {
		return this.compareTo;
	}

	public static enum Operator {
		EQ {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				return b.equal(getPath(r, c.getPropertyName()), c.getCompareTo());
			}
		},
		NEQ {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				return b.notEqual(getPath(r, c.getPropertyName()), c.getCompareTo());
			}
		},
		LT {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				Comparable comparable = getComparable(c);
				Path<? extends Comparable> p = getComparablePath(r, c.getPropertyName());
				return b.lessThan(p, comparable);
			}
		},
		LTE {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				Comparable comparable = getComparable(c);
				Path<? extends Comparable> p = getComparablePath(r, c.getPropertyName());
				return b.lessThanOrEqualTo(p, comparable);
			}
		},
		GT {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				Comparable comparable = getComparable(c);
				Path<? extends Comparable> p = getComparablePath(r, c.getPropertyName());
				return b.greaterThan(p, comparable);
			}
		},
		GTE {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				Comparable comparable = getComparable(c);
				Path<? extends Comparable> p = getComparablePath(r, c.getPropertyName());
				return b.greaterThanOrEqualTo(p, comparable);
			}
		},
		LIKE {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				return b.like(getStringPath(r, c.getPropertyName()), getString(c));
			}
		},
		NOT_LIKE {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				return b.notLike(getStringPath(r, c.getPropertyName()), getString(c));
			}
		},
		IN {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				Object o = c.getCompareTo();
				if (o == null)
					return getPath(r, c.getPropertyName()).in();
				if (o instanceof Collection)
					return getPath(r, c.getPropertyName()).in((Collection<?>) o);
				throw new IllegalArgumentException(c.getPropertyName());
			}
		},
		NOT_IN {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				Object o = c.getCompareTo();
				if (o == null)
					return b.not(getPath(r, c.getPropertyName()).in());
				if (o instanceof Collection)
					return b.not(getPath(r, c.getPropertyName()).in((Collection<?>) o));
				throw new IllegalArgumentException(c.getPropertyName());
			}
		},
		NULL {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				return getPath(r, c.getPropertyName()).isNull();
			}
		},
		NOT_NULL {
			@Override
			public Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b) {
				return getPath(r, c.getPropertyName()).isNotNull();
			}
		};

		public abstract Predicate toPredicate(Criterion c, Root<?> r, CriteriaBuilder b);

		private static Path<?> getPath(Root<?> r, String propertyName) {
			Path<?> p = null;
			boolean first = true;
			for (String s : propertyName.split("\\.")) {
				if (first) {
					try {
						p = r.get(s);
					} catch (IllegalArgumentException e) {
						throw new IllegalArgumentException("检查" + r.getJavaType() + "实体类是否有“" + s + "”属性", e);
					}
					first = false;
				} else {
					p = p.get(s);
				}
			}
			return p;
		}
		
		@SuppressWarnings("unchecked")
		private static Path<? extends Comparable<?>> getComparablePath(Root<?> r, String propertyName) {
			Path<?> p = getPath(r, propertyName);
			return (Path<? extends Comparable<?>>) p;
		}
		
		@SuppressWarnings("unchecked")
		private static Path<String> getStringPath(Root<?> r, String propertyName) {
			Path<?> p = getPath(r, propertyName);
			return (Path<String>) p;
		}
		
		private static Comparable<?> getComparable(Criterion c) {
			Object o = c.getCompareTo();
			if (!(o instanceof Comparable))
				throw new IllegalArgumentException(c.getPropertyName());
			return (Comparable<?>) o;
		}

		private static String getString(Criterion c) {
			Object o = c.getCompareTo();
			if (!(o instanceof String))
				throw new IllegalArgumentException(c.getPropertyName());
			return (String) o;
		}
	}

	@Override
	public String toString() {
		return "Criterion [propertyName=" + propertyName + ", operator=" + operator + ", compareTo=" + compareTo + "]";
	}

}
