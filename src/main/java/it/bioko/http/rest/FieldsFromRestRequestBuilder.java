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

package it.bioko.http.rest;

import it.bioko.http.requestbuilder.FieldsFromNonMultipartRequestRequestBuilder;
import it.bioko.system.KILL_ME.commons.GenericCommandNames;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.entity.EntityClassNameTranslator;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class FieldsFromRestRequestBuilder extends FieldsFromNonMultipartRequestRequestBuilder {

	private String _httpMethod;
	private String _pathInfo;
	private String _body;
	private Map<String, String[]> _parameters;
	private String _headerToken;
	private String _basicAuthentication;
	private static final int ENTITY_PATH_POSITION = 1;

	@Override
	public boolean canHandle(HttpServletRequest request) {		
		if (request.getContentType() == null || 
				request.getContentType().startsWith(GenericFieldValues.JSON_CONTENT_TYPE)) {
			return true;
		}
		
		long contentLength = Long.parseLong(request.getHeader(GenericFieldNames.CONTENT_LENGTH_HEADER));
		return contentLength == 0;
	}

	@Override
	public Fields build() {
		Loggers.engagedInterface.info("requestBody: " + _body + "\n");
		
		String afterEntity = null;
		String entity = "";
		if (!StringUtils.isEmpty(_pathInfo)) {
			String[] splittedPath = _pathInfo.split("/");
			if (splittedPath.length > 0) {
				entity = splittedPath[1];
				if (splittedPath.length > 2) {
					afterEntity = splittedPath[2];
				}
			}
		}
		
		Fields result = null;
		
		if (_httpMethod.equals(HttpMethod.POST.toString()) || _httpMethod.equals(HttpMethod.PUT.toString()))
			result = post(entity, afterEntity, _body, _httpMethod);
		
		else if (_httpMethod.equals(HttpMethod.GET.toString()) || _httpMethod.equals(HttpMethod.DELETE.toString()) 
				|| _httpMethod.equals(HttpMethod.HEAD.toString()))
			
			result = get(entity, afterEntity, _body, _httpMethod, _parameters);
		
		else if (_httpMethod.equals(HttpMethod.OPTIONS.toString()))
			result = options(entity, afterEntity, _body, _httpMethod, _parameters);
		
		// TODO MATTO usare reflection per invocare i metodi post, get, options
//		HashMap<String, String> methodToFieldsHandler = new LinkedHashMap<String, String>();
//		methodToFieldsHandler.put("POST", "post");
//		methodToFieldsHandler.put("PUT", "post");
//		methodToFieldsHandler.put("GET", "get");
//		methodToFieldsHandler.put("DELETE", "get");
//		methodToFieldsHandler.put("OPTIONS", "options");
		
		if (_headerToken != null && !result.contains(GenericFieldNames.AUTH_TOKEN)) {
			result.put(GenericFieldNames.AUTH_TOKEN, _headerToken);
		}
		if (_basicAuthentication != null) {
			result.put(GenericFieldNames.BASIC_AUTHENTICATION, _basicAuthentication);
		}
		
		result.put(FieldNames.COMMAND_NAME, createCommandNameFrom(_httpMethod, _pathInfo, ENTITY_PATH_POSITION));
		
		return result;
	}
	
	private String createCommandNameFrom(String httpMethod, String pathInfo, int entityPathPosition) {
		StringBuilder commandName = new StringBuilder().
				append(httpMethod.toUpperCase()).append("_"); 
		if (!StringUtils.isEmpty(pathInfo)) {
			String[] splitted = pathInfo.split("/");
			if (splitted.length > 0) {
				commandName.append(splitted[entityPathPosition]);
			}
		}
		Loggers.engagedInterface.info("restCommandName: " + commandName );
		return commandName.toString();
	}

	private Fields get(String entity, String entityId, String body, String httpMethod, Map<String, String[]> parameters) {
		Fields result = Fields.empty();
		insertId(result, entity, entityId);
		result.putAll(fieldsForParameterMap(parameters));
		return result;
	}

	protected void insertId(Fields result, String entity, String entityId) {
//		try {
			if (entityId != null) {
				// String xFieldName = retrieveFieldName(entity.toUpperCase(), "id".toUpperCase());
				// result.put(xFieldName, entityId);
				result.put(DomainEntity.ID, entityId);
			}
			
			
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private Fields fieldsForParameterMap(Map<String, String[]> parameters) {
		Fields fields = Fields.empty();
		for (Entry<String, String[]> aParameter : parameters.entrySet()) {
			if (aParameter.getValue().length == 1) {
				fields.put(aParameter.getKey(), aParameter.getValue()[0]);
			} else { 
				Loggers.engagedInterface.error(new StringBuilder()
						.append("URL parameter ")
						.append(aParameter.getKey())
						.append(" expected one value, got ")
						.append(aParameter.getValue().length)
						.append(" values"));
			}
		}
		return fields;
	}

	private Fields post(String entity, String action, String body, String httpMethod) {
		Fields result = Fields.empty();
		result.fromJson(body);
		insertId(result, entity, action);
		return result;
	}
	
	private Fields options(String entity, String action, String body, String httpMethod, Map<String, String[]> parameters) {
		Fields result = Fields.empty();
		if (entity.toUpperCase().equals(GenericCommandNames.COMMAND_INVOCATION_INFO)) {
			result.put(FieldNames.COMMAND_INFORMATION_ON, action);
		} else {
			result.put(FieldNames.COMMAND_INFORMATION_ON, entity);
		}
		
		for (Entry<String, String[]> aParameter : parameters.entrySet()) {
			if (aParameter.getValue().length == 1) {
				result.put(aParameter.getKey(), aParameter.getValue()[0]);
			} else { 
				Loggers.engagedInterface.error(new StringBuilder()
						.append("URL parameter ")
						.append(aParameter.getKey())
						.append(" expected one value, got ")
						.append(aParameter.getValue().length)
						.append(" values"));
			}
		}
		
		return result;
	}

	protected String retrieveFieldName(String entity, String parameterName) throws IllegalArgumentException, IllegalAccessException {
		
		// FIXME la key non è contenuta in PromoFieldNames, GenericFieldNames o TaxiShareFieldNames
		// non è possibile fare una Class.forName(entity) perchè è
		String className = EntityClassNameTranslator.fromHyphened(entity);
		String key = className.substring(0, 1).toLowerCase() + className.substring(1);
		
		return key + "Id";		
	}

	@Override
	protected FieldsFromRestRequestBuilder setMethod(String anHttpMethod) {
		_httpMethod = anHttpMethod;
		return this;
	}

	@Override
	protected FieldsFromRestRequestBuilder setPathInfo(String aPathInfo) {
		_pathInfo = aPathInfo;
		return this;
	}

	@Override
	protected FieldsFromRestRequestBuilder setBody(String body) {
		_body = body;
		return this;
	}

	@Override
	protected FieldsFromRestRequestBuilder setParameters(Map<String, String[]> parameters) {
		_parameters = parameters;
		return this;
	}

	@Override
	protected FieldsFromRestRequestBuilder setHeaderToken(String headerToken) {
		_headerToken = headerToken;
		return this;
	}

	@Override
	protected FieldsFromRestRequestBuilder setAuthentication(String authentication) {
		_basicAuthentication = authentication;
		return this;
	}

}
