package com.github.emailtohl.building.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Test {
	public static void main(String[] args) throws IOException {
		ClassLoader cl = Test.class.getClassLoader();
		URL url = Test.class.getProtectionDomain().getCodeSource().getLocation();
		System.out.println(url);
		try (InputStream is = cl.getResourceAsStream("img/icon-head-emailtohl.png")) {
			System.out.println(is);
		}
	}
}
