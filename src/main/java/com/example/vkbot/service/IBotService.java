package com.example.vkbot.service;

import com.example.vkbot.model.VkObject;

public interface IBotService {
    public boolean supports(VkObject vkObject);
    public boolean execute(VkObject vkObject);
}
