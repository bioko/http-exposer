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

package org.biokoframework.http.handler.annotation;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.biokoframework.http.handler.IHandler;
import org.biokoframework.http.mock.DummyEntity;
import org.biokoframework.http.mock.MockCommand;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.impl.RouteImpl;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.annotation.Command;
import org.biokoframework.system.command.crud.CreateEntityCommand;
import org.biokoframework.system.command.crud.DeleteEntityCommand;
import org.biokoframework.system.command.crud.RetrieveEntityCommand;
import org.biokoframework.system.command.crud.UpdateEntityCommand;
import org.biokoframework.system.command.crud.annotation.CrudCommand;
import org.biokoframework.system.repository.memory.InMemoryRepository;
import org.biokoframework.system.services.RepositoryModule;
import org.biokoframework.utils.repository.RepositoryException;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 16, 2014
 *
 */
public class AnnotationHandlerLocatorTest {

	private Injector fInjector;

	@Test
	public void simpleTest() throws Exception {
		AnnotationHandlerLocator fLocator = new AnnotationHandlerLocator(Simple.class);
		
		// Test first command
		IRoute getRoute = new RouteImpl(HttpMethod.GET, Simple.SIMPLE_GET_COMMAND, null);
		IHandler handler = fLocator.getHandler(getRoute);
		assertThat(handler, is(notNullValue()));
		assertThat(handler.getCommand(fInjector), is(instanceOf(MockCommand.class)));
		
		// Test second command
		IRoute postRoute = new RouteImpl(HttpMethod.POST, Simple.SIMPLE_POST_COMMAND, null);
		handler = fLocator.getHandler(postRoute);
		assertThat(handler, is(notNullValue()));
		assertThat(handler.getCommand(fInjector), is(instanceOf(MockCommand.class)));
		
	}
	
	@Test
	public void crudTest() throws Exception {
		AnnotationHandlerLocator fLocator = new AnnotationHandlerLocator(Crud.class);
		
		// POST Test
		IRoute postRoute = new RouteImpl(HttpMethod.POST, Crud.DUMMY_CRUD, null);
		IHandler handler = fLocator.getHandler(postRoute);
		assertThat(handler, is(notNullValue()));
		assertThat(handler.getCommand(fInjector), is(instanceOf(CreateEntityCommand.class)));
				
		// GET Test
		IRoute getRoute = new RouteImpl(HttpMethod.GET, Crud.DUMMY_CRUD, null);
		handler = fLocator.getHandler(getRoute);
		assertThat(handler, is(notNullValue()));
		assertThat(handler.getCommand(fInjector), is(instanceOf(RetrieveEntityCommand.class)));
		
		// PUT Test
		IRoute putRoute = new RouteImpl(HttpMethod.PUT, Crud.DUMMY_CRUD, null);
		handler = fLocator.getHandler(putRoute);
		assertThat(handler, is(notNullValue()));
		assertThat(handler.getCommand(fInjector), is(instanceOf(UpdateEntityCommand.class)));
		
		// DELETE Test
		IRoute deleteRoute = new RouteImpl(HttpMethod.DELETE, Crud.DUMMY_CRUD, null);
		handler = fLocator.getHandler(deleteRoute);
		assertThat(handler, is(notNullValue()));
		assertThat(handler.getCommand(fInjector), is(instanceOf(DeleteEntityCommand.class)));
				
	}
	
	@Before
	public void createInjector() {
		fInjector = Guice.createInjector(
				new RepositoryModule() {
					@Override
					protected void configureRepositories() throws RepositoryException {
						bindEntity(DummyEntity.class).toInstance(new InMemoryRepository<DummyEntity>(DummyEntity.class));
					}
				});
	}
	
	private static class Simple {
		@Command(impl = MockCommand.class, rest = HttpMethod.GET)
		public static final String SIMPLE_GET_COMMAND = "simple-get-command";
		
		@Command(impl = MockCommand.class, rest = HttpMethod.POST)
		public static final String SIMPLE_POST_COMMAND = "simple-post-command";
	}
	
	private static class Crud {
		@CrudCommand(entity = DummyEntity.class, repoName = "dummyCrud")
		public static final String DUMMY_CRUD = "dummy-crud";
	}

}
