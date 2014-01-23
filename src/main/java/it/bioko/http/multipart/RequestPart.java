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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;

public class RequestPart {

	private static final String BASE64 = "base64";

	private String _headers;
	private byte[] _body;
	private String _contentType;
	private String _name;
	private String _filename;
	private String _fileEncoding;

	private String _encoding;




	public RequestPart(String headers, byte[] body, String encoding) throws IOException {
		_headers = headers;
		_encoding = encoding;
		parseHeaders();

		_body = decode(body);


	}







	@Override
	public String toString() {
		if (isFile()) {
			return "RequestPart [_headers=" + _headers + ", _body=FILE CONTENT"
					+ ", _contentType=" + _contentType
					+ ", _name=" + _name + ", _filename=" + _filename
					+ ", _fileEncoding=" + _fileEncoding + "]";
		} else {
			return "RequestPart [_headers=" + _headers + ", _body="
					+ new String(_body) + ", _contentType=" + _contentType
					+ ", _name=" + _name + ", _filename=" + _filename
					+ ", _fileEncoding=" + _fileEncoding + "]";
		}
	}







	private byte[] decode(byte[] body) throws IOException {
		if (_fileEncoding!=null && _fileEncoding.equalsIgnoreCase(BASE64)) {
			Base64InputStream decoder = new Base64InputStream(new ByteArrayInputStream(body));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copy(decoder, out);
			return out.toByteArray();
		} else
			return body;
	}

	public String getContentType() {
		return _contentType;		
	}

	public InputStream getInputStream() {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(_body);
		return inputStream;
	}

	public String getName() {		
		return _name;
	}

	public long getSize() {
		return _body.length;
	}

	public boolean isFile() {
		if (_filename!=null)
			return true;
		else
			return false;
	}

	public String getBodyAsString() throws UnsupportedEncodingException {
		return new String(_body, _encoding);
	}

	public String getFilename() {
		return _filename;
	}

	public String getFileEncoding() {
		return _fileEncoding;
	}


	private void parseHeaders() {
		Pattern contentTypePattern = Pattern.compile(".*[\\s|^]Content-Type:\\s*(\\S*).*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Pattern namePattern = Pattern.compile(".*\\sname=\"(.+?)\".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Pattern filenamePattern = Pattern.compile(".*filename=\"(.*)\".*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Pattern fileEncodingPattern = Pattern.compile(".*[\\s|^]Content-Transfer-Encoding:\\s*(\\S*).*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);



		Matcher contentTypeMatcher = contentTypePattern.matcher(_headers);
		if (contentTypeMatcher.matches()) 			
			_contentType = contentTypeMatcher.group(1);		

		Matcher nameMatcher = namePattern.matcher(_headers);
		if (nameMatcher.matches()) 		
			_name = nameMatcher.group(1);		

		Matcher filenameMatcher = filenamePattern.matcher(_headers);
		if (filenameMatcher.matches()) 						
			_filename = filenameMatcher.group(1);		

		Matcher fileEncodingMatcher = fileEncodingPattern.matcher(_headers);
		if (fileEncodingMatcher.matches()) 						
			_fileEncoding = fileEncodingMatcher.group(1);


	}


}
