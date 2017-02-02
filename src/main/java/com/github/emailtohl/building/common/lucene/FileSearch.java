package com.github.emailtohl.building.common.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

/**
 * 对文件系统建立索引，查询文本内容
 * 
 * @author HeLei
 */
public class FileSearch {
	public static final String FILE_NAME = "fileName";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_SIZE = "fileSize";
	private final Analyzer analyzer = new StandardAnalyzer();
	private final String indexBase;

	public FileSearch(String indexBase) {
		this.indexBase = indexBase;
	}

	/**
	 * 为
	 * @param filePath
	 */
	public void index(String filePath) {
		IndexWriter iw = null;
		try {
			// 打开索引库
			FSDirectory dir = FSDirectory.open(Paths.get(indexBase));
			// 创建索引的写入配置表
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			iw = new IndexWriter(dir, iwc);
			// 采集原始文档
			for (File f : new File(filePath).listFiles()) {
				String content = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
				long size = FileUtils.sizeOf(f);
				Field fName = new TextField(FILE_NAME, f.getName(), Store.YES);
				Field fContent = new TextField(FILE_CONTENT, content, Store.YES);
				Field fPath = new TextField(FILE_PATH, f.getAbsolutePath(), Store.YES);
				Field fSize = new LongField(FILE_SIZE, size, Store.YES);
				// 创建文档对象
				Document doc = new Document();
				doc.add(fName);
				doc.add(fContent);
				doc.add(fSize);
				doc.add(fPath);
				iw.addDocument(doc);

			}
			iw.commit();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				iw.rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			throw new IllegalStateException("创建索引失败！", e);
		} finally {
			if (iw != null && iw.isOpen()) {
				try {
					iw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
