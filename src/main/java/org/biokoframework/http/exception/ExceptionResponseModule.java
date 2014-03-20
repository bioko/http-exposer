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

package org.biokoframework.http.exception;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.biokoframework.http.exception.impl.ExceptionResponseBuilderImpl;
import org.biokoframework.http.routing.UnknownRouteException;
import org.biokoframework.system.KILL_ME.exception.CommandNotFoundException;
import org.biokoframework.system.command.CommandException;
import org.biokoframework.system.entity.EntityNotFoundException;
import org.biokoframework.system.exceptions.BadCommandInvocationException;
import org.biokoframework.system.exceptions.EasterEggException;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.exception.ValidationException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 19, 2014
 *
 */
public class ExceptionResponseModule extends AbstractModule {

	@Override
	protected void configure() {
	
		bind(IExceptionResponseBuilder.class).to(ExceptionResponseBuilderImpl.class);
		
		bind(new TypeLiteral<Map<Class<? extends BiokoException>, Integer>>(){})
			.annotatedWith(Names.named("statusCodeMap"))
			.toInstance(Collections.unmodifiableMap(createMap()));
		
	}

	protected Map<Class<? extends BiokoException>, ? extends Integer> createMap() {
		HashMap<Class<? extends BiokoException>, Integer> map = new HashMap<>();
		
		map.put(BadCommandInvocationException.class, 400);
		map.put(AuthenticationFailureException.class, 401);
		
		map.put(EntityNotFoundException.class, 404);
		map.put(CommandNotFoundException.class, 404);
		map.put(UnknownRouteException.class, 404);

		map.put(ValidationException.class, 400);
		map.put(CommandException.class, 500);

		map.put(EasterEggException.class, 418);
		
		return map;
	}

}
