package uk.ac.ebi.pride.px.doi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.pride.px.doi.model.DoiRegistration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static uk.ac.ebi.pride.px.doi.model.DoiRegistrationStatus.SUCCESS;

/**
 * This class tests the PX DOI WS to register a DOI for a project.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

  @Value("${pride.username.property}")
  private String prideUsername;
  @Value("${pride.password.property}")
  private String pridePassword;

  @Autowired
  private TestRestTemplate restTemplate;

  /**
   * This method does a test POST to register a DOI, to the test environment.
   * A DOI is not actually registered, but a dummy SUCCESS message is returned.
   */
  @Test
  public void testRegisterDoi() {
    ResponseEntity<DoiRegistration> entity = restTemplate
        .withBasicAuth(prideUsername, pridePassword)
        .postForEntity("/doi-ws/registration/test123/mode/test"
            , ""
            , DoiRegistration.class
        );
    assertThat(SUCCESS.equals(entity.getBody().getStatus()));
    assertThat("SUCCESS".equals(entity.getBody().getMessage()));
  }

  /**
   * This method attempts to use the web service using bad and no login details, which should not be allowed
   * A DOI is not registered.
   */
  @Test
  public void testRegisterDoiFailAuth() {
    ResponseEntity entity = restTemplate
        .withBasicAuth("foo", "bar")
        .getForEntity("/"
            , Object.class
        );
    assertThat(UNAUTHORIZED.equals(entity.getStatusCode()));
    entity = restTemplate
        .getForEntity("/"
            , Object.class
        );
    assertThat(UNAUTHORIZED.equals(entity.getStatusCode()));
  }
}
