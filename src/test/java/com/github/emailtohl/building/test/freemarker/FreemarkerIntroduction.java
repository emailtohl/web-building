package com.github.emailtohl.building.test.freemarker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

public class FreemarkerIntroduction {
	
	public static void main(String[] args) throws IOException, TemplateException {
		Version version = new Version(2, 3, 23);
		Configuration cfg = new Configuration(version);
		cfg.setClassForTemplateLoading(FreemarkerIntroduction.class, "ftl");
		System.out.println(new File("ftl").getAbsolutePath());
		System.out.println(ResourceUtils.getFile("classpath:config.properties"));
//		cfg.setDirectoryForTemplateLoading(new File("ftl"));
		cfg.setObjectWrapper(new DefaultObjectWrapper(version));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setClassicCompatible(true);// 处理空值为空字符串
		Map<String, String> root = new HashMap<String, String>();
		root.put("name", "张三");
		root.put("address", "中国-北京");
		Template template = cfg.getTemplate("person.ftl");
		
		try (Writer out = new StringWriter(2048)) {
			template.process(root, out);
			System.out.println(out.toString());
		};
		
		try (ByteArrayOutputStream b = new ByteArrayOutputStream();
				Writer out = new OutputStreamWriter(b)) {
			template.process(root, out);
			System.out.println(b.toString("UTF-8"));
		};
		
		try (Writer out = new OutputStreamWriter(System.out)) {
			template.process(root, out);
		};
		
	}
}
