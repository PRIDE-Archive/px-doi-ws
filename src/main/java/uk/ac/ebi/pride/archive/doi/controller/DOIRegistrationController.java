package uk.ac.ebi.pride.archive.doi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.doi.error.doi.DOIRegistrationException;
import uk.ac.ebi.pride.archive.doi.model.DOIRegistration;
import uk.ac.ebi.pride.archive.doi.model.RegistrationStatus;
import uk.ac.ebi.pride.doi.RegisterDOI;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Rui Wang
 */
@Controller
@RequestMapping("/doi")
public class DOIRegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(DOIRegistrationController.class);

    @Autowired
    private RegisterDOI doiRegister;

    @Value("#{doiProperties['doi.prefix']}")
    private String doiPrefix;

    @Value("#{doiProperties['doi.px.accession.url.prefix']}")
    private String pxUrlPrefix;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    @RequestMapping(value = "/registration/{projectAccession}/mode/{registrationMode}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DOIRegistration registerDOI(@PathVariable String projectAccession, @PathVariable String registrationMode, Principal user) {

        // check and format project accession
        String formattedProjectAccession = projectAccession.toUpperCase();
        String doi = doiPrefix + '/' + formattedProjectAccession;
        String mappedUrl = pxUrlPrefix + '/' + formattedProjectAccession;

        // check registration mode
        boolean testMode = !registrationMode.equalsIgnoreCase("production");

        // register doi
        registerDoi(doi, mappedUrl, testMode);

        // return registered doi
        logger.info("DOI {} registered for user {}", doi, user.getName());
        Date time = Calendar.getInstance().getTime();
        return new DOIRegistration(doi, mappedUrl, RegistrationStatus.SUCCESS, "Registration successful", sdf.format(time));
    }

    private void registerDoi(String doi, String mappedUrl, boolean testMode) {
        boolean registration;
        logger.info("Going to register " + doi);

        try {
            //when not in testing mode, use the controller to register DOI to Crossref
            if (!testMode) {
                registration = doiRegister.registerDOI(doi, mappedUrl);
                if (!registration) {
                    handlingError(doi, mappedUrl);
                }
            }
        } catch (Exception e) {
            handlingError(doi, mappedUrl);
        }
    }

    private void handlingError(String doi, String mappedUrl) {
        String msg = "Error while registering doi: " + doi;
        throw new DOIRegistrationException(msg, doi, mappedUrl);
    }

}
