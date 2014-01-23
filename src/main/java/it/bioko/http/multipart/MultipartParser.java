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

package it.bioko.http.multipart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;

public class MultipartParser {

	private byte[] _boundary;
	private byte[] _body;
	private String _contentType;
	private String _encoding;

	public MultipartParser(HttpServletRequest request) throws IOException {
		String contentType = request.getContentType();
		int boundaryIndex = contentType.indexOf("boundary=");
		_boundary = (contentType.substring(boundaryIndex + 9)).getBytes();
		_contentType = contentType.substring(0,boundaryIndex);
		
		_encoding = request.getCharacterEncoding();
		if (_encoding == null || _encoding.isEmpty())
			_encoding = Charset.defaultCharset().toString();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		IOUtils.copy(request.getInputStream(), outStream);
		_body = outStream.toByteArray();		
	}

	
		
	
	
	public MultipartParser(byte[] boundary, byte[] body, String encoding) {
		_encoding = encoding;
		if (_encoding == null || _encoding.isEmpty())
			_encoding = Charset.defaultCharset().toString();
		
		_boundary = boundary;
		_body = body;
	}
	
	public String getContentType() {
		return _contentType;
	}

	public List<RequestPart> getParts() throws IOException {		
		ArrayList<RequestPart> parts = new ArrayList<RequestPart>();

		ByteArrayInputStream input = new ByteArrayInputStream(_body);
		MultipartStream multipartStream = new MultipartStream(input, _boundary);
		
//		System.out.println(new String(_body));
		
		
		
		boolean nextPart = multipartStream.skipPreamble();
		while(nextPart) {
			String header = multipartStream.readHeaders();
            ByteArrayOutputStream bodyOutStream = new ByteArrayOutputStream();
            multipartStream.readBodyData(bodyOutStream);
			
            RequestPart part = new RequestPart(header, bodyOutStream.toByteArray(), _encoding);
			
			parts.add(part);
			nextPart = multipartStream.readBoundary();
		}


		return parts;
	}

}
