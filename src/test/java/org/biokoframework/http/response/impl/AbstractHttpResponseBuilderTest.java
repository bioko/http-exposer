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


package org.biokoframework.http.response.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.mock.MockRequest;
import org.biokoframework.http.mock.MockResponse;
import org.biokoframework.utils.fields.Fields;
import org.junit.Test;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class AbstractHttpResponseBuilderTest {

	@Test
	public void testContentType() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("POST", "/something", "some plain text content");
		request.setContentType("text/plain");
		
		builder.build(request, new MockResponse(), null, null);
		
		assertThat(builder.fCapturedTypes, contains("text/plain"));
		assertThat(builder.fCapturedExtension, is(nullValue()));
	}
	
	@Test
	public void testContentTypeAndExtension() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("POST", "/something.txt", "some plain text content");
		request.setContentType("text/plain");
		
		builder.build(request, new MockResponse(), null, null);
		
		assertThat(builder.fCapturedTypes, contains("text/plain"));
		assertThat(builder.fCapturedExtension, is(equalTo("txt")));
	}
	
	@Test
	public void testAccept() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("GET", "/something");
		request.setHeader("Accept", "text/plain");
		
		builder.build(request, new MockResponse(), null, null);
		
		assertThat(builder.fCapturedTypes, contains("text/plain"));
		assertThat(builder.fCapturedExtension, is(nullValue()));
	}
	
	@Test
	public void testAcceptAndExtension() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("GET", "/something.txt");
		request.setHeader("Accept", "text/plain");
		
		builder.build(request, new MockResponse(), null, null);
		
		assertThat(builder.fCapturedTypes, contains("text/plain"));
		assertThat(builder.fCapturedExtension, is(equalTo("txt")));
	}
	
	@Test
	public void testOnlyExtension() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("GET", "/something.txt", "some plain text content");
		
		builder.build(request, new MockResponse(), null, null);
		
		assertThat(builder.fCapturedTypes, is(nullValue()));
		assertThat(builder.fCapturedExtension, is(equalTo("txt")));
	}

	@Test
	public void applyDefaultCharsetToContentType() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("GET", "/something.txt", "some plain text content");
		MockResponse response = new MockResponse();
		
		builder.build(request, response, null, null);
		
		assertThat(builder.fCapturedTypes, is(nullValue()));
		assertThat(builder.fCapturedExtension, is(equalTo("txt")));
		assertThat(response.getContentType(), is(equalTo("plain/text; charset=utf-8")));
	}
	
	@Test
	public void applyRequestCharsetToContentType() throws Exception {
		MockResponseBuilder builder = new MockResponseBuilder();
		
		MockRequest request = new MockRequest("GET", "/something.txt", "some plain text content");
		request.setCharacterEncoding("iso-8859-1");
		MockResponse response = new MockResponse();
		
		builder.build(request, response, null, null);
		
		assertThat(builder.fCapturedTypes, is(nullValue()));
		assertThat(builder.fCapturedExtension, is(equalTo("txt")));
		assertThat(response.getContentType(), is(equalTo("plain/text; charset=iso-8859-1")));
	}

	private static final class MockResponseBuilder extends AbstractHttpResponseBuilder {
		
		public List<String> fCapturedTypes;
		public String fCapturedExtension;
		
		@Override
		protected void safelyBuild(HttpServletRequest request, HttpServletResponse response, Fields input, Fields output)
				throws IOException, RequestNotSupportedException {
			
			response.setContentType("plain/text" + withCharsetFrom(request));
		}

		@Override
		protected void checkAcceptType(List<String> acceptedTypes, String extension) throws RequestNotSupportedException {
			fCapturedTypes = acceptedTypes;
			fCapturedExtension = extension;
		}

	}

	
}
