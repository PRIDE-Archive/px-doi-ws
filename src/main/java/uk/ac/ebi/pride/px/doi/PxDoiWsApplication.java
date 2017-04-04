package uk.ac.ebi.pride.px.doi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * This class launches the PX DOI WS Spring application, and defines access to it.
 *
 * @author Tobias-Ternent
 */
@SpringBootApplication
public class PxDoiWsApplication {

  /**
   * This method runs the Spring application.
   * @param args command line arguments, unused
   */
  public static void main(String[] args) {
    SpringApplication.run(PxDoiWsApplication.class, args);
  }

  @Configuration
  @EnableGlobalMethodSecurity(prePostEnabled = true)
  @EnableWebSecurity
  static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String PARTNER = "PARTNER";
    @Value("${peptide.atlas.username.property}")
    private String peptideatlasUsername;
    @Value("${peptide.atlas.password.property}")
    private String peptideatlasPassword;
    @Value("${massive.username.property}")
    private String massiveUsername;
    @Value("${massive.password.property}")
    private String massivePassword;
    @Value("${jpost.username.property}")
    private String jpostUsername;
    @Value("${jpost.password.property}")
    private String jpostPassword;
    @Value("${pride.username.property}")
    private String prideUsername;
    @Value("${pride.password.property}")
    private String pridePassword;

    /**
     * This section defines the user accounts which can be used for authentication as well as the roles each user has.
     *
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.inMemoryAuthentication()
          .withUser(peptideatlasUsername).password(peptideatlasPassword).roles(PARTNER)
          .and()
          .withUser(massiveUsername).password(massivePassword).roles(PARTNER)
          .and()
          .withUser(jpostUsername).password(jpostPassword).roles(PARTNER)
          .and()
          .withUser(prideUsername).password(pridePassword).roles(PARTNER)
      ;
    }

    /**
     * This method configures the access to the web service: /registration is accessible by logged-in PARTNERs only,
     * all other paths are denied. Sessions are not created. Cross Site Request Forgery protection is disabled.
     * @param http the http security
     * @throws Exception any exceptions during the http security configuration process
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.httpBasic()
          .and()
          .authorizeRequests()
          .antMatchers(HttpMethod.POST, "/doi-ws/registration/**").hasRole(PARTNER)
          .antMatchers(HttpMethod.POST, "/**").denyAll()
          .and()
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
          .and()
          .csrf().disable()
      ;
    }
  }
}