package uk.ac.ebi.pride.px.doi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.px.doi.model.DoiMetaData;
import uk.ac.ebi.pride.px.doi.model.DoiRegistration;
import uk.ac.ebi.pride.px.doi.model.DoiRegistrationStatus;
import uk.ac.ebi.pride.px.doi.upload.DoiRegister;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class handles the mapping for the DOI registration process, under the /registration... path.
 *
 * @author Tobias-Ternent
 */
@Controller
public class DoiRegistrationController {
  private static final Logger log = LoggerFactory.getLogger(DoiRegistrationController.class);
  private static final String PRODUCTION_MODE = "production";

  @Value("${archive.doi.prefix.property}")
  private String doiPrefix;

  @Value("${archive.doi.px.accession.url.prefix.property}")
  private String pxUrlPrefix;

  private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

  /**
   * This starts the process to register a DOI for the provided accession number, in the production or test environment.
   * @param projectAccession the project accession to register.
   * @param registrationMode the environment, "production" for production, or anything else for test.
   * @param user the user who the DOI was reistered for
   * @return a DoiRegistration with the registration result, success or error.
   */
  @RequestMapping(value = "/registration/{projectAccession}/mode/{registrationMode}",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody
  DoiRegistration registerDOI(@PathVariable String projectAccession, @PathVariable String registrationMode, Principal user) {
    String formattedProjectAccession = projectAccession.toUpperCase();
    String doi = doiPrefix + '/' + formattedProjectAccession;
    String mappedUrl = pxUrlPrefix + '/' + formattedProjectAccession;
    boolean productionMode = PRODUCTION_MODE.equalsIgnoreCase(registrationMode);
    boolean registered = registerDoi(doi, mappedUrl, productionMode);
    if (registered) {
      log.info("DOI " + doi + " registered for user " + user.getName());
    } else {
      log.error("ERROR: DOI " + doi + " NOT registered for user " + user.getName());
    }
    Date time = Calendar.getInstance().getTime();
    DoiRegistration result;
    if (registered){
      result = new DoiRegistration(doi, mappedUrl, DoiRegistrationStatus.SUCCESS, "Registration successful", sdf.format(time));
    } else {
      result = new DoiRegistration(doi, mappedUrl, DoiRegistrationStatus.ERROR, "Registration FAILED", sdf.format(time));
    }
    return result;
  }

  /**
   * This method registered a DOI to a target URL.
   * @param doi the DOI to register
   * @param mappedUrl the URL for the DOI to point to.
   * @param productionMode true for production, false otherwise for test.
   * @return true if the registration was successful, false otherwise.
   */
  private boolean registerDoi(String doi, String mappedUrl, boolean productionMode) {
    boolean registered = false;
    log.info("Going to register " + doi);
    try {
      if (productionMode) {
        registered = new DoiRegister(new DoiMetaData()).registerDOI(doi, mappedUrl);
        if (registered) {
          log.info("Successfully registered DOI " + doi + " " + mappedUrl);
        } else {
          logException(doi, mappedUrl, null);
        }
      } else {
        log.info("Test mode, pretending to have succeeded registering DOI.");
        registered = true;
      }
    } catch (Exception e) {
      logException(doi, mappedUrl, e);
    }
    return registered;
  }

  /**
   * This method logs the problem that occured during DOI registration.
   * @param doi the DOI that failed registration.
   * @param mappedUrl the URL the DOI should have been mapped to.
   * @param e the exception, if one was thrown. May be null.
   */
  private void logException(String doi, String mappedUrl, Exception e) {
    log.error("Problem trying to register DOI " + doi + " " + mappedUrl);
    if (e!=null) {
      log.error("Exception while registering DOI", e);
    }
  }
}
