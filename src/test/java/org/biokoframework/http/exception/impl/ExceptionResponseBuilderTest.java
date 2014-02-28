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

package org.biokoframework.http.exception.impl;

import static org.biokoframework.utils.matcher.Matchers.matchesPattern;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.biokoframework.http.mock.MockResponse;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 18, 2014
 *
 */
public class ExceptionResponseBuilderTest {

	@SuppressWarnings("serial")
	private static final class MockException extends BiokoException {

		public MockException(List<ErrorEntity> errors) {
			super(errors);
		}

		public MockException(ErrorEntity errorEntity) {
			super(errorEntity);
		}

		public MockException(Exception exception) {
			super(exception);
		}

	}

	private Map<Class<? extends BiokoException>, Integer> fCodesMap;

	@Before
	public void prepareMap() {
		fCodesMap = new HashMap<Class<? extends BiokoException>, Integer>();
		fCodesMap.put(MockException.class, 626);
		fCodesMap.put(CommandException.class, 747);
	}
	
	@Test
	public void testBiokoExceptionWithErrorEntity() throws Exception {
		
		ExceptionResponseBuilderImpl builder = new ExceptionResponseBuilderImpl(fCodesMap);
		
		MockResponse mockResponse = new MockResponse();
		MockException mockException = new MockException(new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, 12345,
				ErrorEntity.ERROR_MESSAGE, "Failed at life"
		)));
		
		mockResponse = (MockResponse) builder.build(mockResponse, mockException, null, null);
		
		assertThat(mockResponse.getContentType(), is(equalTo(ContentType.APPLICATION_JSON.toString())));
		assertThat(mockResponse.getStatus(), is(equalTo(626)));
		assertThat(mockResponse, hasToString(equalTo(JSONValue.toJSONString(mockException.getErrors()))));
	}
	
	@Test
	public void testNestedBiokoException() throws IOException {
		ExceptionResponseBuilderImpl builder = new ExceptionResponseBuilderImpl(fCodesMap);
		
		MockResponse mockResponse = new MockResponse();
		MockException mockException = new MockException(new CommandException(new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, 12345,
				ErrorEntity.ERROR_MESSAGE, "Failed at life"
		))));
		
		mockResponse = (MockResponse) builder.build(mockResponse, mockException, null, null);
		
		assertThat(mockResponse.getContentType(), is(equalTo(ContentType.APPLICATION_JSON.toString())));
		assertThat(mockResponse.getStatus(), is(equalTo(626)));
		assertThat(mockResponse, hasToString(
				matchesPattern("^.*An unexpected exception as been captured [\\w\\.\\$]+MockException .*Caused by [\\w\\.\\$]+CommandException.*$")));
		
	}
	
	@Test
	public void testNonBiokoException() throws IOException {
		ExceptionResponseBuilderImpl builder = new ExceptionResponseBuilderImpl(fCodesMap);
		
		MockResponse mockResponse = new MockResponse();
		MockException mockException = new MockException(new UnsupportedOperationException("Something"));
		
		mockResponse = (MockResponse) builder.build(mockResponse, mockException, null, null);
		
		assertThat(mockResponse.getContentType(), is(equalTo(ContentType.APPLICATION_JSON.toString())));
		assertThat(mockResponse.getStatus(), is(equalTo(626)));
		assertThat(mockResponse, hasToString(
				matchesPattern("^.*An unexpected exception as been captured [\\w\\.\\$]+MockException .*Caused by [\\w\\.\\$]+UnsupportedOperationException.*$")));
	}

}
