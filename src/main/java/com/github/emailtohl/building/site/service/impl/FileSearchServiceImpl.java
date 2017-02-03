package com.github.emailtohl.building.site.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.emailtohl.building.site.service.FileSearchService;

/**
 * 对文件系统建立索引，查询文本内容
 * 
 * @author HeLei
 */
@Service
public class FileSearchServiceImpl implements FileSearchService, ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LogManager.getLogger();
	private static final Set<String> TEXT_SUFFIX = new HashSet<String>(
			Arrays.asList("txt", "html", "xml", "js", "java", "css", "properties"));
	private static final long TEN_M = 10_485_760L;// 10兆
	public static final String FILE_NAME = "fileName";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_SIZE = "fileSize";
	private final Analyzer analyzer = new StandardAnalyzer();
	
	@Value(value = "${indexBase}")
	private String indexBase;
	@Value(value = "${uploadBase}")
	private String uploadBase;

	@PostConstruct
	public void init(String indexBase) {
		if (StringUtils.isEmpty(indexBase))
			this.indexBase = "../web-building-indexBase/cms";
		else
			this.indexBase = indexBase;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		index(this.uploadBase);
	}
	
	public static void main(String[] args) throws IOException {
		File directory = new File("./test").getCanonicalFile();
		System.out.println(directory);
	}
	
	/**
	 * 为文件目录创建索引
	 * @param filePath
	 */
	public void index(String filePath) {
		IndexWriter indexWriter = null;
		try {
			// 打开索引库
			FSDirectory dir = FSDirectory.open(Paths.get(indexBase));
			// 创建索引的写入配置表
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			indexWriter = new IndexWriter(dir, iwc);
			// 采集原始文档
			addFileToDocument(new File(filePath), indexWriter);
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("创建索引失败！", e);
			try {
				indexWriter.rollback();
			} catch (IOException e1) {
				logger.error("回滚失败", e1);
			}
			throw new IllegalStateException("创建索引失败！", e);
		} finally {
			if (indexWriter != null && indexWriter.isOpen()) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					logger.error("indexWriter关闭失败", e);
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
		IndexWriter indexWriter = null;
		try {
			// 打开索引库
			FSDirectory dir = FSDirectory.open(Paths.get(indexBase));
			// 创建索引的写入配置表
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			indexWriter = new IndexWriter(dir, iwc);
			String[] fields = { FILE_NAME, FILE_CONTENT, FILE_PATH, FILE_SIZE };
			QueryParser qp = new MultiFieldQueryParser(fields, analyzer);
			Query q = qp.parse(queryString);

			// IndexReader ir = DirectoryReader.open(dir);
			IndexReader ir = DirectoryReader.open(indexWriter, true);
			IndexSearcher is = new IndexSearcher(ir);
			TopDocs docs = is.search(q, 20);
			logger.debug(docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				logger.debug(sd.score);
				Document doc = is.doc(sd.doc);
				logger.debug(doc);
				paths.add(doc.getField(FILE_PATH).stringValue());
			}
		} catch (IOException e) {
			logger.error("查询索引失败！", e);
			try {
				indexWriter.rollback();
			} catch (IOException e1) {
				logger.error("indexWriter回滚失败！", e1);
			}
			throw new IllegalStateException("查询索引失败！", e);
		} catch (ParseException e) {
			logger.error("查询语句解析失败！", e);
		} finally {
			if (indexWriter != null && indexWriter.isOpen()) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					logger.error("indexWriter关闭失败", e);
				}
			}
		}
		return paths;
	}
	
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
			flag = TEXT_SUFFIX.contains(suffix);
		}
		return flag;
	}
	
	/**
	 * 将文本文件读为lucene的Document并添加进IndexWriter
	 * @param dir
	 * @param indexWriter
	 * @throws IOException
	 */
	private void addFileToDocument(File dir, IndexWriter indexWriter) throws IOException {
		if (dir.isFile() && isText(dir)) {
			long size = FileUtils.sizeOf(dir);
			if (size < TEN_M) {// 处理不大于10M的文件
				String content = FileUtils.readFileToString(dir, StandardCharsets.UTF_8);
				Field fName = new TextField(FILE_NAME, dir.getName(), Store.YES);
				Field fContent = new TextField(FILE_CONTENT, content, Store.YES);
				Field fPath = new TextField(FILE_PATH, dir.getAbsolutePath(), Store.YES);
				Field fSize = new LongField(FILE_SIZE, size, Store.YES);
				// 创建文档对象
				Document doc = new Document();
				doc.add(fName);
				doc.add(fContent);
				doc.add(fSize);
				doc.add(fPath);
				indexWriter.addDocument(doc);
			}
		} else if (dir.isDirectory()) {
			for (File sub : dir.listFiles()) {
				addFileToDocument(sub, indexWriter);
			}
		}
	}

}
