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


package org.biokoframework.http.handler.impl;

import java.util.List;

import org.biokoframework.http.handler.IHandler;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.system.services.authentication.IAuthenticationService;
import org.biokoframework.system.services.authentication.all.AllAuthenticationService;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Mar 7, 2014
 *
 */
public class SecurityHandler implements IHandler {
	
	private final IHandler fWrappedHandler;
	private final List<String> fRequiredRoles;
	private final IAuthenticationService fAuthService;
	private final boolean fRequireValidAuthentication;

	public SecurityHandler(IHandler wrappedHandler, List<String> requiredRoles, AllAuthenticationService authService, boolean requireValidAuthentication) {
		fWrappedHandler = wrappedHandler;
		fRequiredRoles = requiredRoles;
		fAuthService = authService;
		fRequireValidAuthentication = requireValidAuthentication;
	}

	@Override
	public Fields executeCommand(Fields input) throws SystemException, ValidationException {
		try {
			Fields authFields = fAuthService.authenticate(input, fRequiredRoles);
			input.putAll(authFields);
		} catch (AuthenticationFailureException exception) {
			if (fRequireValidAuthentication) {
				throw exception;
			}
		}
		return fWrappedHandler.executeCommand(input);
	}

}
