package uk.ac.ebi.pride.px.doi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class UtilsConfig {
    @Value("${proxy-host}")
    private String proxyHost;

    @Value("${proxy-port}")
    private Integer proxyPort;

    @Bean("proxyRestTemplate")
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            requestFactory.setProxy(proxy);
        }
        return new RestTemplate(requestFactory);
    }
}
