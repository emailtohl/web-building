package com.github.emailtohl.building.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Servlet Filter implementation class CompressionFilter
 */
//@WebFilter("/*") // 注释掉此行，在mine.frame.filter.Configurator中以编程方式配置的过滤器
@SuppressWarnings("unused")
public class CompressionFilter implements Filter {
	private static final Logger logger = Logger.getLogger(CompressionFilter.class.getName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String accept = ((HttpServletRequest) request).getHeader("Accept-Encoding");
		if (accept != null && accept.contains("gzip")) {
			logger.finest("Encoding requested.");
			((HttpServletResponse) response).setHeader("Content-Encoding", "gzip");
			ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse) response);
			try {
				chain.doFilter(request, wrapper);
			} finally {
				try {
					wrapper.finish();
				} catch (Exception e) {
					e.printStackTrace();
					logger.log(Level.SEVERE, "解压失败", e);
				}
			}
		} else {
			logger.finest("Encoding not requested.");
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	private static class ResponseWrapper extends HttpServletResponseWrapper {
		private GZIPServletOutputStream outputStream;
		private PrintWriter writer;

		public ResponseWrapper(HttpServletResponse request) {
			super(request);
		}

		@Override
		public synchronized ServletOutputStream getOutputStream() throws IOException {
			if (this.writer != null)
				throw new IllegalStateException("getWriter() already called.");
			if (this.outputStream == null)
				this.outputStream = new GZIPServletOutputStream(super.getOutputStream());
			return this.outputStream;
		}

		@Override
		public synchronized PrintWriter getWriter() throws IOException {
			if (this.writer == null && this.outputStream != null)
				throw new IllegalStateException("getOutputStream() already called.");
			if (this.writer == null) {
				this.outputStream = new GZIPServletOutputStream(super.getOutputStream());
				this.writer = new PrintWriter(new OutputStreamWriter(this.outputStream, this.getCharacterEncoding()));
			}
			return this.writer;
		}

		@Override
		public synchronized void flushBuffer() throws IOException {
			if (this.writer != null)
				this.writer.flush();
			else if (this.outputStream != null)
				this.outputStream.flush();
			super.flushBuffer();
		}

		@Override
		public void setContentLength(int length) {
		}

		@Override
		public void setContentLengthLong(long length) {
		}

		@Override
		public void setHeader(String name, String value) {
			if (!"content-length".equalsIgnoreCase(name))
				super.setHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			if (!"content-length".equalsIgnoreCase(name))
				super.setHeader(name, value);
		}

		@Override
		public void setIntHeader(String name, int value) {
			if (!"content-length".equalsIgnoreCase(name))
				super.setIntHeader(name, value);
		}

		@Override
		public void addIntHeader(String name, int value) {
			if (!"content-length".equalsIgnoreCase(name))
				super.setIntHeader(name, value);
		}

		public synchronized void finish() throws IOException {
			if (this.writer != null)
				this.writer.close();
			else if (this.outputStream != null)
				this.outputStream.finish();
		}
	}

	private static class GZIPServletOutputStream extends ServletOutputStream {
		private final ServletOutputStream servletOutputStream;
		private final GZIPOutputStream gzipStream;

		public GZIPServletOutputStream(ServletOutputStream servletOutputStream) throws IOException {
			this.servletOutputStream = servletOutputStream;
			this.gzipStream = new GZIPOutputStream(servletOutputStream);
		}

		@Override
		public boolean isReady() {
			return this.servletOutputStream.isReady();
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
			this.servletOutputStream.setWriteListener(writeListener);
		}

		@Override
		public void write(int b) throws IOException {
			this.gzipStream.write(b);
		}

		@Override
		public void close() throws IOException {
			this.gzipStream.close();
		}

		@Override
		public void flush() throws IOException {
			this.gzipStream.flush();
		}

		public void finish() throws IOException {
			this.gzipStream.finish();
		}
	}
}
