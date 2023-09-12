package com.namekart.amp2.Feign;

import com.namekart.amp2.TelegramEntities.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "Telegram", url = "https://api.telegram.org/bot5680459542:AAHSCAxN0WJsVkBfWlhndgfwLSHvT2TKOhM")
public interface Telegram {

    @Async
    @GetMapping("/answerCallbackQuery")
    Object answerCallback(@RequestParam String callback_query_id, @RequestParam String text, @RequestParam boolean show_alert);
    @Async
    @GetMapping("/answerCallbackQuery")
    Object answerCallback(@RequestParam String callback_query_id);

    @Async
    @GetMapping("/sendMessage")
    Object sendAlert(@RequestParam Long chat_id, @RequestParam String text);

    @Async@GetMapping("/sendMessage")
    Object sendAlert(@RequestParam Long chat_id,@RequestParam Long message_thread_id, @RequestParam String text);
    @Async@GetMapping("/sendMessage")
    Object sendAlert(@RequestParam Long chat_id, @RequestParam String text, @RequestParam String parse_mode);
    @Async@GetMapping("/sendMessage")
    Object sendAlert(@RequestParam Long chat_id,@RequestParam Long message_thread_id, @RequestParam String text, @RequestParam String parse_mode);
    @Async@GetMapping("/editmessagereplymarkup")
    Object editMessage(@RequestBody EditMessageReplyMarkup editMessageReplyMarkup);
    @Async@PostMapping("/sendMessage")
    Message sendKeyboard(@RequestBody SendMessage sendMessage);

    @PostMapping("/sendMessage")
    Message sendKeyboardSync(@RequestBody SendMessage sendMessage);

    @PostMapping("/editmessagetext")
    Object editMessageText(@RequestBody EditMessage editMessage);
    @Async@PostMapping("/sendMessage")
    Message sendReplyKeyboard(@RequestBody SendReplyKeyboard sendReplyKeyboard);
}
