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

import org.biokoframework.http.fields.IHttpFieldsParser;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 22, 2014
 *
 */
public abstract class AbstractFieldsParser implements IHttpFieldsParser {
	
	@Override
	public final Fields parse(HttpServletRequest request) throws RequestNotSupportedException {
		checkContentType(request.getContentType());
		
		return safelyParse(request);
	}

	protected abstract Fields safelyParse(HttpServletRequest request) throws RequestNotSupportedException;

	/**
	 * Check if the parser can handle the request content
	 * 
	 * @param contentType The content type as in the request
	 * @throws RequestNotSupportedException If it cannot
	 */
	protected abstract void checkContentType(String contentType) throws RequestNotSupportedException;

	protected RequestNotSupportedException badContentType(String foundContentType, String expectedContentType) {
		String message = new StringBuilder()
				.append("Request type ").append(foundContentType).append(" not supported. ")
				.append(expectedContentType).append(" expected.").toString();
		
		ErrorEntity entity = new ErrorEntity();
		entity.setAll(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.UNSUPPORTED_FORMAT_CODE,
				ErrorEntity.ERROR_MESSAGE, message));
		return new RequestNotSupportedException(entity);
	}
}
