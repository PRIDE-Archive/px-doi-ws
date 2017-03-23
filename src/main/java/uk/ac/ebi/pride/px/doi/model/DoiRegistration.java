package uk.ac.ebi.pride.px.doi.model;


import java.io.Serializable;

/**
 * This class is used to hold information about a DOI registration.
 *
 * @author Tobias-Ternent
 */
public class DoiRegistration implements Serializable {
  private String doi;
  private String mappedUrl;
  private DoiRegistrationStatus status;
  private String message;
  private String date;

  /**
   * Default constructor.
   */
  public DoiRegistration() {
  }

  /**
   * Creates a DoiRegistration object with provided information.
   * @param doi the DOI
   * @param mappedUrl the URL for mapping
   * @param status the DOI registration status
   * @param message the message
   * @param date the date
   */
  public DoiRegistration(String doi,
                         String mappedUrl,
                         DoiRegistrationStatus status,
                         String message,
                         String date) {
    this.doi = doi;
    this.mappedUrl = mappedUrl;
    this.status = status;
    this.message = message;
    this.date = date;
  }

  /**
   * Gets the DOI.
   * @return the DOI
   */
  public String getDoi() {
    return doi;
  }

  /**
   * Sets the DOI.
   * @param doi the DOI
   */
  public void setDoi(String doi) {
    this.doi = doi;
  }

  /**
   * Gets the mapped URL.
   * @return the mapped URL
   */
  public String getMappedUrl() {
    return mappedUrl;
  }

  /**
   * Sets the mapped URL.
   * @param mappedUrl the mapped URL
   */
  public void setMappedUrl(String mappedUrl) {
    this.mappedUrl = mappedUrl;
  }

  /**
   * Gets the status.
   * @return the status
   */
  public DoiRegistrationStatus getStatus() {
    return status;
  }

  /**
   * Sets the status.
   * @param status the status
   */
  public void setStatus(DoiRegistrationStatus status) {
    this.status = status;
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
   * Gets the date.
   * @return the date
   */
  public String getDate() {
    return date;
  }

  /**
   * Sets the date.
   * @param date the data
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * Compares if two DoiRegistration objects contain the same iformation or not.
   * @param o the other DoiRegistration object to compare to.
   * @return true for the same information, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DoiRegistration)) return false;
    DoiRegistration that = (DoiRegistration) o;
    if (!date.equals(that.date)) return false;
    if (!doi.equals(that.doi)) return false;
    if (!mappedUrl.equals(that.mappedUrl)) return false;
    if (!message.equals(that.message)) return false;
    if (status != that.status) return false;
    return true;
  }

  /**
   * Generates a hash based off the information present in the object.
   * @return the hashcode integer of the object
   */
  @Override
  public int hashCode() {
    int result = doi.hashCode();
    result = 31 * result + mappedUrl.hashCode();
    result = 31 * result + status.hashCode();
    result = 31 * result + message.hashCode();
    result = 31 * result + date.hashCode();
    return result;
  }

  /**
   * Converts all the information held in the object to a String.
   * @return a String of all the information held in the object.
   */
  @Override
  public String toString() {
    return "DOIRegistration{" +
        "doi='" + doi + '\'' +
        ", mappedUrl='" + mappedUrl + '\'' +
        ", status=" + status +
        ", message='" + message + '\'' +
        ", date=" + date +
        '}';
  }
}
