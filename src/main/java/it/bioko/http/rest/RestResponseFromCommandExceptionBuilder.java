package it.bioko.http.rest;

import it.bioko.http.rest.exception.HttpError;
import it.bioko.http.rest.exception.HttpResponseBuilder;
import it.bioko.http.rest.exception.HttpResponseExceptionFactory;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.exceptions.ErrorMessagesFactory;
import it.bioko.utils.domain.ErrorEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class RestResponseFromCommandExceptionBuilder {
	
	private Fields _outputFields;
	private SystemException _exception;

	public HttpServletResponse build(HttpServletResponse response) {
		
		HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder(HttpResponseExceptionFactory.create());
		HttpError httpError = httpResponseBuilder.buildFrom(_exception);
		
		List<ErrorEntity> body = httpError.body();
		if (body != null && !body.isEmpty()) {
			response.setStatus(httpError.status());
		} else {
			httpError = internalServerErrorCapturedException();
			response.setStatus(httpError.status());
		}
		_outputFields.put(GenericFieldNames.RESPONSE, httpError.body());
		_outputFields.put(GenericFieldNames.RESPONSE_CONTENT_TYPE, GenericFieldValues.JSON_CONTENT_TYPE);
		return response;
	}

	private HttpError internalServerErrorCapturedException() {
		// 500 - Internal Server Error
		List<ErrorEntity> errorWithEceptionMessage = _exception.getErrors();
		if (_exception.exception() != null) {
			String[] messageChuncks = ErrorMessagesFactory.createMap().get(FieldNames.CONTAINER_EXCEPTION_CODE);
			String message = new StringBuilder().
					append(messageChuncks[0]).
					append(_exception.exception().getClass().getSimpleName()).
					append(messageChuncks[1]).
					append(_exception.exception().getMessage()).
					append(" [").
					append(_exception.exception().getStackTrace()[0].toString()).
					append(" ...]").
					toString();
			
			Fields fields = Fields.single(ErrorEntity.ERROR_MESSAGE, message);
			fields.put(ErrorEntity.ERROR_CODE, FieldNames.CONTAINER_EXCEPTION_CODE);
			errorWithEceptionMessage.add(new ErrorEntity(fields));
		}
		return new HttpError(500, errorWithEceptionMessage);
	}		
	
	public RestResponseFromCommandExceptionBuilder setInputFields(Fields inputFields) {
		return this;
	}

	public RestResponseFromCommandExceptionBuilder setOutputFields(Fields outputFields) {
		_outputFields = outputFields;
		return this;
	}
	
	public RestResponseFromCommandExceptionBuilder setException(SystemException exception) {
		_exception = exception;
		return this;
	}
	
}
