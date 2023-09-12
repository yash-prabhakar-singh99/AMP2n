package com.namekart.amp2.TelegramEntities;

public class SendReplyKeyboard {
    Long chat_id;

    public SendReplyKeyboard(Long chat_id, Long message_thread_id, String text, ReplyKeyboardMarkup reply_markup) {
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
    ReplyKeyboardMarkup reply_markup;

    public SendReplyKeyboard(Long chat_id, String text, ReplyKeyboardMarkup reply_markup) {
        this.chat_id = chat_id;
        this.text = text;
        this.reply_markup = reply_markup;
    }

    public SendReplyKeyboard() {
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

    public ReplyKeyboardMarkup getReply_markup() {
        return reply_markup;
    }

    public void setReply_markup(ReplyKeyboardMarkup reply_markup) {
        this.reply_markup = reply_markup;
    }
}
