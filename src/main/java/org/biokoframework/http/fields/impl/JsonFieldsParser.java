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

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.utils.fields.Fields;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 22, 2014
 *
 */
public class JsonFieldsParser extends AbstractFieldsParser {

	private static final String JSON_TYPE = "application/json";

	@Override
	public Fields safelyParse(HttpServletRequest request) throws RequestNotSupportedException {		
		Reader reader;
		try {
			reader = request.getReader();
			Writer writer = new StringWriter();
			IOUtils.copy(reader, writer);
			return Fields.fromJson(writer.toString());
		} catch (IOException exception) {
			// TODO log exception
			throw new RequestNotSupportedException(exception);
		}
	}

	@Override
	protected void checkContentType(String contentType) throws RequestNotSupportedException {
		if (!StringUtils.startsWith(contentType, JSON_TYPE)){
			throw badContentType(contentType, JSON_TYPE);
		}
	}

}
