package com.namekart.amp2.TelegramEntities;

public class EditMessageReplyMarkup {
    Long chat_id;
    Long message_id;
    InlineKeyboardMarkup reply_markup;

    public EditMessageReplyMarkup(Long chat_id, Long message_id, InlineKeyboardMarkup reply_markup) {
        this.chat_id = chat_id;
        this.message_id = message_id;
        this.reply_markup = reply_markup;
    }

    public EditMessageReplyMarkup() {
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public Long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Long message_id) {
        this.message_id = message_id;
    }

    public InlineKeyboardMarkup getReply_markup() {
        return reply_markup;
    }

    public void setReply_markup(InlineKeyboardMarkup reply_markup) {
        this.reply_markup = reply_markup;
    }
}
