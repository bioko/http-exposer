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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
public class HttpResponseBuilderImplTest {

	@Test
	public void simpleTest() throws Exception {
		
		HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl();
		
		MockRequest httpRequest = new MockRequest("GET", "/something.json");
		MockResponse httpResponse = new MockResponse();
		
		Fields input = new Fields();
		
		Fields response = new Fields(
				"something", "hasAStringValue",
				"somethingElse", false);
		
		Fields output = new Fields(
				"RESPONSE", response);
		
		builder.build(httpRequest, httpResponse, input, output);
		
		assertThat(httpResponse.getStatus(), is(200));
		assertThat(httpResponse.getContentType(), is(equalTo("application/json; charset=utf-8")));
		assertThat(httpResponse.toString(), is(equalTo(response.toJSONString())));
		
	}
	
}
