/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package org.biokoframework.http.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 18, 2014
 *
 */
public class MockResponse implements HttpServletResponse {

	private String fContentType;
	private String fCharset = Charset.defaultCharset().toString();
	private MockSOS fOutputStream;
	private StringWriter fStringWriter;
	private int fSC;

	@Override
	public String getCharacterEncoding() {
		return fCharset;
	}

	@Override
	public String getContentType() {
		return fContentType;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (fOutputStream == null) {
			fOutputStream = new MockSOS();
		}
		return fOutputStream;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		fCharset = charset;
	}

	@Override
	public void setContentType(String type) {
		fContentType = type;
	}

	@Override
	public void setStatus(int sc) {
		fSC = sc;
	}

	@Override
	public int getStatus() {
		return fSC;
	}

	@Override
	public String toString() {
		if (fOutputStream != null) {
			return fOutputStream.toString();
			
		} else if (fStringWriter != null) {
			return fStringWriter.toString();
			
		} else {
			return super.toString();
		}

	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (fStringWriter == null) {
			fStringWriter = new StringWriter();
		}
		return new PrintWriter(fStringWriter);
	}

	private static final class MockSOS extends ServletOutputStream {

		private ByteArrayOutputStream os = new ByteArrayOutputStream();

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {

		}

		@Override
		public void write(int b) throws IOException {
			os.write(b);
		}

		@Override
		public String toString() {
			return new String(os.toByteArray());
		}
	}

	// Everything else will throw UnsupportedOperationException

	@Override
	public void setContentLength(int len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLengthLong(long len) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBufferSize(int size) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocale(Locale loc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeUrl(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int sc) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatus(int sc, String sm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

}
