package com.example.vkbot.service;

import com.example.vkbot.model.VkObject;
import org.springframework.web.client.RestClientException;

public interface IBotService {
    public boolean supports(VkObject vkObject);
    public boolean execute(VkObject vkObject);
}
