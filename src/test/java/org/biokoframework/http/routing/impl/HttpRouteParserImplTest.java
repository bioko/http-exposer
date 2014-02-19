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

import javax.servlet.http.HttpServletRequest;

import org.biokoframework.http.mock.MockRequest;
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
public class HttpRouteParserImplTest {
	
	private HttpRouteParserImpl fParser;
	
	@Rule
	public ExpectedException fExpected = ExpectedException.none();

	@Before
	public void createParser()  {
		fParser = new HttpRouteParserImpl();
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
}
