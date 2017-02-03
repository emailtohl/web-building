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
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * lucene的数据源获取有很多开源框架，如Solr提取数据库和XML；Nutch、Heritrix、Grub获取web站点；
 * Aperture支持web站点，文件系统、邮箱等；Tika提供数据过滤。
 * 
 * 本工具只是适应本项目中轻量级的对文件系统建立索引，查询文本内容，更多应用还需借助成熟的开源框架。
 * 
 * @author HeLei
 */
public class FileSearch {
	private static final Logger logger = LogManager.getLogger();
	private static final Set<String> TEXT_SUFFIX = new HashSet<String>(
			Arrays.asList("txt", "html", "xml", "js", "java", "css", "properties"));
	private static final long TEN_M = 10_485_760L;// 10兆
	public static final String FILE_NAME = "fileName";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_SIZE = "fileSize";
	/** 分词器 */
	private Analyzer analyzer = new StandardAnalyzer();
	/** 索引库 */
	private Directory indexBase;

	/**
	 * 可接受文件系统的索引目录，也可以接受内存形式的索引目录
	 * @param indexBase 索引目录
	 */
	public FileSearch(Directory indexBase) {
		this.indexBase = indexBase;
	}
	
	/**
	 * 只接受文件系统的索引目录
	 * @param indexBaseFSDirectory 文件系统的索引目录
	 * @throws IOException
	 */
	public FileSearch(String indexBaseFSDirectory) throws IOException {
		this.indexBase = FSDirectory.open(Paths.get(indexBaseFSDirectory));
	}
	
	/**
	 * 为文件目录创建索引
	 * @param filePath
	 */
	public void index(String filePath) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 每一次都会进行创建新的索引,第二次删掉原来的创建新的索引
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			// 采集原始文档
			addFileToDocument(new File(filePath), indexWriter);
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("打开索引库失败", e);
		}
	}
	
	/**
	 * 根据查询语句获取文件的路径
	 * @param query
	 * @return
	 */
	public Set<String> queryPath(String queryString) {
		Set<String> paths = new TreeSet<String>();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
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
			ir.close();
		} catch (IOException e) {
			logger.error("打开索引库失败", e);
		} catch (ParseException e) {
			logger.error("查询语句解析失败", e);
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
