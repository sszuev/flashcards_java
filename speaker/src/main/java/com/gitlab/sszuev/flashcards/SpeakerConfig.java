package com.gitlab.sszuev.flashcards;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by @ssz on 22.05.2021.
 */
@Configuration
@PropertySource("classpath:speaker.properties")
public class SpeakerConfig {

    @Bean
    public RestTemplate restTemplate(@Value("${app.http-client.connect-timeout:3000}") int connectTimeout,
                                     @Value("${app.http-client.read-timeout:3000}") int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return new RestTemplate(factory);
    }

}
