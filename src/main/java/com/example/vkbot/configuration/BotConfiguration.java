    package com.example.vkbot.configuration;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.client.RestTemplate;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.client.ClientHttpRequestInterceptor;
    import java.util.Collections;

    @Configuration
    public class BotConfiguration {
        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();

            ClientHttpRequestInterceptor headerInterceptor = (request, body, execution) -> {
                HttpHeaders headers = request.getHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                return execution.execute(request, body);
            };

            restTemplate.setInterceptors(Collections.singletonList(headerInterceptor));
            return restTemplate;
        }
    }
