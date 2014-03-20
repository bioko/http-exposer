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

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.biokoframework.http.handler.IHandler;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.fields.Fields;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 16, 2014
 *
 */
public class GenericHandler implements IHandler {

	private final Class<? extends DomainEntity> fEntity;
	private final Class<? extends ICommand> fCommand;
	private final Injector fInjector;

	public GenericHandler(Class<? extends DomainEntity> entity, Class<? extends ICommand> command, Injector injector) {
		fCommand = command;
		fEntity = entity;
		fInjector = injector.createChildInjector(new EntityModule());
	}
	
	private ICommand getCommand() {
		return fInjector.getInstance(fCommand);
	}
	
	public Fields executeCommand(Fields input) throws SystemException, ValidationException {
		return getCommand().execute(input);
	}

//	@Override
//	public List<IValidator> getValidators() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	private class EntityModule extends AbstractModule {

		@Override
		protected void configure() {
			bindConstant().annotatedWith(Names.named("entity")).to(fEntity);
		}

	}

}
