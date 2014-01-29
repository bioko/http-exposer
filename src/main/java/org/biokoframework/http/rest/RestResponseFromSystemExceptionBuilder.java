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

package org.biokoframework.http.rest;

import javax.servlet.http.HttpServletResponse;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.exceptions.SystemNotFoundException;
import org.biokoframework.utils.fields.Fields;

public class RestResponseFromSystemExceptionBuilder {

	private Fields _output;
	
	private SystemNotFoundException _exception;

	public HttpServletResponse build(HttpServletResponse response) {
		response.setStatus(404);
		_output.put(GenericFieldNames.RESPONSE, _exception.getErrors());
		_output.put(GenericFieldNames.RESPONSE_CONTENT_TYPE, GenericFieldValues.JSON_CONTENT_TYPE);
		return response;
	}

	public RestResponseFromSystemExceptionBuilder setOutputFields(Fields output) {
		_output = output;
		return this;
	}

	public RestResponseFromSystemExceptionBuilder setException(SystemNotFoundException exception) {
		_exception = exception;
		return this;
	}

}
