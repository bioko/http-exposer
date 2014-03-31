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

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 18, 2014
 *
 */
public class MockRequest implements HttpServletRequest {

	private final String fMethod;
	private final String fPath;
	private final String fContent;
	private String fType;
	private Map<String, String> fHeaders;
	private String fCharacterEncoding;

    public MockRequest(String method, String path) {
		this(method, path, "");
	}
	
	public MockRequest(String method, String path, String content) {
		fMethod = method;
		fPath = path;
		fContent = content;
		fHeaders = new HashMap<>();
	}
	

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		fCharacterEncoding = env;
	}
	
	@Override
	public String getCharacterEncoding() {
		return fCharacterEncoding;
	}
	
	@Override
	public int getContentLength() {
        if (fContent == null) {
            return 0;
        }
		return fContent.length();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return new MOCSIS(fContent.getBytes());
	}
	

	@Override
	public String getMethod() {
		if (fMethod != null) {
			return fMethod;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPathInfo() {
		if (fPath != null) {
			return fPath;
		}
		throw new UnsupportedOperationException();
	}
	
	public void setContentType(String type) {
		fType = type;
	}
	
	@Override
	public String getContentType() {
		return fType;
	}
	
	public void setHeader(String name, String value) {
		fHeaders.put(name, value);
	}

	@Override
	public String getHeader(String name) {
		return fHeaders.get(name);
	}

    @Override
    public String getQueryString() {
        return getPathInfo().replaceAll(".*\\?", "");
    }

    private static final class MOCSIS extends ServletInputStream {

		private ByteArrayInputStream fIS;

		public MOCSIS(byte[] bytes) {
			fIS = new ByteArrayInputStream(bytes);
		}

		@Override
		public boolean isFinished() {
			return fIS.available() > 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener readListener) {
		}

		@Override
		public int read() throws IOException {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	@Override
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getContentLengthLong() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParameter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getParameterValues(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProtocol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getScheme() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServerName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getServerPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteAddr() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteHost() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSecure() {
		throw new UnsupportedOperationException();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRealPath(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getRemotePort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocalName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLocalAddr() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLocalPort() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletContext getServletContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncStarted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAsyncSupported() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AsyncContext getAsyncContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cookie[] getCookies() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getDateHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getIntHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPathTranslated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContextPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRemoteUser() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUserInRole(String role) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Principal getUserPrincipal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestedSessionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getRequestURI() {
		throw new UnsupportedOperationException();
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getServletPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSession getSession(boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpSession getSession() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String changeSessionId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void login(String username, String password) throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void logout() throws ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		throw new UnsupportedOperationException();
	}

}