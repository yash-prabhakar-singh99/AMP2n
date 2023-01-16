package com.namekart.amp2.Feign;

import com.namekart.amp2.TelegramEntities.Message;
import com.namekart.amp2.TelegramEntities.SendMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "Telegram", url = "https://api.telegram.org/bot5680459542:AAHSCAxN0WJsVkBfWlhndgfwLSHvT2TKOhM")
public interface Telegram {

    @GetMapping("/answerCallbackQuery")
    Object answerCallback(@RequestParam String callback_query_id, @RequestParam String text, @RequestParam boolean show_alert);
    @GetMapping("/sendMessage")
    Object sendAlert(@RequestParam Long chat_id, @RequestParam String text);

    @PostMapping("/sendMessage")
    Message sendKeyboard(@RequestBody SendMessage sendMessage);
}
