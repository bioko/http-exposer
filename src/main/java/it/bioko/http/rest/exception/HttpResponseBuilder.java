package it.bioko.http.rest.exception;

import it.bioko.system.KILL_ME.exception.SystemException;

import java.util.HashMap;

public class HttpResponseBuilder {

	private HashMap<Class<? extends SystemException>, HttpError> _exceptionMap;

	public HttpResponseBuilder(HashMap<Class<? extends SystemException>, HttpError> exceptionMap) {
		_exceptionMap = exceptionMap;
	}

	public HttpError buildFrom(SystemException _exception) {
		HttpError httpError = _exceptionMap.get(_exception.getClass());
		httpError.setBody(_exception.getErrors());
		return httpError;
	}

}
