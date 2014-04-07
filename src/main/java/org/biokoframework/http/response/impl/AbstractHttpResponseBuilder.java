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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.response.IHttpResponseBuilder;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public abstract class AbstractHttpResponseBuilder implements IHttpResponseBuilder {

    protected static final String JOLLY_TYPE = "*/*";

	private static final String ACCEPT = "Accept";
	private final Pattern fExtensionPattern = Pattern.compile("\\.([a-zA-Z0-9]+)$");
	
	@Override
	public final void build(HttpServletRequest request, HttpServletResponse response, Fields input, Fields output) throws IOException, RequestNotSupportedException {
		if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
			checkAcceptType(Arrays.asList(request.getContentType()), getRequestExtension(request.getPathInfo()));
		} else {
			checkAcceptType(getAccept(request), getRequestExtension(request.getPathInfo()));
		}
		
		response.setStatus(HttpStatus.SC_OK);
		
		safelyBuild(request, response, input, output);
	}

	protected abstract void safelyBuild(HttpServletRequest request, HttpServletResponse response, Fields input, Fields output) throws IOException, RequestNotSupportedException;

	/**
	 * @param acceptedTypes - The list contained in the header "Accept" or the "Content-Type", depending on the request type (or null if not present) 
	 * @param extension - Request extension e.g. {@code http://your.site/page.json} has extension {@code json} (or null if not present)
	 * @throws RequestNotSupportedException 
	 */
	protected abstract void checkAcceptType(List<String> acceptedTypes, String extension) throws RequestNotSupportedException;
	
	protected String getRequestExtension(String pathInfo) {
		Matcher matcher = fExtensionPattern.matcher(pathInfo);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	protected List<String> getAccept(HttpServletRequest request) {
		String accept = request.getHeader(ACCEPT);
		if (!StringUtils.isEmpty(accept)) {
			return Arrays.asList(accept.split(","));
		}
		return null;
	}

	/**
	 * @param request
	 * @return the string "; charset=" followed by the charset used in the request or "utf-8"
	 */
	protected String withCharsetFrom(HttpServletRequest request) {
		String charset = request.getCharacterEncoding();
		if (StringUtils.isEmpty(charset)) {
			return "; charset=" + "utf-8";
		} else {
			return "; charset=" + charset;
		}
	}

	
}
