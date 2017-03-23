package uk.ac.ebi.pride.px.doi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.ebi.pride.px.doi.model.ErrorJson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * This class specifies a more detailed error message result, which also maps the default /error page.
 */
@RestController
public class ErrorJsonController implements ErrorController {
  private static final Logger log = LoggerFactory.getLogger(ErrorJsonController.class);

  private static final String PATH = "/error";

  @Autowired
  private ErrorAttributes errorAttributes;

  /**
   * This method is the default error message, mapped to /error.
   * @param request the http request
   * @param response the http response
   * @return ab ErrorJson with information about the error in more detailed, which will be formatted as a JSON result.
   */
  @RequestMapping(value = PATH)
  public ErrorJson error(HttpServletRequest request, HttpServletResponse response) {
    logError(request);
    return new ErrorJson(response.getStatus(), getErrorAttributes(request, false));
  }

  /**
   * This method logs any errors with the web service.
   * @param request the http request
   */
  private void logError(HttpServletRequest request) {
    log.error("Error with web service:");
    getErrorAttributes(request, true).forEach((s, o) -> log.error("\t" + s + ": " + o));
    log.error("end of error.");
  }

  /**
   * This method gets the error path.
   * @return the error path
   */
  @Override
  public String getErrorPath() {
    return PATH;
  }

  /**
   * This method gets the error attributes from the requeste and converts it to a Map.
   * @param request the http request
   * @param includeStack true to include the stack trace, false otherwise
   * @return a map of key/value pairs about the error that was encountered
   */
  private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStack) {
    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    return errorAttributes.getErrorAttributes(requestAttributes, includeStack);
  }

}
