package org.biokoframework.http.routing;

import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.utils.domain.ErrorEntity;

public class UnknownRouteException extends SystemException {

	private static final long serialVersionUID = -4481436151175783637L;

	public UnknownRouteException(ErrorEntity errorEntity) {
		super(errorEntity);
	}
	
}
