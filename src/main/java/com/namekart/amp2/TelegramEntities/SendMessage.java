package com.namekart.amp2.TelegramEntities;

public class SendMessage {
    Long chat_id;

    public SendMessage(Long chat_id, Long message_thread_id, String text, InlineKeyboardMarkup reply_markup) {
        this.chat_id = chat_id;
        this.message_thread_id = message_thread_id;
        this.text = text;
        this.reply_markup = reply_markup;
    }

    public Long getMessage_thread_id()
    {
        return message_thread_id;
    }

    public void setMessage_thread_id(Long message_thread_id) {
        this.message_thread_id = message_thread_id;
    }

    Long message_thread_id;
    String text;
    InlineKeyboardMarkup reply_markup;

    public SendMessage(Long chat_id, String text, InlineKeyboardMarkup reply_markup) {
        this.chat_id = chat_id;
        this.text = text;
        this.reply_markup = reply_markup;
    }

    public SendMessage() {
    }

    public Long getChat_id() {
        return chat_id;
    }

    public void setChat_id(Long chat_id) {
        this.chat_id = chat_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InlineKeyboardMarkup getReply_markup() {
        return reply_markup;
    }

    public void setReply_markup(InlineKeyboardMarkup reply_markup) {
        this.reply_markup = reply_markup;
    }
}
