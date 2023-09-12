package com.namekart.amp2.TelegramEntities;

public class Message {
    Long message_id;

    Long message_thread_id;

    public Long getMessage_thread_id() {
        return message_thread_id;
    }

    public void setMessage_thread_id(Long message_thread_id) {
        this.message_thread_id = message_thread_id;
    }

    User from;
    Chat chat;
    Long date;
    String text;

    InlineKeyboardMarkup reply_markup;

    public InlineKeyboardMarkup getReply_markup() {
        return reply_markup;
    }

    public void setReply_markup(InlineKeyboardMarkup reply_markup) {
        this.reply_markup = reply_markup;
    }

    public Message() {
    }

    public Long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Long message_id) {
        this.message_id = message_id;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
