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

import com.google.common.net.MediaType;
import org.biokoframework.http.mock.MockRequest;
import org.biokoframework.http.mock.MockResponse;
import org.biokoframework.utils.fields.Fields;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class JsonResponseBuilderImplTest {

	@Test
	public void simpleTest() throws Exception {
		
		JsonResponseBuilderImpl builder = new JsonResponseBuilderImpl();
		
		MockRequest httpRequest = new MockRequest("GET", "/something.json/");
		MockResponse httpResponse = new MockResponse();
		
		Fields input = new Fields();
		
		Fields response = new Fields(
				"something", "hasAStringValue",
				"somethingElse", false);
		
		Fields output = new Fields(
				"RESPONSE", response);
		
		builder.build(httpResponse, output);
		
		assertThat(httpResponse.getContentType(), is(equalTo(MediaType.JSON_UTF_8.toString())));
		assertThat(httpResponse.toString(), is(equalTo(response.toJSONString())));
		
	}

    @Test
    public void acceptOnlyJsonExtension() throws Exception {
        JsonResponseBuilderImpl builder = new JsonResponseBuilderImpl();

        assertThat(builder.isCompatibleWith("json"), is(true));
        assertThat(builder.isCompatibleWith("js"), is(true));

        assertThat(builder.isCompatibleWith("txt"), is(false));
    }

    @Test
    public void acceptJsonMimeAndTheLike() throws Exception {
        JsonResponseBuilderImpl builder = new JsonResponseBuilderImpl();

        assertThat(builder.isCompatibleWith(MediaType.JSON_UTF_8), is(true));
        assertThat(builder.isCompatibleWith(MediaType.ANY_APPLICATION_TYPE), is(true));
        assertThat(builder.isCompatibleWith(MediaType.ANY_TYPE), is(true));

        assertThat(builder.isCompatibleWith(MediaType.ANY_TEXT_TYPE), is(false));
    }

}
