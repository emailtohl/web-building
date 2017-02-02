package com.github.emailtohl.building.common.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * 对文件系统建立索引，查询文本内容
 * 
 * @author HeLei
 */
public class FileSearch {
	private static final Logger logger = LogManager.getLogger();
	public static final String FILE_NAME = "fileName";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_SIZE = "fileSize";
	private final Analyzer analyzer = new StandardAnalyzer();
	private final String indexBase;

	public FileSearch(String indexBase) {
		this.indexBase = indexBase;
	}

	private final Set<String> textSuffix = new HashSet<String>(Arrays.asList("txt", "html", "xml", "js", "java", "css", "properties"));
	/**
	 * 根据后缀判断该文件是否可读
	 * @param f
	 * @return
	 */
	private boolean isText(File f) {
		boolean flag = false;
		String name = f.getName();
		int i = name.lastIndexOf(".");
		if (i > -1 && name.length() > i) {
			String suffix = name.substring(i + 1, name.length());
			flag = textSuffix.contains(suffix);
		}
		
		return flag;
	}
	
	/**
	 * 将文本文件读为lucene的Document并添加进IndexWriter
	 * @param f
	 * @param iw
	 * @throws IOException
	 */
	private void addFileToDocument(File f, IndexWriter iw) throws IOException {
		if (f.isFile() && isText(f)) {
			long size = FileUtils.sizeOf(f);
			String content = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
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
		} else if (f.isDirectory()) {
			for (File sub : f.listFiles()) {
				addFileToDocument(sub, iw);
			}
		}
	}
	
	/**
	 * 为文件目录创建索引
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
			addFileToDocument(new File(filePath), iw);
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
	
	/**
	 * 根据查询语句获取文件的路径
	 * @param query
	 * @return
	 */
	public Set<String> queryPath(String queryString) {
		Set<String> paths = new TreeSet<String>();
		IndexWriter iw = null;
		try {
			// 打开索引库
			FSDirectory dir = FSDirectory.open(Paths.get(indexBase));
			// 创建索引的写入配置表
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			iw = new IndexWriter(dir, iwc);
			String[] fields = { FILE_NAME, FILE_CONTENT, FILE_PATH, FILE_SIZE };
			QueryParser qp = new MultiFieldQueryParser(fields, analyzer);
			Query q = qp.parse(queryString);

			// IndexReader ir = DirectoryReader.open(dir);
			IndexReader ir = DirectoryReader.open(iw, true);
			IndexSearcher is = new IndexSearcher(ir);
			TopDocs docs = is.search(q, 20);
			logger.debug(docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				logger.debug(sd.score);
				Document doc = is.doc(sd.doc);
				logger.debug(doc);
				paths.add(doc.getField("path").stringValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				iw.rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			throw new IllegalStateException("创建索引失败！", e);
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (iw != null && iw.isOpen()) {
				try {
					iw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return paths;
	}
}
