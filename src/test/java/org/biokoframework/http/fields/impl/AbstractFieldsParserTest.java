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


package org.biokoframework.http.fields.impl;

import com.google.common.net.MediaType;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.mock.MockRequest;
import org.biokoframework.utils.fields.Fields;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 24, 2014
 *
 */
public class AbstractFieldsParserTest {

	@Test
	public void testContentType() throws Exception {
		MockFieldsParser parser = new MockFieldsParser();
		
		MockRequest request = new MockRequest("POST", "/something", "some plain text content");
		request.setContentType("text/plain");
		
		parser.parse(request);
		
		assertThat(parser.getCapturedRequest(), is(theInstance((HttpServletRequest)request)));
		assertThat(parser.getCapturedMediaType(), is(equalTo(MediaType.PLAIN_TEXT_UTF_8.withoutParameters())));
	}
	
	@Test
	public void testContentTypeNotPresent() throws Exception {
		MockFieldsParser parser = new MockFieldsParser();
		
		MockRequest request = new MockRequest("POST", "/something.txt", "some plain text content");
		
		parser.parse(request);
		
		assertThat(parser.getCapturedRequest(), is(theInstance((HttpServletRequest)request)));
		assertThat(parser.getCapturedMediaType(), is(nullValue()));
	}
	
	private final class MockFieldsParser extends AbstractFieldsParser {

		private HttpServletRequest fRequest;
		private MediaType fMediaType;

		@Override
		protected Fields safelyParse(HttpServletRequest request) throws RequestNotSupportedException {
			fRequest = request;
			return null;
		}

		@Override
		protected void checkContentType(MediaType mediaType) throws RequestNotSupportedException {
			fMediaType = mediaType;
		}
		
		public MediaType getCapturedMediaType() {
			return fMediaType;
		}
		
		public HttpServletRequest getCapturedRequest() {
			return fRequest;
		}

        @Override
        public boolean isCompatible(MediaType mediaType) {
            return true;
        }
    }

}
