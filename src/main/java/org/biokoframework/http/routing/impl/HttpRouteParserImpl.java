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

package org.biokoframework.http.routing.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.biokoframework.http.fields.IHttpFieldsParser;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.routing.IHttpRouteParser;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.RouteNotSupportedException;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.utils.fields.Fields;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 15, 2014
 *
 */
public class HttpRouteParserImpl implements IHttpRouteParser {

	private final IHttpFieldsParser fFieldsParser;
	private final Map<String, String> fHeadersMapping;
	
	@Inject
	public HttpRouteParserImpl(IHttpFieldsParser fieldsParser, @Named("httpHeaderToFieldsMap") Map<String, String> headersMapping) {
		fFieldsParser = fieldsParser;
		fHeadersMapping = headersMapping;
	}
	
	@Override
	public IRoute getRoute(HttpServletRequest request) throws RouteNotSupportedException {
		return new RouteImpl(getMethod(request), getPath(request), getFields(request));
	}

	private Fields getFields(HttpServletRequest request) throws RouteNotSupportedException {
		if (request.getContentLength() > 0) {
			try {
				return fFieldsParser.parse(request).putAll(extractHeaders(request));
			} catch (RequestNotSupportedException exception) {
				throw new RouteNotSupportedException(exception);
			}
		}
		return null;
	}

	private Fields extractHeaders(HttpServletRequest request) {
		Fields headerFields = new Fields();
		for (Entry<String, String> entry : fHeadersMapping.entrySet()) {
			String value = request.getHeader(entry.getKey());
			if (value != null) {
				headerFields.put(entry.getValue(), value);
			}
		}
		return headerFields;
	}

	private HttpMethod getMethod(HttpServletRequest request) throws RouteNotSupportedException {
		try {
			String method = request.getMethod();
			return HttpMethod.valueOf(method);
		} catch (IllegalArgumentException exception) {
			throw new RouteNotSupportedException(exception);
		}
	}

	private String getPath(HttpServletRequest request) {
		String path = request.getPathInfo();
		if (!path.endsWith("/")) {
			return path + "/";
		}
		return path;
	}

}
