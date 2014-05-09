package uk.ac.ebi.pride.archive.doi.error.doi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ebi.pride.archive.doi.model.DOIRegistration;
import uk.ac.ebi.pride.archive.doi.model.RegistrationStatus;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Rui Wang
 * @version $Id$
 */
@ControllerAdvice
public class DOIRegistrationAdvice {
    private static final Logger logger = LoggerFactory.getLogger(DOIRegistrationAdvice.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    @ExceptionHandler(DOIRegistrationException.class)
    public @ResponseBody
    DOIRegistration handleAccessDeniedException(DOIRegistrationException ex, Principal principal) {
        logger.error("Failed to register doi {} for user {}", ex.getDoi(), principal.getName());

        Date time = Calendar.getInstance().getTime();
        return new DOIRegistration(ex.getDoi(), ex.getMappedUrl(), RegistrationStatus.ERROR, ex.getMessage(), sdf.format(time));
    }
}
