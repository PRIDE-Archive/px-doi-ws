package uk.ac.ebi.pride.px.doi.model;

/**
 * This class specifies all the information required when registering a DOI through CrossRef.
 */
public class DoiMetaData {

  private String organization;
  private String email;
  private String title;
  private String dataOwner;
  private String user;
  private String password;

  /**
   * Gets the organization.
   * @return the organization
   */
  public String getOrganization() {
    return organization;
  }

  /**
   * Sets the organization
   * @param organization the organization
   */
  public void setOrganization(String organization) {
    this.organization = organization;
  }

  /**
   * Gets the email address.
   * @return the email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address.
   * @param email the email address.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the title.
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title
   * @param title the title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the data owner.
   * @return the data ownder
   */
  public String getDataOwner() {
    return dataOwner;
  }

  /**
   * Sets the data owner.
   * @param dataOwner the dat owner
   */
  public void setDataOwner(String dataOwner) {
    this.dataOwner = dataOwner;
  }

  /**
   * Gets the user.
   * @return the user.
   */
  public String getUser() {
    return user;
  }

  /**
   * Sets the user.
   * @param user the user
   */
  public void setUser(String user) {
    this.user = user;
  }

  /**
   * Gets the password.
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the password
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }
}

