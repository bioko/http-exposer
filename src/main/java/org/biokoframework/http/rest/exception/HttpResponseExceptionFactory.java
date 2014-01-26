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

package org.biokoframework.http.rest.exception;

import java.util.HashMap;

import org.biokoframework.system.KILL_ME.exception.CommandNotFoundException;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.EntityNotFoundException;
import org.biokoframework.system.exceptions.BadCommandInvocationException;
import org.biokoframework.system.exceptions.EasterEggException;
import org.biokoframework.system.service.authentication.AuthenticationFailureException;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.exception.ValidationException;


public class HttpResponseExceptionFactory {

	public static HashMap<Class<? extends BiokoException>, HttpError> create() {
		HashMap<Class<? extends BiokoException>, HttpError> exceptionMap = new HashMap<Class<? extends BiokoException>, HttpError>();
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
