package com.github.emailtohl.building.common.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
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
	private static final long TEN_MBYTES = 10_485_760L;// 10兆
	public static final String FILE_NAME = "fileName";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_SIZE = "fileSize";
	public static final int TOP_HITS = 100;
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
	 * 为需要查询的目录创建索引
	 * @param searchDir 需要查询的目录
	 */
	public void index(String searchDir) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 每一次都会进行创建新的索引,第二次删掉原来的创建新的索引
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			// 采集原始文档
			appendDocument(new File(searchDir), indexWriter);
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("创建索引失败", e);
		}
	}
	
	/**
	 * 查询出Lucene原始的Document对象
	 * @param queryString
	 * @return
	 */
	public List<Document> query(String queryString) {
		List<Document> list = new ArrayList<Document>();
		IndexReader indexReader = null;
		try {
			String[] fields = { FILE_NAME, FILE_CONTENT, FILE_PATH, FILE_SIZE };
			QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
			Query query = queryParser.parse(queryString);
			indexReader = DirectoryReader.open(indexBase);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			TopDocs docs = indexSearcher.search(query, TOP_HITS);
			logger.debug(docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				logger.debug(sd.score);
				Document doc = indexSearcher.doc(sd.doc);
				logger.debug(doc);
				list.add(doc);
			}
		} catch (IOException e) {
			logger.error("打开索引库失败", e);
		} catch (ParseException e) {
			logger.error("查询语句解析失败", e);
		} finally {
			if (indexReader != null) {
				try {
					indexReader.close();
				} catch (IOException e) {
					logger.error("IndexReader关闭失败", e);
				}
			}
		}
		return list;
	}
	
	/**
	 * 添加文件的索引
	 * @param file
	 */
	public void addIndex(String file) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			File f = new File(file);
			Document doc = getDocument(f);
			indexWriter.addDocument(doc);
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("添加索引失败", e);
		}
	}
	
	/**
	 * 更新文件的索引
	 * @param file
	 */
	public void updateIndex(String file) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			File f = new File(file);
			Document doc = getDocument(f);
			indexWriter.updateDocument(new Term(FILE_PATH, f.getPath()), doc);
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("更新索引失败", e);
		}
	}
	
	/**
	 * 删除文件的索引
	 * @param file
	 */
	public void deleteIndex(String file) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			File f = new File(file);
			indexWriter.deleteDocuments(new Term(FILE_PATH, f.getPath()));
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("更新索引失败", e);
		}
	}
	
	/**
	 * 删除全部索引
	 */
	public void deleteAllIndex() {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			indexWriter.deleteAll();
			indexWriter.commit();
		} catch (IOException e) {
			logger.error("更新索引失败", e);
		}
	}

	/**
	 * 根据查询语句获取文件的路径
	 * @param query
	 * @return 返回的路径是相对于index时的路径，若index时是绝对路径，则返回的也是绝对路径
	 */
	public Set<String> queryForFilePath(String queryString) {
		Set<String> paths = new TreeSet<String>();
		List<Document> list = query(queryString);
		for (Document doc : list) {
			paths.add(doc.getField(FILE_PATH).stringValue());
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
	 * @param file
	 * @param indexWriter
	 * @throws IOException
	 */
	private void appendDocument(File file, IndexWriter indexWriter) throws IOException {
		if (file.isFile() && isText(file)) {
			long size = FileUtils.sizeOf(file);
			if (size < TEN_MBYTES) {// 处理不大于10M的文件
				indexWriter.addDocument(getDocument(file));
			}
		} else if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				appendDocument(sub, indexWriter);
			}
		}
	}

	/**
	 * 分析文本文件，并创建一个Lucene的Document
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Document getDocument(File file) throws IOException {
		String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		// TextField既被索引又被分词，但是没有词向量
		Field fName = new TextField(FILE_NAME, file.getName(), Store.YES);
		Field fContent = new TextField(FILE_CONTENT, content, Store.YES);
		// StringField被索引不被分词，整个值被看作为一个单独的token而被索引
		Field fPath = new StringField(FILE_PATH, file.getPath(), Store.YES);
		// 创建文档对象
		Document doc = new Document();
		doc.add(fName);
		doc.add(fContent);
		doc.add(fPath);
		return doc;
	}
}
