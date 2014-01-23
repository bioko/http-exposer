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

package it.bioko.http.requestbuilder;

import it.bioko.http.FieldsFromRequestBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class FieldsFromNonMultipartRequestRequestBuilder extends
		FieldsFromRequestBuilder {

	@Override
	public FieldsFromNonMultipartRequestRequestBuilder setRequest(HttpServletRequest request) {
		super.setRequest(request);
		setBody(readBody(request));
		setParameters(request.getParameterMap());
		return this;
	}

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setParameters(Map<String, String[]> parameters);

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setMethod(String anHttpMethod);

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setPathInfo(String aPathInfo);

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setBody(String body);

	protected String readBody(HttpServletRequest httpRequest) {
		String result = "";
		String charsetName = getRequestCharset(httpRequest);
		try {
			InputStream inputStream = httpRequest.getInputStream();
			StringWriter writer = new StringWriter(1024);
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charsetName)));
			int c;
			while ((c = inputReader.read()) != -1) {
				writer.write(c);
			}
			
			result = writer.toString();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		}
		
		return result;
	}

}
