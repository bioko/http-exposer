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

package org.biokoframework.http.services.authentication.basic;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.codec.binary.Base64;
import org.biokoframework.http.services.authentication.HttpAuthenticationStrategy;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.entity.login.Login;
import org.biokoframework.system.exceptions.CommandExceptionsFactory;
import org.biokoframework.system.services.authentication.AuthenticationFailureException;
import org.biokoframework.utils.fields.Fields;
import org.biokoframework.utils.repository.Repository;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 13, 2014
 *
 */
public class BasicAccessAuthenticationStrategy extends HttpAuthenticationStrategy {
	
	private static final String BASIC_AUTH_START = "Basic ";

	private final String fRealm;
	private final Repository<Login> fLoginRepository;
	
	@Inject
	public BasicAccessAuthenticationStrategy(Repository<Login> loginRepository, @Named("basicAuthRealm") String realm) {
		fRealm = realm;
		fLoginRepository = loginRepository;
	}
	
	@Override
	public Fields httpAuthenticate(Fields input) throws AuthenticationFailureException {
		String encodedAuth = getHeader(AUTHORIZATION);
		String decodedAuth = new String(Base64.decodeBase64(encodedAuth.substring(BASIC_AUTH_START.length())));
		
		String userName = decodedAuth.replaceFirst(":.*", "");
		String password = decodedAuth.replaceFirst(".*:", "");
		
		Login login = fLoginRepository.retrieveByForeignKey(GenericFieldNames.USER_EMAIL, userName);
		if (login == null) {
			throw CommandExceptionsFactory.createInvalidLoginException();
		} else if (!login.get(GenericFieldNames.PASSWORD).equals(password)) {
			throw CommandExceptionsFactory.createInvalidLoginException();
		}
		
		return new Fields(Login.class.getSimpleName(), login);
	}

	@Override
	public boolean canAuthenticate(Fields fields) {
		if (!fields.containsKey(HTTP_HEADERS)) {
			return false;
		} else {
			Fields headers = fields.get(HTTP_HEADERS);
			
			return headers.containsKey(AUTHORIZATION);
		}
	}

	@Override
	public Fields getChallenge() {
		return new Fields(
				HTTP_HEADERS, new Fields(
						WWW_AUTHENTICATE, "Basic realm=\"" + fRealm + "\""));
	}

	@Override
	public boolean hasChallenge() {
		return true;
	}

}
