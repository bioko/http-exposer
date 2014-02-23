package org.biokoframework.http.fields;

import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.exception.BiokoException;

public class RequestNotSupportedException extends BiokoException {

	private static final long serialVersionUID = -426397240485504185L;

	public RequestNotSupportedException(ErrorEntity error) {
		super(error);
	}

	public RequestNotSupportedException(Exception exception) {
		super(exception);
	}

}
