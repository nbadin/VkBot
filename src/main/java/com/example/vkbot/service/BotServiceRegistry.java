package com.example.vkbot.service;

import org.springframework.stereotype.Component;
import com.example.vkbot.model.VkObject;
import java.util.List;

@Component
public class BotServiceRegistry {

    private final List<IBotService> botServices;

    public BotServiceRegistry(List<IBotService> botServices) {
        this.botServices = botServices;
    }

    public IBotService findSupportService(VkObject vkObject) {
        return botServices.stream()
                .filter(service -> service.supports(vkObject))
                .findFirst()
                .orElse(null);
    }
}
