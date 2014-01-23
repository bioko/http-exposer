package it.bioko.http.rest.exception;

import it.bioko.utils.domain.ErrorEntity;

import java.util.ArrayList;
import java.util.List;

public class HttpError {

	private int _httpStatus;
	private List<ErrorEntity> _errorEntities = new ArrayList<ErrorEntity>();

	public HttpError(int httpStatus) {
		_httpStatus = httpStatus;
	}

	public HttpError(int httpStatus, List<ErrorEntity> errorEntities) {
		this(httpStatus);
		_errorEntities = errorEntities;
	}

	public int status() {
		return _httpStatus;
	}

	public List<ErrorEntity> body() {
		return _errorEntities;
	}

	public void setBody(List<ErrorEntity> errors) {				
		_errorEntities = new ArrayList<ErrorEntity>(errors);
	}
	
	public void setBody(ErrorEntity error) {				
		_errorEntities.add(error);
	}

}
