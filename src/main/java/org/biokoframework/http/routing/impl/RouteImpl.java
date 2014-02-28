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

import org.apache.commons.lang3.ObjectUtils;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.utils.fields.Fields;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 15, 2014
 *
 */
public class RouteImpl implements IRoute {

	private final HttpMethod fHttpMethod;
	private final String fPath;
	private final Fields fFields;

	public RouteImpl(HttpMethod httpMethod, String path, Fields fields) {
		fHttpMethod = httpMethod;
		fPath = path;
		
		fFields = ObjectUtils.defaultIfNull(fields, new Fields());
	}
	
	@Override
	public HttpMethod getMethod() {
		return fHttpMethod;
	}

	@Override
	public String getPath() {
		return fPath;
	}
	
	@Override
	public Fields getFields() {
		return fFields;
	}

	@Override
	public void matchedParameters(Fields parameters) {
		fFields.putAll(parameters);
	}

}
