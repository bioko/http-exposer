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

package org.biokoframework.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class RequestWrapper extends HttpServletRequestWrapper {

	private static final String CRLF = "\r\n";
	private byte[] body;
	private HttpServletRequest _request;

	public RequestWrapper(HttpServletRequest request) throws IOException {
		super(request);

		_request = request;
		
//		body = "VACCA IN FIAMME, DOBBIAMO TROVARE IL MODO DI LOGGARLO!";
		InputStream inputStream = request.getInputStream();		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		IOUtils.copy(inputStream, outStream);
		body = outStream.toByteArray();
		
	}


	@Override  
	public ServletInputStream getInputStream () throws IOException {          
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

		ServletInputStream inputStream = new ServletInputStream() {  
			public int read () throws IOException {  
				return byteArrayInputStream.read();  
			}  
		};

		return inputStream;  
	} 


	public String report() {
		StringBuffer report = new StringBuffer("REQUEST:").append(CRLF);
		report.append("getAuthType: ").append(_request.getAuthType()).append(CRLF);
		report.append("getCharacterEncoding: ").append(_request.getCharacterEncoding()).append(CRLF);
		report.append("getContentLength: ").append(_request.getContentLength()).append(CRLF);
		report.append("getContentType: ").append(_request.getContentType()).append(CRLF);
		report.append("getContextPath: ").append(_request.getContextPath()).append(CRLF);
		report.append("getLocalAddr: ").append(_request.getLocalAddr()).append(CRLF);
		report.append("getLocalName: ").append(_request.getLocalName()).append(CRLF);
		report.append("getLocalPort: ").append(_request.getLocalPort()).append(CRLF);
		report.append("getMethod: ").append(_request.getMethod()).append(CRLF);
		report.append("getQueryString: ").append(_request.getQueryString()).append(CRLF);
		report.append("getPathInfo: ").append(_request.getPathInfo()).append(CRLF);
		report.append("getPathTranslated: ").append(_request.getPathTranslated()).append(CRLF);
		report.append("getRequestURI: ").append(_request.getRequestURI()).append(CRLF);
		report.append("requestBody: ").append(new String(body)).append(CRLF);
		return report.toString();
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		
		String queryString = _request.getQueryString();

		String encoding = _request.getCharacterEncoding();
		if (encoding == null) {
			encoding = Charset.defaultCharset().toString();
		}
		
		try {
			for (String aParameterEntry : queryString.split("&")) {
				String[] splitted = aParameterEntry.split("=");
				parameterMap.put(URLDecoder.decode(splitted[0], encoding), new String[] { URLDecoder.decode(splitted[1], encoding) });
			}
			return parameterMap;
		} catch (Exception exception) {
			return _request.getParameterMap();
		}
	}

}
