package it.bioko.http.rest;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.exceptions.SystemNotFoundException;
import it.bioko.utils.fields.Fields;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

public class RestResponseFromSystemExceptionBuilder {

	private Fields _output;
	
	private SystemNotFoundException _exception;

	@SuppressWarnings("unchecked")
	public HttpServletResponse build(HttpServletResponse response) {
		response.setStatus(404);
		JSONArray jsonResponse = new JSONArray();
		jsonResponse.add(_exception.error());
		
		_output.put(GenericFieldNames.RESPONSE, jsonResponse);
		_output.put(GenericFieldNames.RESPONSE_CONTENT_TYPE, GenericFieldValues.JSON_CONTENT_TYPE);
		return response;
	}

	public RestResponseFromSystemExceptionBuilder setOutputFields(Fields output) {
		_output = output;
		return this;
	}

	public RestResponseFromSystemExceptionBuilder setException(SystemNotFoundException exception) {
		_exception = exception;
		return this;
	}

}
