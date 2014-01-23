package it.bioko.http;

import it.bioko.system.KILL_ME.XSystem;
import it.bioko.system.KILL_ME.XSystemIdentityCard;
import it.bioko.system.KILL_ME.exception.SystemException;
import it.bioko.system.exceptions.SystemExceptionsFactory;
import it.bioko.system.exceptions.SystemNotFoundException;
import it.bioko.system.factory.AnnotatedSystemFactory;
import it.bioko.system.service.context.ContextFactory;

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
