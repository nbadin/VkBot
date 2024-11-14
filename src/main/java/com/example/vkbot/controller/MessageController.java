package com.example.vkbot.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.vkbot.model.VkObject;
import com.example.vkbot.service.BotServiceRegistry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Controller
public class MessageController {
    public static final String EVENT_TYPE = "confirmation";

    private final Gson gson;
    private final BotServiceRegistry botServiceRegistry;

    @Value("${vk.api.confirm.code}")
    private String confirmCode;

    private static final ConcurrentMap<String, Boolean> processedEventIds = new ConcurrentHashMap<>();

    public MessageController(BotServiceRegistry botServiceRegistry, Gson gson) {
        this.botServiceRegistry = botServiceRegistry;
        this.gson = gson;
    }

    private boolean isConfirmation(VkObject vkObject) {
        return vkObject.getType().equals(EVENT_TYPE);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String sendMessage(@RequestBody(required = false) String message) {
        VkObject vkObject = gson.fromJson(message, VkObject.class);

        if (isConfirmation(vkObject)) {
            return confirmCode;
        }

        String eventId = vkObject.getEventId();
        if (processedEventIds.putIfAbsent(eventId, true) != null) {
            return "DUPLICATE EVENT";
        }

        var service = botServiceRegistry.findSupportService(vkObject);
        if (service != null) {
            processedEventIds.remove(eventId);
            service.execute(vkObject);
            return "OK";
        }

        return "UNSUPPORTED EVENT";
    }
}
