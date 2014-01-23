package it.bioko.http;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.utils.fields.Fields;

import javax.servlet.http.HttpServletRequest;

public abstract class FieldsFromRequestBuilder {

	public abstract Fields build();
	
	public FieldsFromRequestBuilder setRequest(HttpServletRequest request) {
		setMethod(request.getMethod());
		setPathInfo(request.getPathInfo());
		setHeaderToken(request.getHeader(GenericFieldNames.TOKEN_HEADER));
		setAuthentication(request.getHeader(GenericFieldNames.BASIC_AUTHENTICATION_HEADER));
		return this;
	}
	
	public abstract boolean canHandle(HttpServletRequest request);
	
	protected abstract FieldsFromRequestBuilder setPathInfo(String pathInfo);
	protected abstract FieldsFromRequestBuilder setMethod(String method);
	
	protected abstract FieldsFromRequestBuilder setHeaderToken(String headerToken);
	protected abstract FieldsFromRequestBuilder setAuthentication(String authentication);
	
	protected String getRequestCharset(HttpServletRequest request) {
		return request.getCharacterEncoding() == null ? "utf-8" : request.getCharacterEncoding();
	}
}
