package uk.ac.ebi.pride.px.doi.model;

import java.util.Map;

/**
 * This class used to store/retrieve information about a web service error.
 *
 * @author Tobias-Ternent
 */
public class ErrorJson {

  Integer status;
  String error;
  String message;
  String path;
  String timeStamp;
  String trace;

  /**
   * Constructor to map error attributes to class variables.
   * @param status the error status
   * @param errorAttributes the error attributes
   */
  public ErrorJson(int status, Map<String, Object> errorAttributes) {
    this.status = status;
    this.error = (String) errorAttributes.get("error");
    this.message = (String) errorAttributes.get("message");
    this.path = (String) errorAttributes.get("path");
    this.timeStamp = errorAttributes.get("timestamp").toString();
    this.trace = (String) errorAttributes.get("trace");
  }

  /**
   * Gets the status.
   * @return the status
   */
  public Integer getStatus() {
    return status;
  }

  /**
   * Sets the status.
   * @param status the status
   */
 public void setStatus(Integer status) {
    this.status = status;
  }

  /**#
   * Gets the error.
   * @return the error
   */
 public String getError() {
    return error;
  }

  /**
   * Sets the error
   * @param error the error
   */
 public void setError(String error) {
    this.error = error;
  }

  /**
   * Gets the message.
   * @return the message
   */
 public String getMessage() {
    return message;
  }

  /**
   * Sets the message.
   * @param message the message
   */
 public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the path.
   * @return the path
   */
 public String getPath() {
    return path;
  }

  /**
   * Sets the path.
   * @param path the path
   */
 public void setPath(String path) {
    this.path = path;
  }

  /**
   * Gets the timestamp.
   * @return the timestamp
   */
 public String getTimeStamp() {
    return timeStamp;
  }

  /**
   * Sets the timestamp.
   * @param timeStamp the timestamp
   */
 public void setTimeStamp(String timeStamp) {
    this.timeStamp = timeStamp;
  }

  /**
   * Gets the stacktrace.
   * @return the stacktrace
   */
 public String getTrace() {
    return trace;
  }

  /**
   * Sets the stacktrace.
   * @param trace the stacktrace
   */
 public void setTrace(String trace) {
    this.trace = trace;
  }
}
