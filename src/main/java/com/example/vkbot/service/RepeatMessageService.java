package com.example.vkbot.service;

import com.example.vkbot.model.VkObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.web.client.RestClientException;
import org.springframework.retry.annotation.Recover;

@Slf4j
@Service
public class RepeatMessageService implements IBotService {
    public static final String EVENT_TYPE = "message_new";

    @Value("${vk.api.url.sendmsg}")
    private String vkApiMethod;

    private final RestTemplate restTemplate;

    public RepeatMessageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean supports(VkObject vkObject) {
        return vkObject.getType().equals(EVENT_TYPE);
    }

    @Override
    @Retryable(
            retryFor = {RestClientException.class},
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public boolean execute(VkObject vkObject) {
        try {
            VkObject.ObjectData data = vkObject.getObject();
            VkObject.ObjectData.Message message = (data != null) ? data.getMessage() : null;

            if (message == null || message.getText() == null) {
                return false;
            }

            long userId = message.getFromId();
            long randomId = message.getRandomId();

            String url = UriComponentsBuilder.fromHttpUrl(vkApiMethod)
                    .queryParam("user_id", userId)
                    .build()
                    .toUriString();

            MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
            bodyParams.add("message", "Вы сказали: " + message.getText());
            bodyParams.add("random_id", String.valueOf(randomId));

            ResponseEntity<String> response = restTemplate.postForEntity(url, bodyParams, String.class);

            log.info("Response from VK API: {}", response.getBody());
        } catch (RestClientException e) {
            log.error("Error on attempt {}", e.getMessage());
            throw e;
        }

        return true;
    }

    @Recover
    public boolean recover(RestClientException e) {
        log.error("All retry attempts failed: {}", e.getMessage());
        return false;
    }
}
