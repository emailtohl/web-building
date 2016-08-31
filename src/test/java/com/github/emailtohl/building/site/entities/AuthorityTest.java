package com.github.emailtohl.building.site.entities;
import static com.github.emailtohl.building.site.entities.Authority.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class AuthorityTest {

	@Test
	public void testToStringArray() {
		String[] expected1 = {"ADMIN", "MANAGER", "EMPLOYEE"};
		assertArrayEquals(expected1, toStringArray(Arrays.asList(ADMIN, MANAGER, EMPLOYEE)));
		String[] expected2 = {};
		assertArrayEquals(expected2, toStringArray(Arrays.asList()));
	}

}
