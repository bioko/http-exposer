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

package org.biokoframework.http.requestbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.biokoframework.http.FieldsFromRequestBuilder;
import org.biokoframework.http.multipart.MultipartParser;
import org.biokoframework.http.multipart.RequestPart;
import org.biokoframework.system.KILL_ME.commons.GenericCommandNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

public class FieldsFromMultipartRequestBuilder extends FieldsFromRequestBuilder implements FieldsFromPartBuilder {

	private Map<String, FieldsFromPartBuilder> _buildersMap;
	private HashMap<RequestPart, FieldsFromPartBuilder> _partsMap = new HashMap<RequestPart, FieldsFromPartBuilder>();

	private HttpServletRequest _request;
	private String _pathInfo;
	private String _method;
	private Object _headerToken;
	private Object _basicAuthentication;

	public FieldsFromMultipartRequestBuilder(Map<String, FieldsFromPartBuilder> buildersMap) {
		_buildersMap = buildersMap;
	}
	
	@Override
	public Fields build() {
		String[] splittedPath = _pathInfo.split("/");
		String entity = splittedPath[1];
		String entityId = splittedPath.length > 2 ? splittedPath[2] : null;
			
		Fields result = Fields.single(FieldNames.COMMAND_NAME, GenericCommandNames.composeCommandName(_method, entity));
		if (entityId != null) {
			result.put(DomainEntity.ID, entityId);
		}
		
		if (_headerToken != null && !result.contains(GenericFieldNames.AUTH_TOKEN)) {
			result.put(GenericFieldNames.AUTH_TOKEN, _headerToken);
		}
		if (_basicAuthentication != null) {
			result.put(GenericFieldNames.BASIC_AUTHENTICATION, _basicAuthentication);
		}
		
		for (Entry<RequestPart, FieldsFromPartBuilder> anEntry : _partsMap.entrySet()) {
			result.putAll(anEntry.getValue().build(anEntry.getKey(), getRequestCharset(_request)));
		}
		_partsMap.clear();
		return result;
	}

	@Override
	public FieldsFromMultipartRequestBuilder setRequest(HttpServletRequest request) {
		super.setRequest(request);
		_request = request;
		return this;
	}

	@Override
	public boolean canHandle(HttpServletRequest request) {
		if (request.getContentType() == null || 
				!request.getContentType().startsWith(GenericFieldValues.MULTIPART_CONTENT_TYPE)) {
			
			return false;
		}
		
		Collection<RequestPart> parts = getParts(request);
		if (parts == null) {
			return false;
		} else {
			for (RequestPart aPart : parts) {
				_partsMap.put(aPart, this);
				if (aPart.getContentType()!=null) {
//					if (getFileName(aPart)!=null)
					_partsMap.put(aPart, _buildersMap.get(RequestBuilders.BINARY_PART_BUILDER));					
				}
						
				// TODO fare unit test e migliorare quando ci saranno builders specifici
//				for (FieldsFromPartBuilder aBuilder : _buildersMap.values()) {
//					if (getFileName(aPart)!=null && aPart.getContentType() != null && aBuilder.canHandle(aPart)) {
//						_partsMap.put(aPart, aBuilder);
//						continue;
//					}
//				}
			}
			return true;
		}
	}

	private Collection<RequestPart> getParts(HttpServletRequest request) {
		Collection<RequestPart> parts = null;
		try {
			// parts = request.getParts();
			MultipartParser multipartParser = new MultipartParser(request);			
			parts = multipartParser.getParts();
		} catch (Exception exception) {
			Loggers.engagedInterface.error("Parsing multipart request content", exception);
			parts = null;
		}
		return parts;
	}

	@Override
	protected FieldsFromRequestBuilder setPathInfo(String pathInfo) {
		_pathInfo = pathInfo;
		return this;
	}

	@Override
	protected FieldsFromRequestBuilder setMethod(String method) {
		_method = method;
		return this;
	}

	@Override
	public Fields build(RequestPart part, String charset) {
		StringWriter writer = new StringWriter();
		try {
			InputStream inputStream = part.getInputStream();
			IOUtils.copy(inputStream, writer, charset);
//			inputStream.close();
		} catch (IOException exception) {
			Loggers.engagedInterface.error("Parsing part", exception);
			return new Fields();
		}
		
		return Fields.single(part.getName(), writer.toString());
	}
	
	@Override
	public boolean canHandle(RequestPart part) {
		return true;
	}

	@Override
	protected FieldsFromMultipartRequestBuilder setHeaderToken(String headerToken) {
		_headerToken = headerToken;
		return this;
	}

	@Override
	protected FieldsFromMultipartRequestBuilder setAuthentication(String authentication) {
		_basicAuthentication = authentication;
		return this;
	}

	
//	private String getFileName(Part part) {
//		for (String cd : part.getHeader("content-disposition").split(";")) {
//			if (cd.trim().startsWith("filename")) {
//				return cd.substring(cd.indexOf('=') + 1).trim()
//						.replace("\"", "");
//			}
//		}
//		return null;
//	}
	
}
