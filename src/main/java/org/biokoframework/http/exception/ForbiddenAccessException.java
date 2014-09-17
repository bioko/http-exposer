package org.biokoframework.http.exception;

import org.biokoframework.system.command.CommandException;
import org.biokoframework.utils.domain.ErrorEntity;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-09-08
 */
public class ForbiddenAccessException extends CommandException {

    public ForbiddenAccessException(ErrorEntity error) {
        super(error);
    }

}
