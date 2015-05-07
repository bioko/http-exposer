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
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.mock.MockRequest;
import org.biokoframework.http.mock.MockResponse;
import org.biokoframework.http.response.IResponseContentBuilder;
import org.biokoframework.utils.fields.Fields;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class HttpResponseBuilderTest {

    public static final MediaType PLAIN_TEXT = MediaType.PLAIN_TEXT_UTF_8.withoutParameters();

    @Test
	public void testNoExtensionOrAcceptUseRequestContentType() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, null);
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.<String, String>emptyMap(),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );
		
		MockRequest request = new MockRequest("POST", "/something", "some plain text content");
		request.setContentType(PLAIN_TEXT.toString());
		
		builder.build(request, new MockResponse(), null, null);

        assertThat(mockContentBuilder.fCapturedExtension, is(nullValue()));
        assertThat(mockContentBuilder.fCapturedTypes, contains(PLAIN_TEXT));
	}

	@Test
	public void testExtensionMismatchUseRequestContentType() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, "ext");
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.<String, String>emptyMap(),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );

		MockRequest request = new MockRequest("POST", "/something.txt", "some plain text content");
		request.setContentType(PLAIN_TEXT.toString());

		builder.build(request, new MockResponse(), null, null);

		assertThat(mockContentBuilder.fCapturedTypes, contains(PLAIN_TEXT));
		assertThat(mockContentBuilder.fCapturedExtension, is(equalTo("txt")));
	}

	@Test
	public void testNoExtensionUseAccept() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, "ext");
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.<String, String>emptyMap(),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );

        MockRequest request = new MockRequest("GET", "/something");
		request.setHeader("Accept", PLAIN_TEXT.toString());

		builder.build(request, new MockResponse(), null, null);

		assertThat(mockContentBuilder.fCapturedTypes, contains(PLAIN_TEXT));
		assertThat(mockContentBuilder.fCapturedExtension, is(nullValue()));
	}

	@Test
	public void testMismatchExtensionUseAccept() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, "ext");
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.<String, String>emptyMap(),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );

		MockRequest request = new MockRequest("GET", "/something.json");
		request.setHeader("Accept", PLAIN_TEXT.toString());

		builder.build(request, new MockResponse(), null, null);

		assertThat(mockContentBuilder.fCapturedTypes, contains(PLAIN_TEXT));
		assertThat(mockContentBuilder.fCapturedExtension, is(equalTo("json")));
	}

	@Test
	public void testOnlyExtension() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, null);
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.<String, String>emptyMap(),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );

		MockRequest request = new MockRequest("GET", "/something.txt", "some plain text content");

		builder.build(request, new MockResponse(), null, null);

		assertThat(mockContentBuilder.fCapturedTypes, is(emptyCollectionOf(MediaType.class)));
		assertThat(mockContentBuilder.fCapturedExtension, is(equalTo("txt")));
	}

//	@Test
//	public void applyDefaultCharsetToContentType() throws Exception {
//        MockBuilder mockContentBuilder = new MockBuilder(null, null);
//        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
//                Collections.<String, String>emptyMap(),
//                Collections.<IResponseContentBuilder>singletonList(mockContentBuilder)
//        );
//
//		MockRequest request = new MockRequest("GET", "/something.txt", "some plain text content");
//		MockResponse response = new MockResponse();
//
//		builder.build(request, response, null, null);
//
//		assertThat(mockContentBuilder.fCapturedTypes, is(emptyCollectionOf(MediaType.class)));
//		assertThat(mockContentBuilder.fCapturedExtension, is(equalTo("txt")));
//		assertThat(response.getContentType(), is(equalTo("plain/text; charset=utf-8")));
//	}
//
//	@Test
//	public void applyRequestCharsetToContentType() throws Exception {
//		MockResponseBuilder builder = new MockResponseBuilder();
//
//		MockRequest request = new MockRequest("GET", "/something.txt", "some plain text content");
//		request.setCharacterEncoding("iso-8859-1");
//		MockResponse response = new MockResponse();
//
//		builder.build(request, response, null, null);
//
//		assertThat(builder.fCapturedTypes, is(emptyCollectionOf(MediaType.class)));
//		assertThat(builder.fCapturedExtension, is(equalTo("txt")));
//		assertThat(response.getContentType(), is(equalTo("plain/text; charset=iso-8859-1")));
//	}

    @Test
    public void headerTest() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, null);
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.singletonMap("aField", "The-Header"),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );

        MockRequest httpRequest = new MockRequest("GET", "/something.json");
        MockResponse httpResponse = new MockResponse();

        Fields input = new Fields();
        Fields output = new Fields("aField", "hasAStringValue");

        builder.build(httpRequest, httpResponse, input, output);

        assertThat(httpResponse.getStatus(), is(200));
        assertThat(httpResponse.getHeader("The-Header"), is(equalTo("hasAStringValue")));
    }

    @Test
    public void forcedContentTypeTest() throws Exception {
        MockBuilder mockContentBuilder = new MockBuilder(null, null);
        HttpResponseBuilderImpl builder = new HttpResponseBuilderImpl(
                Collections.<String, String>emptyMap(),
                Collections.<IResponseContentBuilder>singleton(mockContentBuilder)
        );

        MockRequest httpRequest = new MockRequest("GET", "/something.html");
        MockResponse httpResponse = new MockResponse();

        Fields input = new Fields();
        Fields output = new Fields(
                "mediaType", MediaType.JSON_UTF_8,
                "RESPONSE", new Fields("a", "value"));

        builder.build(httpRequest, httpResponse, input, output);

        assertThat(mockContentBuilder.fCapturedTypes, contains(MediaType.JSON_UTF_8));
    }

//    @Test
//    public void failBecauseNoSupport() {
//
//    }

	private static final class MockBuilder implements IResponseContentBuilder {

        private final MediaType fAcceptableMediaType;
        private final String fAcceptableExtension;
        public List<MediaType> fCapturedTypes = new ArrayList<>();
		public String fCapturedExtension;

        public MockBuilder(MediaType acceptableMediaType, String acceptableExtension) {
            fAcceptableMediaType = acceptableMediaType;
            fAcceptableExtension = acceptableExtension;
        }

        @Override
        public boolean isCompatibleWith(MediaType mediaType) {
            fCapturedTypes.add(mediaType);

            if (fAcceptableMediaType == null) {
                return true;
            } else {
                return fAcceptableMediaType.is(mediaType);
            }
        }

        @Override
        public boolean isCompatibleWith(String requestExtension) {
            fCapturedExtension = requestExtension;
            if (fAcceptableExtension == null) {
                return true;
            } else {
                return fAcceptableExtension.equals(requestExtension);
            }
        }

        @Override
        public void build(HttpServletResponse response, Fields output) throws IOException, RequestNotSupportedException {

        }
    }

}
