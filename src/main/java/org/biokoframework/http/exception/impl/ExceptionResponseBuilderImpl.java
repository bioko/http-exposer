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

package org.biokoframework.http.exception.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.biokoframework.http.exception.IExceptionResponseBuilder;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONValue;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 18, 2014
 *
 */
public class ExceptionResponseBuilderImpl implements IExceptionResponseBuilder {

	private static final Logger LOGGER = Logger.getLogger(ExceptionResponseBuilderImpl.class);
	private final Map<Class<? extends BiokoException>, Integer> fStatusCodesMap;
	
	@Inject
	public ExceptionResponseBuilderImpl(@Named("statusCodeMap") Map<Class<? extends BiokoException>, Integer> statusCodesMap) {
		fStatusCodesMap = Collections.unmodifiableMap(statusCodesMap);
	}
	
	@Override
	public HttpServletResponse build(HttpServletResponse response, Exception exception, Fields input, Fields output) throws IOException {	
		response.setContentType(ContentType.APPLICATION_JSON.toString());
		
		LOGGER.info("Before choosing");
		response.setStatus(chooseStatusCode(exception));
		LOGGER.info("After choosing");
		
		List<ErrorEntity> errors = null;
		if (exception instanceof BiokoException) {
			errors = ((BiokoException) exception).getErrors();
		}
		
		if (errors == null ||errors.isEmpty()) {
			errors = new ArrayList<>();
			StringBuilder message = new StringBuilder()
				.append("An unexpected exception as been captured ").append(descriptionOf(exception));
				
			ErrorEntity entity = new ErrorEntity();
			entity.setAll(new Fields(
					ErrorEntity.ERROR_CODE, FieldNames.CONTAINER_EXCEPTION_CODE,
					ErrorEntity.ERROR_MESSAGE, message.toString()));
			errors.add(entity);
		}
		IOUtils.copy(new StringReader(JSONValue.toJSONString(errors)), response.getWriter());
		
		return response;
	}

	private String descriptionOf(Throwable throwable) {
		StringBuilder message = new StringBuilder().append(throwable.getClass().getName());
		if (!StringUtils.isEmpty(throwable.getMessage())) {
			message.append(" with message '" ).append(throwable.getMessage()).append("'");
		}
		message.append(" [").append(throwable.getStackTrace()[0]).append("]");
		if (throwable.getCause() != null) {
			message.append(" Caused by " + descriptionOf(throwable.getCause()));
		}
		return message.toString();
	}

	private int chooseStatusCode(Throwable cause) {
		while (cause != null && cause != cause.getCause()) {
			if (cause instanceof BiokoException) { 
				Integer code = fStatusCodesMap.get(cause.getClass());
				if (code != null) {
					return code;
				}
			}
			cause = cause.getCause();
		}
		return HttpStatus.SC_INTERNAL_SERVER_ERROR;
	}

}
