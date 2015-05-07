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
import org.apache.commons.io.IOUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 22, 2014
 *
 */
public class JsonFieldsParser extends AbstractFieldsParser {

	private static final MediaType JSON_TYPE = MediaType.JSON_UTF_8.withoutParameters();

	@Override
	public Fields safelyParse(HttpServletRequest request) throws RequestNotSupportedException {		
		Reader reader;
		try {
            if (request.getCharacterEncoding() == null) {
                request.setCharacterEncoding("utf-8");
            }
            reader = request.getReader();
			return Fields.fromJson(IOUtils.toString(reader));
		} catch (IOException exception) {
			// TODO log exception
			throw new RequestNotSupportedException(exception);
		}
	}

	@Override
	protected void checkContentType(MediaType mediaType) throws RequestNotSupportedException {
		if (!isCompatibleWith(mediaType)){
			throw badContentType(mediaType, JSON_TYPE);
		}
	}

    @Override
    public boolean isCompatibleWith(MediaType mediaType) {
        return mediaType.withoutParameters().is(JSON_TYPE);
    }

}
