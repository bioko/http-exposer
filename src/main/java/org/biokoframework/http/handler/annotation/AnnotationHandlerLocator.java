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

import com.google.inject.Injector;
import org.biokoframework.http.handler.IHandler;
import org.biokoframework.http.handler.impl.AbstractHandlerLocator;
import org.biokoframework.http.handler.impl.GenericHandler;
import org.biokoframework.http.handler.impl.HandlerImpl;
import org.biokoframework.http.handler.impl.SecurityHandler;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.http.routing.impl.RouteMatcherImpl;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.system.command.ICommand;
import org.biokoframework.system.command.annotation.Command;
import org.biokoframework.system.command.crud.CreateEntityCommand;
import org.biokoframework.system.command.crud.DeleteEntityCommand;
import org.biokoframework.system.command.crud.RetrieveEntityCommand;
import org.biokoframework.system.command.crud.UpdateEntityCommand;
import org.biokoframework.system.command.crud.annotation.CrudCommand;
import org.biokoframework.system.command.crud.binary.GetBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.PostBinaryEntityCommand;
import org.biokoframework.system.command.crud.binary.annotation.BlobCrudCommand;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.system.services.authentication.all.AllAuthenticationService;
import org.biokoframework.system.services.authentication.annotation.Auth;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 16, 2014
 *
 */
public class AnnotationHandlerLocator extends AbstractHandlerLocator {

	private final Class<?> fCommandsClass;
	private final Injector fInjector;
	private final AllAuthenticationService fAuthService;

	@Inject
	public AnnotationHandlerLocator(@SuppressWarnings("rawtypes") @Named("Commands") Class commandsClass, Injector injector) {
		fCommandsClass = commandsClass;
		fAuthService = injector.getInstance(AllAuthenticationService.class);
		fInjector = injector;
		
		for (Field aCandidateCommand : fCommandsClass.getFields()) {
			if (aCandidateCommand.getAnnotation(Command.class) != null) {
				registerCommand(aCandidateCommand);
			} else if (aCandidateCommand.getAnnotation(CrudCommand.class) != null) {
				registerCrudCommand(aCandidateCommand);
			} else if (aCandidateCommand.getAnnotation(BlobCrudCommand.class) != null) {
				registerBlobCrudCommand(aCandidateCommand);
			}
		}
	}

	protected void registerCommand(Field aCandidateCommand) {
		try {
			Command annotation = aCandidateCommand.getAnnotation(Command.class);
			String basePath = (String) aCandidateCommand.get(null);
			if (!basePath.startsWith("/")) {
				basePath = "/" + basePath;
			}
			IRouteMatcher matcher = createRouteMatcher(annotation.rest(), basePath);
			// TODO this should be injected as an enhancement to an handler
			IHandler handler = createBasicHandler(annotation.impl());
			
			if (aCandidateCommand.isAnnotationPresent(Auth.class)) {
				Auth auth = aCandidateCommand.getAnnotation(Auth.class);
				handler = new SecurityHandler(handler, Arrays.asList(auth.roles()), fAuthService, true);
			} else {
				handler = new SecurityHandler(handler, Collections.<String>emptyList(), fAuthService, false);				
			}
			
			addRoute(matcher, handler);
		} catch (IllegalAccessException exception) {
			// TODO handle exception
			exception.printStackTrace();
		}
	}

	private IRouteMatcher createRouteMatcher(HttpMethod method, String regexp) {
		return new RouteMatcherImpl(method, regexp);
	}

	private IHandler createBasicHandler(Class<? extends ICommand> command) {
		return new HandlerImpl(command, fInjector);
	}

	protected void registerCrudCommand(Field aCandidateCommand) {
		try {
			CrudCommand annotation = aCandidateCommand.getAnnotation(CrudCommand.class);
			String basePath = (String) aCandidateCommand.get(null);
			if (!basePath.startsWith("/")) {
				basePath = "/" + basePath;
			}
			if (!basePath.contains("/{<\\d*>id}")) {
				basePath = basePath + "/{<\\d*>id}";
			}
			
			boolean mandatory = false;
			List<String> roles = Collections.emptyList();
			if (aCandidateCommand.isAnnotationPresent(Auth.class)) {
				mandatory = true;
				roles = Arrays.asList(aCandidateCommand.getAnnotation(Auth.class).roles());
			}
			
			if (annotation.create()) {
				IRouteMatcher matcher = createRouteMatcher(HttpMethod.POST, basePath);
				IHandler handler = new GenericHandler(annotation.entity(), CreateEntityCommand.class, fInjector);
				
				handler = new SecurityHandler(handler, roles, fAuthService, mandatory);
				
				addRoute(matcher, handler);
			}
			if (annotation.read()) {
				IRouteMatcher matcher = createRouteMatcher(HttpMethod.GET, basePath);
				IHandler handler = new GenericHandler(annotation.entity(), RetrieveEntityCommand.class, fInjector);
			
				handler = new SecurityHandler(handler, roles, fAuthService, mandatory);
				
				addRoute(matcher, handler);
			}
			if (annotation.update()) {
				IRouteMatcher matcher = createRouteMatcher(HttpMethod.PUT, basePath);
				IHandler handler = new GenericHandler(annotation.entity(), UpdateEntityCommand.class, fInjector);
				
				handler = new SecurityHandler(handler, roles, fAuthService, mandatory);
				
				addRoute(matcher, handler);
			}
			if (annotation.delete()) {
				IRouteMatcher matcher = createRouteMatcher(HttpMethod.DELETE, basePath);
				IHandler handler = new GenericHandler(annotation.entity(), DeleteEntityCommand.class, fInjector);

				handler = new SecurityHandler(handler, roles, fAuthService, mandatory);
				
				addRoute(matcher, handler);
			}
		} catch (IllegalAccessException exception) {
			// TODO handle exception
			exception.printStackTrace();
		}
	}

	protected void registerBlobCrudCommand(Field aCandidateCommand) {
        try {
            BlobCrudCommand annotation = aCandidateCommand.getAnnotation(BlobCrudCommand.class);
            String basePath = (String) aCandidateCommand.get(null);
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
                if (!basePath.startsWith("/")) {
                    basePath = "/" + basePath;
                }
                if (!basePath.contains("/{<\\d*>id}")) {
                    basePath = basePath + "/{<\\d*>id}";
                }

                boolean mandatory = false;
                List<String> roles = Collections.emptyList();
                if (aCandidateCommand.isAnnotationPresent(Auth.class)) {
                    mandatory = true;
                    roles = Arrays.asList(aCandidateCommand.getAnnotation(Auth.class).roles());
                }

                if (annotation.create()) {
                    IRouteMatcher matcher = createRouteMatcher(HttpMethod.POST, basePath);
                    IHandler handler = new GenericHandler(BinaryEntity.class, PostBinaryEntityCommand.class, fInjector);

                    handler = new SecurityHandler(handler, roles, fAuthService, mandatory);

                    addRoute(matcher, handler);
                }
                if (annotation.read()) {
                    IRouteMatcher matcher = createRouteMatcher(HttpMethod.GET, basePath);
                    IHandler handler = new GenericHandler(BinaryEntity.class, GetBinaryEntityCommand.class, fInjector);

                    handler = new SecurityHandler(handler, roles, fAuthService, mandatory);

                    addRoute(matcher, handler);
                }
            }
        } catch (IllegalAccessException exception) {
            // TODO handle exception
                exception.printStackTrace();
        }
	}
	
	

}
