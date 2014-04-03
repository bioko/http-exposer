/*
 * Copyright (c) 2014.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.http.injection;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.repository.population.IRepositoryPopulator;
import org.biokoframework.system.repository.service.IRepositoryService;
import org.biokoframework.system.services.entity.IEntityBuilderService;
import org.biokoframework.utils.exception.ValidationException;
import org.biokoframework.utils.repository.RepositoryException;

import java.util.Set;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014 Apr 02
 */
public abstract class SystemServletConfig extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        Injector injector = createInjector();

        if (ConfigurationEnum.DEV.equals(injector.getInstance(ConfigurationEnum.class))) {
            try {
                populateRepository(injector.getInstance(Key.get(new TypeLiteral<Set<IRepositoryPopulator>>() {})),
                        injector.getInstance(IRepositoryService.class), injector.getInstance(IEntityBuilderService.class));
            } catch (RepositoryException|ValidationException exception) {
                throw new RuntimeException(exception);
            }
        }

        return injector;
    }

    protected void populateRepository(Set<IRepositoryPopulator> populators, IRepositoryService repositoryService,
                                      IEntityBuilderService entityBuilderService) throws RepositoryException, ValidationException {

        for (IRepositoryPopulator aPopulator : populators) {
            aPopulator.populate(repositoryService, entityBuilderService);
        }
    }

    protected abstract Injector createInjector();

}
