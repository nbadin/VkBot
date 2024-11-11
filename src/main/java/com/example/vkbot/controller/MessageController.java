package com.example.vkbot.controller;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.vkbot.model.VkObject;
import com.example.vkbot.service.BotServiceRegistry;

@Controller
public class MessageController {
    private final Gson gson;
    private final BotServiceRegistry botServiceRegistry;

    @Value("${vk.api.confirm.code}")
    private String confirmCode;

    public MessageController(BotServiceRegistry botServiceRegistry, Gson gson) {
        this.botServiceRegistry = botServiceRegistry;
        this.gson = gson;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String sendMessage(@RequestBody(required = false) String message) {
        VkObject vkObject = gson.fromJson(message, VkObject.class);
        var service = botServiceRegistry.findSupportService(vkObject);

        if (service != null) {
            service.execute(vkObject);
            return "OK";
        }

        return confirmCode;
    }
}
