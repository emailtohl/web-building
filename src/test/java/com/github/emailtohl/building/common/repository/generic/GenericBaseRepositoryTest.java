package com.github.emailtohl.building.common.repository.generic;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenericBaseRepositoryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGenericBaseRepository() {
		Left l = new Left();
		Assert.assertSame(Long.class, l.idClass);
		Assert.assertSame(String.class, l.entityClass);

		Right r = new Right();
		Assert.assertSame(Long.class, r.idClass);
		Assert.assertSame(String.class, r.entityClass);

		Pair p = new Pair();
		Assert.assertSame(Long.class, p.idClass);
		Assert.assertSame(String.class, p.entityClass);
	}

	@Test(expected = IllegalStateException.class)
	public void testSpecific() {
		SpecificLeftDefined<String> s1 = new SpecificLeftDefined<String>();
		Assert.assertSame(Long.class, s1.idClass);
		Assert.assertNull(s1.entityClass);

		SpecificRightDefined<Long> s2 = new SpecificRightDefined<Long>();
		Assert.assertNull(s2.idClass);
		Assert.assertSame(String.class, s2.entityClass);

		SpecificPairDefined<Long, String> s3 = new SpecificPairDefined<Long, String>();
		Assert.assertNull(s3.idClass);
		Assert.assertNull(s3.entityClass);
	}

}

abstract class LeftDefined<A extends Serializable> extends GenericJpaRepository<Long, A> {
}

abstract class RightDefined<B extends Serializable> extends GenericJpaRepository<B, String> {
}

abstract class PairDefined extends GenericJpaRepository<Long, String> {
}

class Left extends LeftDefined<String> {
}

class Right extends RightDefined<Long> {
}

class Pair extends PairDefined {
}

class SpecificLeftDefined<A extends Serializable> extends GenericJpaRepository<Long, A> {
}

class SpecificRightDefined<B extends Serializable> extends GenericJpaRepository<B, String> {
}

class SpecificPairDefined<A extends Serializable, B extends Serializable> extends GenericJpaRepository<A, B> {
}