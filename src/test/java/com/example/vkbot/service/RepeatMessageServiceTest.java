package com.example.vkbot.service;

import com.example.vkbot.model.VkObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.junit.jupiter.api.extension.ExtendWith;
import java.lang.reflect.Field;
import org.springframework.web.client.RestClientException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RepeatMessageServiceTest {

    @InjectMocks
    private RepeatMessageService repeatMessageService;

    @Mock
    private RestTemplate restTemplate;

    private VkObject vkObject;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        VkObject.ObjectData.Message message = new VkObject.ObjectData.Message();
        message.setFromId(123L);
        message.setRandomId(456L);
        message.setText("Hello");

        VkObject.ObjectData objectData = new VkObject.ObjectData();
        objectData.setMessage(message);

        vkObject = new VkObject();
        vkObject.setType(RepeatMessageService.EVENT_TYPE);
        vkObject.setObject(objectData);

        Field vkApiMethodField = RepeatMessageService.class.getDeclaredField("vkApiMethod");
        vkApiMethodField.setAccessible(true);
        vkApiMethodField.set(repeatMessageService, "https://api.vk.com/method/messages.send");
    }

    @Test
    void supportsShouldReturnTrueForMessageNewEvent() {
        assertTrue(repeatMessageService.supports(vkObject));
    }

    @Test
    void supportsShouldReturnFalseForOtherEvent() {
        vkObject.setType("message_update");
        assertFalse(repeatMessageService.supports(vkObject));
    }

    @Test
    void executeShouldSendMessageToVKAPI() {
        ResponseEntity<String> responseEntity = ResponseEntity.ok("ok");
        when(restTemplate.postForEntity(anyString(), any(MultiValueMap.class), eq(String.class)))
                .thenReturn(responseEntity);

        boolean result = repeatMessageService.execute(vkObject);

        assertTrue(result);
        verify(restTemplate, times(1)).postForEntity(
                eq("https://api.vk.com/method/messages.send?user_id=123"),
                any(MultiValueMap.class),
                eq(String.class)
        );
    }

    @Test
    void executeShouldReturnFalseIfMessageIsNull() {
        vkObject.getObject().getMessage().setText(null);
        boolean result = repeatMessageService.execute(vkObject);

        assertFalse(result);
    }

    @Test
    void executeShouldRetryIfRestTemplateThrowsException() {
        when(restTemplate.postForEntity(anyString(), any(MultiValueMap.class), eq(String.class)))
                .thenThrow(new RestClientException("Some Error"))
                .thenReturn(ResponseEntity.ok("ok"));

        try {
            repeatMessageService.execute(vkObject);
        } catch (RestClientException e) {
            assertEquals("Some Error", e.getMessage());
            boolean result = repeatMessageService.execute(vkObject);
            assertTrue(result);
        }

        verify(restTemplate, times(2)).postForEntity(anyString(), any(MultiValueMap.class), eq(String.class));
    }
}
