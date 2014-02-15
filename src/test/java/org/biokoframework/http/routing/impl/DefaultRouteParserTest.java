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

package org.biokoframework.http.routing.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.RouteNotSupportedException;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 15, 2014
 * 
 */
public class DefaultRouteParserTest {
	
	private DefaultRouteParser fParser;
	
	@Rule
	public ExpectedException fExpected = ExpectedException.none();

	@Before
	public void createParser()  {
		fParser = new DefaultRouteParser();
	}

	@Test
	public void parseSimple() throws Exception {
		HttpServletRequest request = new MockRequest(HttpMethod.GET.toString(), "/1.0/path");
		
		IRoute route = fParser.getRoute(request);
		assertThat(route, is(notNullValue()));
		assertThat(route.getMethod(), is(equalTo(HttpMethod.GET)));
		assertThat(route.getPath(), is(equalTo("path")));
		
		request = new MockRequest(HttpMethod.GET.toString(), "/1.0/path/");
		
		route = fParser.getRoute(request);
		assertThat(route, is(notNullValue()));
		assertThat(route.getMethod(), is(equalTo(HttpMethod.GET)));
		assertThat(route.getPath(), is(equalTo("path")));
	}

	@Test
	public void parseRoot() throws Exception {
		
		HttpServletRequest request = new MockRequest(HttpMethod.POST.toString(), "/1.0");
		
		IRoute route = fParser.getRoute(request);
		assertThat(route, is(notNullValue()));
		assertThat(route.getMethod(), is(equalTo(HttpMethod.POST)));
		assertThat(route.getPath(), is(nullValue()));
		
		request = new MockRequest(HttpMethod.POST.toString(), "/1.0/");
		
		route = fParser.getRoute(request);
		assertThat(route, is(notNullValue()));
		assertThat(route.getMethod(), is(equalTo(HttpMethod.POST)));
		assertThat(route.getPath(), is(nullValue()));
	}
	
	@Test
	public void parseUnsupported() throws Exception {
		fExpected.expect(RouteNotSupportedException.class);
		
		HttpServletRequest request = new MockRequest("UNSUPPORTED", "/1.0/unsupported");
		
		fParser.getRoute(request);
	}
	
	@Test
	public void parseEscaped() throws Exception {
		HttpServletRequest request = new MockRequest(HttpMethod.PUT.toString(), "/1.0/some%2Fthing");
		
		IRoute route = fParser.getRoute(request);
		assertThat(route, is(notNullValue()));
		assertThat(route.getMethod(), is(equalTo(HttpMethod.PUT)));
		assertThat(route.getPath(), is(equalTo("some/thing")));
		
		request = new MockRequest(HttpMethod.PUT.toString(), "/1.0/some%2Fthing/");
		
		route = fParser.getRoute(request);
		assertThat(route, is(notNullValue()));
		assertThat(route.getMethod(), is(equalTo(HttpMethod.PUT)));
		assertThat(route.getPath(), is(equalTo("some/thing")));
	}

	private static final class MockRequest implements HttpServletRequest {

		private final String fMethod;
		private final String fPath;

		public MockRequest(String method, String path) {
			fMethod = method;
			fPath = path;
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
		public String getCharacterEncoding() {
			return null;
		}

		@Override
		public void setCharacterEncoding(String env)
				throws UnsupportedEncodingException {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getContentLength() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long getContentLengthLong() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getContentType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
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
		public AsyncContext startAsync(ServletRequest servletRequest,
				ServletResponse servletResponse) throws IllegalStateException {
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
		public String getHeader(String name) {
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

		@Override
		public String getPathTranslated() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getContextPath() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getQueryString() {
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
}
