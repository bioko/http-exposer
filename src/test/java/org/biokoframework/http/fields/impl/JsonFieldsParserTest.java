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

import org.apache.http.entity.ContentType;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.mock.MockRequest;
import org.biokoframework.utils.fields.Fields;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.biokoframework.utils.matcher.Matchers.matchesPattern;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 23, 2014
 *
 */
public class JsonFieldsParserTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public void simpleParsing() throws Exception {
		JsonFieldsParser parser = new JsonFieldsParser();
		
		MockRequest request = new MockRequest("POST", "/something", "{\"some\":\"thing\",  \"and\": 3}");
		request.setContentType(ContentType.APPLICATION_JSON.toString());
		
		Fields expected = new Fields(
				"some", "thing",
				"and", 3L
			);
		
		assertThat(parser.parse(request), is(equalTo(expected)));
	}
	
	@Test
	public void failBecauseOfWrongContentType() throws Exception {
		thrown.expect(RequestNotSupportedException.class);
		thrown.expectMessage(matchesPattern(".*type example not supported.*"));
		
		JsonFieldsParser parser = new JsonFieldsParser();
		MockRequest request = new MockRequest("POST", "/something", "Some content");
		request.setContentType("example");
		
		parser.parse(request);
	}

}
