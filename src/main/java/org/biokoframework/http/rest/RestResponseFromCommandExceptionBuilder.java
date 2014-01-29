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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.biokoframework.http.rest.exception.HttpError;
import org.biokoframework.http.rest.exception.HttpResponseBuilder;
import org.biokoframework.http.rest.exception.HttpResponseExceptionFactory;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.exceptions.ErrorMessagesFactory;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

public class RestResponseFromCommandExceptionBuilder {
	
	private Fields _outputFields;
	private BiokoException _exception;

	public HttpServletResponse build(HttpServletResponse response) {
		
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(_exception);
		
		List<ErrorEntity> body = httpError.body();
		if (body != null && !body.isEmpty()) {
			response.setStatus(httpError.status());
		} else {
			httpError = internalServerErrorCapturedException();
			response.setStatus(httpError.status());
		}
		_outputFields.put(GenericFieldNames.RESPONSE, httpError.body());
		_outputFields.put(GenericFieldNames.RESPONSE_CONTENT_TYPE, GenericFieldValues.JSON_CONTENT_TYPE);
		return response;
	}

	private HttpError internalServerErrorCapturedException() {
		// 500 - Internal Server Error
		List<ErrorEntity> errorWithEceptionMessage = _exception.getErrors();
		if (_exception.exception() != null) {
			String[] messageChuncks = ErrorMessagesFactory.createMap().get(FieldNames.CONTAINER_EXCEPTION_CODE);
			String message = new StringBuilder().
					append(messageChuncks[0]).
					append(_exception.exception().getClass().getSimpleName()).
					append(messageChuncks[1]).
					append(_exception.exception().getMessage()).
					append(" [").
					append(_exception.exception().getStackTrace()[0].toString()).
					append(" ...]").
					toString();
			
			Fields fields = Fields.single(ErrorEntity.ERROR_MESSAGE, message);
			fields.put(ErrorEntity.ERROR_CODE, FieldNames.CONTAINER_EXCEPTION_CODE);
			errorWithEceptionMessage.add(new ErrorEntity(fields));
		}
		return new HttpError(500, errorWithEceptionMessage);
	}		
	
	public RestResponseFromCommandExceptionBuilder setInputFields(Fields inputFields) {
		return this;
	}

	public RestResponseFromCommandExceptionBuilder setOutputFields(Fields outputFields) {
		_outputFields = outputFields;
		return this;
	}
	
	public RestResponseFromCommandExceptionBuilder setException(BiokoException exception) {
		_exception = exception;
		return this;
	}
	
}
