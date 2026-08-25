package br.com.jhonatan.consumer.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ProviderClientConfig {

    @Bean
    public RestClient providerRestClient(
            @Value("${provider.api.base-url}") String baseUrl,
            @Value("${provider.api.connect-timeout}") int connectTimeout,
            @Value("${provider.api.read-timeout}") int readTimeout) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}