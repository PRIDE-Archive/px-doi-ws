package uk.ac.ebi.pride.archive.doi.error.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.pride.web.util.exception.RestError;
import uk.ac.ebi.pride.web.util.exception.RestErrorRegistry;

/**
 * @author Rui Wang
 * @version $Id$
 */
@ControllerAdvice
public class AccessDeniedAdvice {
    private static final Logger logger = LoggerFactory.getLogger(AccessDeniedAdvice.class);

    @ExceptionHandler(AccessDeniedException.class)
    public @ResponseBody RestError handleAccessDeniedException(AccessDeniedException ex) {
        logger.error(ex.getMessage(), ex);

        RestError error = RestErrorRegistry.getRestErrorByClass(AccessDeniedException.class);
        error.setDeveloperMessage(ex.getMessage());

        return error;
    }
}
