package it.bioko.http.rest.exception;

import it.bioko.system.KILL_ME.exception.CommandNotFoundException;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.command.CommandException;
import it.bioko.system.command.ValidationException;
import it.bioko.system.entity.EntityNotFoundException;
import it.bioko.system.exceptions.BadCommandInvocationException;
import it.bioko.system.exceptions.EasterEggException;
import it.bioko.system.service.authentication.AuthenticationFailureException;

import java.util.HashMap;


public class HttpResponseExceptionFactory {

	public static HashMap<Class<? extends SystemException>, HttpError> create() {
		HashMap<Class<? extends SystemException>, HttpError> exceptionMap = new HashMap<Class<? extends SystemException>, HttpError>();
		exceptionMap.put(BadCommandInvocationException.class, new HttpError(400));
		exceptionMap.put(AuthenticationFailureException.class, new HttpError(401));
		exceptionMap.put(EntityNotFoundException.class, new HttpError(404));
		exceptionMap.put(CommandNotFoundException.class, new HttpError(404));
		
		exceptionMap.put(ValidationException.class, new HttpError(400));
		exceptionMap.put(CommandException.class, new HttpError(500));
		
		exceptionMap.put(EasterEggException.class, new HttpError(418));
		return exceptionMap;
	}

}
