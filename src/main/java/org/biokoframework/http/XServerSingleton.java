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

package org.biokoframework.http;

import org.biokoframework.system.KILL_ME.XSystem;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.system.exceptions.SystemExceptionsFactory;
import org.biokoframework.system.exceptions.SystemNotFoundException;
import org.biokoframework.system.factory.AnnotatedSystemFactory;
import org.biokoframework.system.service.context.ContextFactory;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class XServerSingleton {

	private final Class<?>_annotatedSystemCommands;
	private final ContextFactory _systemContextFactory;
	
	private Hashtable<XSystemIdentityCard, XSystem> _systems;
	
	@Inject
	public XServerSingleton(@Named("Commands") Class<?> commandsClass, ContextFactory contextFactory) {
		_systems = new Hashtable<XSystemIdentityCard, XSystem>();
		
		_annotatedSystemCommands = commandsClass;
		_systemContextFactory = contextFactory;
	}

	public XSystem getSystem(XSystemIdentityCard xSystemIdentityCard, Logger logger) throws SystemNotFoundException {
		logger.info("Getting system: " + xSystemIdentityCard.report());
		XSystem system = _systems.get(xSystemIdentityCard);
		if (system == null) {
			try {
				system = createSystem(xSystemIdentityCard);
			} catch (SystemException exception) {
				throw SystemExceptionsFactory.createSystemNotFound(xSystemIdentityCard);
			}
			_systems.put(xSystemIdentityCard, system);
			return system;
		}
		return _systems.get(xSystemIdentityCard);
	}
	
	// TODO i metodi qui sotto sono quadruplicati, credo basterebbe la IdentityCard
	// per tirare fuori il sistema corretto.
	// da provare quando tutti i test sono a posto
	
	private XSystem createSystem(XSystemIdentityCard identityCard) throws SystemNotFoundException, SystemException {
		try {
			return AnnotatedSystemFactory.createSystem(identityCard, _systemContextFactory, _annotatedSystemCommands);
		} catch (Exception exception) {
			throw new SystemException(exception);
		}
	}
	
	public void shutdown() {
		for (XSystem aSystem : _systems.values()) {
			aSystem.shutdown();
		}
	}

}
