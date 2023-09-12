package com.namekart.amp2.TelegramEntities;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditMessage {
    String text;
    Long chat_id,message_id;
    InlineKeyboardMarkup reply_markup;

    public EditMessage() {
    }

    public EditMessage(String text, Long chat_id, Long message_id) {
        this.text = text;
        this.chat_id = chat_id;
        this.message_id = message_id;
    }

    public EditMessage(String text, Long chat_id, Long message_id, InlineKeyboardMarkup reply_markup) {
        this.text = text;
        this.chat_id = chat_id;
        this.message_id = message_id;
        this.reply_markup = reply_markup;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
