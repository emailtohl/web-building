package com.github.emailtohl.building.common.lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.FileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
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
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * lucene的数据源获取有很多开源框架，如Solr提取数据库和XML；Nutch、Heritrix、Grub获取web站点；
 * Aperture支持web站点，文件系统、邮箱等；Tika提供数据过滤。
 * 
 * 注意：对于文本文件，目前只支持UTF-8格式
 * 
 * 本工具只是适应本项目中轻量级的对文件系统建立索引，查询文本内容，更多应用还需借助成熟的开源框架。
 * 
 * @author HeLei
 * @date 2017.02.04
 */
public class FileSearch {
	private static final Logger logger = LogManager.getLogger();
	public static final String FILE_NAME = "fileName";
	public static final String FILE_TIME = "fileTime";
	public static final String FILE_CONTENT = "fileContent";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_SIZE = "fileSize";
	public static final int TOP_HITS = 1000;
	/** 是否索引过，如果已经索引了，则不能再设置分词器 */
	private volatile boolean isIndexed = false;
	private FileFilter textFileFilter = new TextFilesFilter();
	
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
	 * @return 被索引的Document数
	 */
	public synchronized int index(String searchDir) {
		int numIndexed = 0;
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		// 每一次都会进行创建新的索引,第二次删掉原来的创建新的索引
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		// 创建索引的Writer
		try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
			// 采集原始文档
			appendDocument(new File(searchDir), indexWriter);
			indexWriter.commit();
			numIndexed = indexWriter.numDocs();
			isIndexed = true;
		} catch (IOException e) {
			logger.error("创建索引失败", e);
		}
		return numIndexed;
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
			String[] fields = { FILE_NAME, FILE_TIME, FILE_CONTENT, FILE_PATH, FILE_SIZE };
			QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
//			Query q = new TermQuery(new Term(FILE_CONTENT, queryString));
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
	 * 分页查询出Lucene原始的Document对象
	 * @param queryString 查询语句
	 * @param pageable Spring-data的分页对象
	 * @return Spring-data的页面对象
	 */
	public Page<Document> query(String queryString, Pageable pageable) {
		List<Document> list = new ArrayList<Document>();
		int count = 0;
		IndexReader indexReader = null;
		try {
			String[] fields = { FILE_NAME, FILE_TIME, FILE_CONTENT, FILE_PATH, FILE_SIZE };
			QueryParser queryParser = new MultiFieldQueryParser(fields, analyzer);
			Query query = queryParser.parse(queryString);
			indexReader = DirectoryReader.open(indexBase);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			count = indexSearcher.count(query);
			Sort sort = getSort(pageable);
			TopDocs docs = indexSearcher.search(query, TOP_HITS, sort);
			logger.debug(docs.totalHits);
			int offset = pageable.getOffset();
			int end = offset + pageable.getPageSize();
			
			for (int i = offset; i < end && i < count && i < TOP_HITS; i++) {
				ScoreDoc sd = docs.scoreDocs[i];
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
		return new PageImpl<Document>(list, pageable, count);
	}
	
	private Sort getSort(Pageable pageable) {
		Sort sort = new Sort();
		List<SortField> ls = new ArrayList<SortField>();
		org.springframework.data.domain.Sort s = pageable.getSort();
		if (s != null) {
			for (Iterator<org.springframework.data.domain.Sort.Order> i = s.iterator(); i.hasNext();) {
				org.springframework.data.domain.Sort.Order o = i.next();
				SortField sortField = new SortField(o.getProperty(), Type.SCORE);// 以相关度进行排序
				ls.add(sortField);
			}
		}
		if (ls.size() > 0) {
			SortField[] sortFields = new SortField[ls.size()];
			sort.setSort(ls.toArray(sortFields));
		}
		return sort;
	}
	
	/**
	 * 添加文件的索引
	 * @param file
	 */
	public void addIndex(String file) {
		File f = new File(file);
		if (textFileFilter.accept(f)) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
				Document doc = getDocument(f);
				indexWriter.addDocument(doc);
				indexWriter.commit();
			} catch (IOException e) {
				logger.error("添加索引失败", e);
			}
		}
	}
	
	/**
	 * 更新文件的索引
	 * @param file
	 */
	public synchronized void updateIndex(String file) {
		File f = new File(file);
		if (textFileFilter.accept(f)) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
					Document doc = getDocument(f);
					indexWriter.updateDocument(new Term(FILE_PATH, f.getPath()), doc);
					indexWriter.commit();
			} catch (IOException e) {
				logger.error("更新索引失败", e);
			}
		}
	}
	
	/**
	 * 删除文件的索引
	 * @param file
	 */
	public synchronized void deleteIndex(String file) {
		File f = new File(file);
		if (textFileFilter.accept(f)) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			// 创建索引的Writer
			try (IndexWriter indexWriter = new IndexWriter(indexBase, indexWriterConfig)) {
					indexWriter.deleteDocuments(new Term(FILE_PATH, f.getPath()));
					indexWriter.commit();
			} catch (IOException e) {
				logger.error("更新索引失败", e);
			}
		}
	}
	
	/**
	 * 删除全部索引
	 */
	public synchronized void deleteAllIndex() {
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
	
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public synchronized void setAnalyzer(Analyzer analyzer) {
		synchronized (this) {
			if (isIndexed)
				throw new IllegalStateException("已经被索引过，不能再设置分词器!");
		}
		this.analyzer = analyzer;
	}

	/**
	 * 将文本文件读为lucene的Document并添加进IndexWriter
	 * @param file
	 * @param indexWriter
	 * @throws IOException
	 */
	private void appendDocument(File file, IndexWriter indexWriter) throws IOException {
		if (textFileFilter.accept(file)) {
			indexWriter.addDocument(getDocument(file));
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
		fName.setBoost(1.2F);
		Field fContent = new TextField(FILE_CONTENT, content, Store.NO);
		// StringField被索引不被分词，整个值被看作为一个单独的token而被索引
		Field fPath = new StringField(FILE_PATH, file.getPath(), Store.YES);
		Field fTime = new LongField(FILE_TIME, System.currentTimeMillis(), Store.YES);
		// 创建文档对象
		Document doc = new Document();
		doc.add(fName);
		doc.add(fContent);
		doc.add(fPath);
		doc.add(fTime);
		return doc;
	}
	
	/**
	 * 过滤出文本文件
	 * @author HeLei
	 * @date 2017.02.04
	 */
	class TextFilesFilter implements FileFilter {
		private static final long MAX_BYTES = 10_485_760L;// 10兆
		private final FileTypeMap fileTypeMap = FileTypeMap.getDefaultFileTypeMap();
		private final Set<String> TEXT_SUFFIX = new HashSet<String>(
				Arrays.asList("txt", "html", "xml", "js", "java", "css", "properties"));
		@Override
		public boolean accept(File f) {
			if (f.isDirectory() || !f.canRead() || f.length() > MAX_BYTES)
				return false;
			boolean flag = false;
			String name = f.getName();
			int i = name.lastIndexOf(".");
			if (i > -1 && name.length() > i) {
				String suffix = name.substring(i + 1, name.length());
				flag = TEXT_SUFFIX.contains(suffix);
			}
			String fileType;
			if (!flag) {
				fileType = fileTypeMap.getContentType(f.getAbsolutePath());
				flag = fileType.contains("text");
			}
			if (!flag) {
				fileType = URLConnection.guessContentTypeFromName(f.getAbsolutePath());
				if (fileType != null)
					flag = fileType.contains("text");
			}
			return flag;
		}
	}
}
